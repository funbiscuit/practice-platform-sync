package org.CliSystem.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.CliSystem.ModuleObj;
import org.CliSystem.Yaml.ConfigDto;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
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
            String script;
            if (gitModules.containsKey(name)) {
                script = String.valueOf(new StringBuilder().append("from modules.").append(name).append("_default").append(" import config as config_default\n\n")
                        .append("def merge(a, b):\n\t")
                        .append("if isinstance(a, dict) and isinstance(b, dict):\n\t\t")
                        .append("a_and_b = a.keys() & b.keys()\n\t\t")
                        .append("all_keys = a.keys() | b.keys()\n\t\t")
                        .append("return {k: merge(a[k], b[k]) if k in a_and_b else deepcopy(a[k] if k in a else b[k]) for k in all_keys}\n\t")
                        .append("return deepcopy(b)\n\n")
                        .append(configToScript(config))
                        .append("\n\nconfig = merge(config_default, config)"));
                System.out.println(script);
            } else {
                script = configToScript(config);
            }
            return new ModuleObj(name, script, lms.createMetadata(script));
        } catch (IOException e) {
            throw new RuntimeException("Can't read yaml file: " + tempDir, e);
        }
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
