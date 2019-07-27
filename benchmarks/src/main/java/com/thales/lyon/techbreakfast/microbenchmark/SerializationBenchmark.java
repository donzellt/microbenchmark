package com.thales.lyon.techbreakfast.microbenchmark;

import java.io.IOException;

import org.apache.commons.lang3.SerializationUtils;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;

import com.esotericsoftware.kryo.Kryo;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.rits.cloning.Cloner;
import com.thales.lyon.techbreakfast.microbenchmark.pojo.Address;
import com.thales.lyon.techbreakfast.microbenchmark.pojo.User;

@BenchmarkMode(Mode.Throughput)
@State(Scope.Benchmark)
@Fork(value = 2)
@Warmup(iterations = 2, time = 5)
@Measurement(iterations = 5, time = 5)
public class SerializationBenchmark {

	// The POJO to be cloned
	private User user;

	// Serializer tools
	private ObjectMapper objectMapper;

	private Gson gson;

	private Kryo kryo;

	private Cloner cloner;

	@Setup(Level.Invocation)
	public void createPojo() {
		Address address = new Address("Downing St 10", "London", "England");
		user = new User("Prime", "Minister", address);
	}

	@Setup(Level.Trial)
	public void setUpSerializer() {
		objectMapper = new ObjectMapper();
		gson = new Gson();
		kryo = new Kryo();
		kryo.register(User.class);
		kryo.register(Address.class);
		cloner = new Cloner();
	}

	@Benchmark
	public void benchDoNothing(Blackhole bh) {
		// Do nothing
		bh.consume(null);
	}

	@Benchmark
	public void benchLang3SerializationUtils(Blackhole bh) {
		bh.consume(SerializationUtils.clone(user));
	}

	@Benchmark
	public void benchLangSerializationUtils(Blackhole bh) {
		bh.consume(org.apache.commons.lang.SerializationUtils.clone(user));
	}

	@Benchmark
	public void benchClone(Blackhole bh) {
		bh.consume((User) user.clone());
	}

	@Benchmark
	public void benchConstructor(Blackhole bh) {
		bh.consume(new User(user));
	}

	@Benchmark
	public void benchJackson(Blackhole bh)
			throws JsonParseException, JsonMappingException, JsonProcessingException, IOException {
		bh.consume(objectMapper.readValue(objectMapper.writeValueAsString(user), User.class));
	}

	@Benchmark
	public void benchGson(Blackhole bh) {
		bh.consume(gson.fromJson(gson.toJson(user), User.class));
	}

	@Benchmark
	public void benchKryo(Blackhole bh) {
		bh.consume(kryo.copy(user));
	}

	@Benchmark
	public void benchCloningLib(Blackhole bh) {
		bh.consume(cloner.deepClone(user));
	}

}