### Transaction Agent

#### Usage
* mvn assembly:single
* cd target
* mkdir -p ru/nsu/fit/javaperf/sartakov
* cd ru/nsu/fit/javaperf/sartakov
* cp src/main/java/ru/nsu/fit/javaperf/sartakov/TransactionProcessor.java .
* javac TransactionProcessor
* cd -
* java -javaagent:transaction-agent-1.0-SNAPSHOT-jar-with-dependencies.jar ru.nsu.fit.javaperf.sartakov.TransactionProcessor
