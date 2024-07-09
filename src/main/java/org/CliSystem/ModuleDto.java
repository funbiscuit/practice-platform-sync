package org.CliSystem;

import java.util.Map;

public record ModuleDto(
        String name,
        Map<String, String> metadata
) {
    public String getCheckSum() {
        return metadata.get("CheckSum");
    }
}
