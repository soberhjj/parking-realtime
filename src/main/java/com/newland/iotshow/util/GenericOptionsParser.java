package com.newland.iotshow.util;

import java.io.IOException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.newland.iotshow.conf.Configuration;

public class GenericOptionsParser {

	private static final Logger LOG = LoggerFactory
			.getLogger(GenericOptionsParser.class);

	private Configuration conf;

	public GenericOptionsParser(String[] args)
			throws Exception {
		this.conf = new Configuration();
		parseGeneralOptions(args);
	}

	public Configuration getConf(){
		return this.conf;
	}
	
	private void parseGeneralOptions(String[] args) throws Exception {
		Options opts = buildGeneralOptions();
		CommandLineParser parser = new PosixParser();
		try {
			CommandLine commandLine = parser.parse(opts, args);
			processGeneralOptions(commandLine);
		} catch (Exception e) {
			LOG.warn("options parsing failed: " + e.getMessage());
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp(" dashboard-realtime.jar -duration <duration>  -brokers <brokers> -topic <topic> -consumer <group.id> ", opts);
			System.exit(1);
		}
	}

	/**
	 * 构建输入参数
	 * 
	 * @return
	 */
	private Options buildGeneralOptions() {
		Option duration = OptionBuilder.withArgName("batch duration").hasArg()
				.withDescription("specify batch duration(unit:second)")
				.create("duration");

		Option brokers = OptionBuilder
				.withArgName("comma separated list of brokers")
				.hasArg()
				.withDescription(
						"specify comma separated files to be copied to the map reduce cluster")
				.create("brokers");

		Option topic = OptionBuilder.withArgName("kafka topic name").hasArg()
				.withDescription("specify kafka topic name").create("topic");

		Option groupId = OptionBuilder.withArgName("kafka consumer group id")
				.hasArg().withDescription("specify kafka consumer group id")
				.create("consumer");

		Options opt = new Options();
		opt.addOption(duration);
		opt.addOption(brokers);
		opt.addOption(topic);
		opt.addOption(groupId);
		return opt;
	}

	/**
	 * 提取参数
	 * @param line
	 * @throws IOException
	 */
	private void processGeneralOptions(CommandLine line)
			throws IOException {
		if (line.hasOption("duration")) {
			this.conf.setDuration(Long.parseLong(line.getOptionValue("duration")));
		}else{
			throw new IllegalArgumentException("please specify batch duration");
		}
		if (line.hasOption("brokers")) {
			this.conf.setBrokers(line.getOptionValue("brokers"));
		}else{
			throw new IllegalArgumentException("please specify brokers");
		}
		if (line.hasOption("topic")) {
			this.conf.setTopic(line.getOptionValue("topic"));
		}else{
			throw new IllegalArgumentException("please specify topic");
		}
		if (line.hasOption("consumer")) {
			this.conf.setGroupId(line.getOptionValue("consumer"));
		}else{
			throw new IllegalArgumentException("please specify group.id");
		}
	}
}
