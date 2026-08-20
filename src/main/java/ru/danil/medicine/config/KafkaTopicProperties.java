package ru.danil.medicine.config;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "kafka-topic")
public class KafkaTopicProperties {
    @NotNull
    private Integer replicas;

    @NotNull
    private Integer partitions;

    @NotNull
    private String minInsyncReplicas;

    @NotNull
    private String policyCreatedTopicName;

    @NotNull
    private String PolicyCreatedDlqName;
}
