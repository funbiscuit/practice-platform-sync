package org.CliSystem;

import java.util.Map;

public record ModuleObj(
        String name,
        String script,
        Map<String, String> metadata) {

    public String getCheckSum() {
        return metadata.get("CheckSum");
    }
}
