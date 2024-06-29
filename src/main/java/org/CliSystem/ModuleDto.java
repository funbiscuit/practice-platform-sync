package org.CliSystem;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public record ModuleDto(
        String name,
        Map<String, String> metadata
) {
}
