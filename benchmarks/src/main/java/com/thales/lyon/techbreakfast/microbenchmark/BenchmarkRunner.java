package com.thales.lyon.techbreakfast.microbenchmark;

import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

public class BenchmarkRunner {

	public static void main(String[] args) throws RunnerException {

		Options opt = new OptionsBuilder()
				.include(SerializationBenchmark.class.getSimpleName())
				.resultFormat(ResultFormatType.JSON)
				.result("benchmark-result/" + System.currentTimeMillis() + ".json")
				.build();

		new Runner(opt).run();
	}
	
}