# Microbenchmark with java
JMH usage example

## Prerequisites
- maven
- JDK 12

## Getting Started
1. Compile sources
```
mvn clean install
```

2. Run the benchmark
```
java -jar target/benchmarks-0.0.1-SNAPSHOT.jar -rf JSON -rff benchmark-result/test-result.json 
```

3. Visualise result

Load json report into : https://jmh.morethan.io/
