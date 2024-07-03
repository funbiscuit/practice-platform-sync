package org.CliSystem;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public record ModuleObj(
        @JsonProperty(value = "name") String name,
        @JsonProperty(value = "script") String script,
        @JsonProperty(value = "metadata") Map<String, String> metadata) {
}
