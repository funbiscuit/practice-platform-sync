package org.CliSystem;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public record ModuleDto(
        @JsonProperty(value = "name") String name,
        @JsonProperty(value = "metadata") Map<String, String> metadata
) {
}
