package ru.danil.medicine.config;

import lombok.Getter;
import lombok.Setter;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

import java.util.Map;

@Configuration
@Getter
@Setter
@ConfigurationProperties(prefix = "kafka-topic")
public class KafkaConfig {
    private Integer replicas;
    private Integer partitions;
    private String minInsyncReplicas;

    @Bean
    NewTopic createTopic(){
        return TopicBuilder
                .name("policy-created-dlq")
                .partitions(partitions)
                .replicas(replicas)
                .configs(Map.of("min.insync.replicas", minInsyncReplicas))
                .build();
    }
}
