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

	// Serializing tools
	
	private ObjectMapper objectMapper;

	private Gson gson;

	private Kryo kryo;

	private Cloner cloner;
	
	// Setup methods

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
	
	// Benchmark methods

	@Benchmark
	public void doNothing(Blackhole bh) {
		// Methods without cloning to have reference throughput
		User nonClonedUser = user;
		bh.consume(nonClonedUser);
	}
	
	@Benchmark
	public void clone(Blackhole bh) {
		// Test using the clone methods from pojo
		User clonedUser = (User) user.clone();
		bh.consume(clonedUser);
	}

	@Benchmark
	public void constructor(Blackhole bh) {
		// Test using the pojo's constructor
		bh.consume(new User(user));
	}

	@Benchmark
	public void commonLang3(Blackhole bh) {
		// Test using SerializationUtils.clone from common-lang3
		User clonedUser = SerializationUtils.clone(user);
		bh.consume(clonedUser);
	}

	@Benchmark
	public void commonLang(Blackhole bh) {
		// Test using SerializationUtils.clone from common-lang
		User clonedUser = (User) org.apache.commons.lang.SerializationUtils.clone(user);
		bh.consume(clonedUser);
	}

	@Benchmark
	public void jackson(Blackhole bh)
			throws JsonParseException, JsonMappingException, JsonProcessingException, IOException {
		// Test using serialization + deserialization from Jackson lib
		User clonedUser = objectMapper.readValue(objectMapper.writeValueAsString(user), User.class);
		bh.consume(clonedUser);
	}

	@Benchmark
	public void gson(Blackhole bh) {
		// Test using serialization + deserialization from Google Gson lib
		User clonedUser = gson.fromJson(gson.toJson(user), User.class);
		bh.consume(clonedUser);
	}

	@Benchmark
	public void kryo(Blackhole bh) {
		// Test using copy from Kryo lib
		User clonedUser = kryo.copy(user);
		bh.consume(clonedUser);
	}

	@Benchmark
	public void cloningLib(Blackhole bh) {
		// Test using deepcopy specialized lib from Github
		User clonedUser = cloner.deepClone(user);
		bh.consume(clonedUser);
	}

}