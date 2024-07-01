package org.CliSystem;

import lombok.ToString;

import java.util.Map;

@ToString
public class ModuleDto {
    private String name;
    private Map<String, String> metadata;
}
