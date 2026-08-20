package ru.danil.medicine.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonValue;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Gets or Sets IdempotencyStatus
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-14T17:44:17.891438500+03:00[Europe/Moscow]")
public enum IdempotencyStatus {
  
  PROCESSING("PROCESSING"),
  
  RETRY("RETRY"),
  
  COMPLETED("COMPLETED"),
  
  FAILED("FAILED");

  private String value;

  IdempotencyStatus(String value) {
    this.value = value;
  }

  @JsonValue
  public String getValue() {
    return value;
  }

  @Override
  public String toString() {
    return String.valueOf(value);
  }

  @JsonCreator
  public static IdempotencyStatus fromValue(String value) {
    for (IdempotencyStatus b : IdempotencyStatus.values()) {
      if (b.value.equals(value)) {
        return b;
      }
    }
    throw new IllegalArgumentException("Unexpected value '" + value + "'");
  }
}

