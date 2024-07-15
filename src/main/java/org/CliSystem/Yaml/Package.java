package org.CliSystem.Yaml;

import java.util.Map;

public record Package(String name, Ref ref, Map<String, String> config) {
}
