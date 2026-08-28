package ru.danil.medicine.config;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class KafkaConfig {
    private final KafkaTopicProperties kafkaTopicProperties;

    @Bean
    NewTopic createPolicyCreated(){
        return TopicBuilder
                .name(kafkaTopicProperties.getPolicyCreatedTopicName())
                .partitions(kafkaTopicProperties.getPartitions())
                .replicas(kafkaTopicProperties.getReplicas())
                .configs(Map.of("min.insync.replicas", kafkaTopicProperties.getMinInsyncReplicas()))
                .build();
    }

    @Bean
    NewTopic createPolicyCreatedDlq(){
        return TopicBuilder
                .name(kafkaTopicProperties.getPolicyCreatedDlqName())
                .partitions(kafkaTopicProperties.getPartitions())
                .replicas(kafkaTopicProperties.getReplicas())
                .configs(Map.of("min.insync.replicas", kafkaTopicProperties.getMinInsyncReplicas()))
                .build();
    }
}
