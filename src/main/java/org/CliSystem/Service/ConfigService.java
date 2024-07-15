package org.CliSystem.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.CliSystem.ModuleObj;
import org.CliSystem.Yaml.ConfigDto;
import org.apache.commons.collections4.SetUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ConfigService {

    public ModuleObj parsePackage(Path tempDir, Map<String, String> config, Map<String, ModuleObj> gitModules) {
        String pack = tempDir + "/package.yml";
        LocalModuleService lms = new LocalModuleService();
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        mapper.findAndRegisterModules();
        try {
            ConfigDto packageConfig = mapper.readValue(new File(pack), ConfigDto.class);
            String name = packageConfig.config().values().toString();
            name = name.substring(1, name.length() - 1);
            if (gitModules.containsKey(name)) {
                Map<String, String> gitConfig = getConfig(gitModules.get(name).script());
                Map<String, String> defaultConfig = combineConfigs(config, gitConfig);
                String script = configToScript(defaultConfig);
                return new ModuleObj(name, script, lms.createMetadata(script));
            } else {
                String script = configToScript(config);
                return new ModuleObj(name, script, lms.createMetadata(script));
            }
        } catch (IOException e) {
            throw new RuntimeException("Can't read yaml file: " + tempDir, e);
        }
    }

    public Map<String, String> getConfig(String script) {
        Map<String, String> configs = new HashMap<>();
        StringBuilder text = new StringBuilder(script);
        String key, value;
        while (text.indexOf("\"") != -1) {
            text.delete(0, text.indexOf("\"") + 1);
            key = text.substring(0, text.indexOf("\""));
            text.delete(0, text.indexOf("\"") + 1);
            text.delete(0, text.indexOf("\"") + 1);
            value = text.substring(0, text.indexOf("\""));
            text.delete(0, text.indexOf("\"") + 1);
            configs.put(key, value);
        }
        return configs;
    }

    public Map<String, String> combineConfigs(Map<String, String> yamlConfig, Map<String, String> gitConfig) {
        Set<String> diffGit = SetUtils.difference(gitConfig.keySet(), yamlConfig.keySet());
        diffGit.forEach(config -> yamlConfig.put(config, gitConfig.get(config)));
        return yamlConfig;
    }

    public String configToScript(Map<String, String> defaultConfig) {
        StringBuilder script = new StringBuilder();
        Set<String> keysConfig = defaultConfig.keySet();
        script.append("config = {");
        keysConfig.forEach(key -> script.append("\n" + "\t" + "\"" + key + "\"" + ":" + " \"" + defaultConfig.get(key) + "\","));
        script.append("\n}");
        return String.valueOf(script);
    }
}
