package org.CliSystem;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public record ModuleObj(
        String name,
        String script,
        Map<String, String> metadata) {
}
