package com.newland.iotshow;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;

import java.text.SimpleDateFormat;
import java.util.*;

import com.alibaba.fastjson.JSON;
import com.newland.iotshow.conf.PlaceStatusEnum;
import com.newland.iotshow.conf.StaticVariable;
import com.newland.iotshow.model.kafka.AccessParkingLotInfo;
import com.newland.iotshow.model.kafka.Message;
import com.newland.iotshow.model.kafka.ParkingPlaceRealTimeStatus;
import com.newland.iotshow.model.mysql.ParkingLotPlaceRealTimeStatus;
import com.newland.iotshow.service.MysqlService;
import com.newland.iotshow.util.*;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.spark.SparkConf;
import org.apache.spark.TaskContext;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka010.ConsumerStrategies;
import org.apache.spark.streaming.kafka010.HasOffsetRanges;
import org.apache.spark.streaming.kafka010.KafkaUtils;
import org.apache.spark.streaming.kafka010.LocationStrategies;
import org.apache.spark.streaming.kafka010.OffsetRange;

import com.newland.iotshow.conf.Configuration;
import com.newland.iotshow.conf.SqlConstant;
import com.newland.iotshow.model.kafka.Offset;
import com.newland.iotshow.service.OffsetQueryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 实时流处理入口
 *
 * @author Administrator
 *
 */

public class Main implements Tool,Serializable {

	private static final long serialVersionUID = 1L;
	private static final Logger LOG = LoggerFactory.getLogger(Main.class);

	public static void main(String[] args) throws Exception{
		ToolRunner.run(new Main(), args);
	}

	/**
	 * 执行
	 */
	@Override
	public int run(Configuration conf) throws Exception {
		SparkConf sparkConf = new SparkConf();
		// 本地调试
//		sparkConf.setMaster("local[2]").setAppName("Main");

		JavaSparkContext jsc = new JavaSparkContext(sparkConf);
		JavaStreamingContext jssc = new JavaStreamingContext(jsc, Durations.seconds(conf.getDuration()));
		JavaInputDStream<ConsumerRecord<String, String>> stream = null;

		OffsetQueryService offsetQueryService = new OffsetQueryService();
		// 从mysql获取上一次的消费记录
		List<Offset> offsets = offsetQueryService.query(conf.getTopic(), conf.getGroupId());
		// 构建kafka参数
		Map<String, Object> kafkaParams = buildKafkaParameters(conf);
		if (offsets == null || offsets.isEmpty()) {
			LOG.info(String.format("topic[%s] consumer[%s] not found consumer record", conf.getTopic(), conf.getGroupId()));
			stream = KafkaUtils.createDirectStream(jssc, LocationStrategies.PreferConsistent(), ConsumerStrategies.Subscribe(Arrays.asList(conf.getTopic()), kafkaParams));
		} else {
			Map<TopicPartition, Long> fromOffsets = new HashMap<TopicPartition, Long>(16);
			for (Offset offset : offsets) {
				LOG.info(String.format("topic[%s] consumer[%s] partition[%s] offset[%s]", offset.getTopic(), conf.getGroupId(), offset.getPartition(), offset.getOffset()));
				fromOffsets.put(new TopicPartition(offset.getTopic(), offset.getPartition()), offset.getOffset());
			}
			stream = KafkaUtils.createDirectStream(jssc, LocationStrategies.PreferConsistent(), ConsumerStrategies.Assign(fromOffsets.keySet(), kafkaParams, fromOffsets));
		}
		// 处理
		process(conf,stream);

		jssc.start();

		try {
			jssc.awaitTermination();
		} catch (InterruptedException e) {
			LOG.error("awaitTermination", e);
		} finally {
			jssc.stop();
		}

		return 0;
	}

	private void process(Configuration conf,JavaInputDStream<ConsumerRecord<String, String>> stream) {

		final String consumerId = conf.getGroupId();



		stream.foreachRDD(rdd -> {
			OffsetRange[] offsets = ((HasOffsetRanges) rdd.rdd()).offsetRanges();
			for(OffsetRange offset : offsets){
				System.out.println(String.format("prepare process topic[%s] partition[%s] offset[%s]", offset.topic(),offset.partition(),offset.untilOffset()));
			}

			// 获取当前时间(天) 判断日期是否发生变化,用于及时重置flow
			String today1 = new SimpleDateFormat("yyyy-MM-dd").format(System.currentTimeMillis());
			if(today1.compareTo(StaticVariable.day) > 0){
				StaticVariable.day = today1;
				MysqlService mysqlService = new MysqlService();
				try {
					mysqlService.resetParkingFlow();
					LOG.info("update StaticVariable.day to {} in rdd",StaticVariable.day);
				} catch (Exception e){
					e.printStackTrace();
				}
			}

			rdd.foreachPartition(tuples -> {
				final OffsetRange offset = offsets[TaskContext.get().partitionId()];
				System.out.println(String.format("current process topic[%s] partition[%s] offset[%s]", offset.topic(),offset.partition(),offset.untilOffset()));

				/* 业务逻辑 处理一条条数据 */
				MysqlService mysqlService = new MysqlService();
				while(tuples.hasNext()){
					//获取kafka(datareport_park)的数据
					String massageStr = tuples.next().value();
					Message message = JSON.parseObject(massageStr,Message.class);

					/* 车辆进出场 */
					if(message.getReportType().intValue() == 100400){
						for(Object oneData : message.getReportData()){
							AccessParkingLotInfo info = JSON.parseObject(oneData.toString(),AccessParkingLotInfo.class);
							// 获取当前时间(天) 判断该记录是否是当天的
							String today = new SimpleDateFormat("yyyy-MM-dd").format(System.currentTimeMillis());
							if(today.compareTo(StaticVariable.day) > 0){
								StaticVariable.day = today;
								mysqlService.resetParkingFlow();
								LOG.info("update StaticVariable.day to {} in partition",StaticVariable.day);
							}
							if(info.getEnterTime().startsWith(StaticVariable.day) && info.getIsLeft().intValue()==0){

								mysqlService.updateParkingFlow(info.getParkId());
							}

							//数据插入parkinglot_access_record表
							mysqlService.insertParkinglotAccessRecord(info);
						}


					/* 车位实时状态处理 */
					}else if(message.getReportType().intValue() == 100600){
						for(Object oneData : message.getReportData()){
							ParkingPlaceRealTimeStatus info = JSON.parseObject(oneData.toString(),ParkingPlaceRealTimeStatus.class);
							// 查询数据库中的最新数据的时间
							Long mysqlTimeStamp = mysqlService.selectUpdateTimeById(info.getParkId());

							if(mysqlTimeStamp == null){
								LOG.error("timestamp is null, check mysql data of parking id: %s",info.getParkId());

							}else if(mysqlTimeStamp.longValue() < info.getUpdateTime().longValue()){
								// 更新mysql数据

								ParkingLotPlaceRealTimeStatus mysqlInfo = new ParkingLotPlaceRealTimeStatus();
								int totalPlace = (int)mysqlService.selectParkinglotColumn1ByColumn2("total_place","id",info.getParkId());
								float pressure = (float)(info.getParkedPlace()+info.getReservedPlace())/totalPlace;
								//float pressure = (float)(info.getParkedPlace()+info.getReservedPlace())/info.getTotalPlace();

								String status;
								if(pressure >= PlaceStatusEnum.FULL.getPressure()){
									status = PlaceStatusEnum.FULL.getStatus();
								}else if(pressure > PlaceStatusEnum.FEWER.getPressure()){
									status = PlaceStatusEnum.FEWER.getStatus();
								}else {
									status = PlaceStatusEnum.ENOUGH.getStatus();
								}
								// mysqlinfo 用于更新mysql数据
								//mysqlInfo.setTotalPlace(info.getTotalPlace());
								mysqlInfo.setFreePlace(totalPlace-info.getReservedPlace()-info.getParkedPlace());
								mysqlInfo.setReservedPlace(info.getReservedPlace());
								mysqlInfo.setParkedPlace(info.getParkedPlace());
								mysqlInfo.setPressure(pressure);
								mysqlInfo.setStatus(status);
								mysqlInfo.setUpdateTime(info.getUpdateTime());
								mysqlInfo.setId(info.getParkId());

								mysqlService.updateParkingLotPlaceRealTimeStatus(mysqlInfo);
							}

						}
					}
				}

				saveKafkaOffset(offset, consumerId);
			});
		});
	}


	private void saveKafkaOffset(OffsetRange offset, String consumerId) throws Exception {
		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			conn = DBConnectionManager.getInstance().getConnection();
			System.out.println("init dbconnection"+DBConnectionManager.getInstance());
			stmt = conn.prepareStatement(SqlConstant.SQL_UPDATE_OFFSET);
			stmt.setString(1, offset.topic());
			stmt.setInt(2, offset.partition());
			stmt.setString(3, consumerId);
			stmt.setLong(4, offset.untilOffset());
			stmt.execute();
		} catch (Exception e) {
			throw e;
		} finally{
			if(stmt != null){
				stmt.close();
			}
			if(conn != null){
				conn.close();
			}
		}
	}



	/**
	 * 构建kafka参数
	 * @Author sj
	 * @Date 2020/7/28 15:26
	 * @param conf
	 * @return java.util.Map<java.lang.String,java.lang.Object>
	 */
	private Map<String, Object> buildKafkaParameters(Configuration conf) {
		Map<String, Object> kafkaParams = new HashMap<String, Object>(16);
		kafkaParams.put("bootstrap.servers", conf.getBrokers());
		kafkaParams.put("key.deserializer", StringDeserializer.class);
		kafkaParams.put("value.deserializer", StringDeserializer.class);
		kafkaParams.put("auto.offset.reset", "earliest");
		kafkaParams.put("enable.auto.commit", false);
		kafkaParams.put("request.timeout.ms", 100000);
		kafkaParams.put("session.timeout.ms", 10000);
		kafkaParams.put("heartbeat.interval.ms", 6000);
		kafkaParams.put("group.id", conf.getGroupId());
		return kafkaParams;
	}






}
