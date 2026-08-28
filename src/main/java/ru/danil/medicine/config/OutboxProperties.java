package ru.danil.medicine.config;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@Validated
@Component
@ConfigurationProperties(prefix = "outbox-service")
public class OutboxProperties {
    @NotNull
    @Min(1)
    private Integer limit;

    @NotNull
    @Min(1)
    private Integer retryDelaySeconds;

    @NotNull
    @Min(1)
    private Integer processingLeaseSeconds;

    @NotNull
    @Min(1)
    private Integer maxAttempts;

    @NotNull
    @Min(1)
    private Integer parallelism;
}
