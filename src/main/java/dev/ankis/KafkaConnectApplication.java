package dev.ankis;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.utils.Exit;
import org.apache.kafka.connect.cli.ConnectDistributed;
import org.apache.kafka.connect.runtime.Connect;
import org.apache.kafka.connect.runtime.WorkerInfo;

import java.io.*;
import java.util.*;

@Slf4j
public class KafkaConnectApplication {
    private static final String KC_PROPERTIES_FILE_PATH = "/Users/a0s0f6i/Dev/Repositories/Personal/kafka-connect-standalone/src/main/resources/distributed-config.properties";

    public static void main(String[] args) throws Exception {
        KafkaConnectApplication app = new KafkaConnectApplication();
        app.runKafkaConnect(app.readProperties());
    }

    private void runKafkaConnect(Map<String, String> workerProps){
        try {
            WorkerInfo initInfo = new WorkerInfo();
            initInfo.logAll();

            ConnectDistributed connectDistributed = new ConnectDistributed();
            Connect connect = connectDistributed.startConnect(workerProps);

            // Shutdown will be triggered by Ctrl-C or via HTTP shutdown request
            connect.awaitStop();
        } catch (Throwable t) {
            log.error("Stopping due to error. ", t);
            Exit.exit(2);
        }
    }

    private Map<String, String> readProperties() {
        String path = Thread.currentThread().getContextClassLoader().getResource("distributed-config.properties").getPath();
        Properties appProps = new Properties();
        try {
            appProps.load(new FileInputStream(path));
        } catch (IOException e) {
            log.error("Error reading properties file from path : {}", path);
        }
        Map<String, String> props = new HashMap<>();
        for (final String name : appProps.stringPropertyNames()) {
            props.put(name, appProps.getProperty(name));
        }
        return props;
    }
}