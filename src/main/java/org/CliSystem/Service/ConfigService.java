package org.CliSystem.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.CliSystem.ModuleObj;
import org.CliSystem.Yaml.PackageDef;

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
            PackageDef packageDef = mapper.readValue(new File(pack), PackageDef.class);
            String name = packageDef.config().module();
            String script;
            if (gitModules.containsKey(name)) {
                script = "from modules." + name +"_default import config as config_default" + """
                        
                        def merge(a, b):
                          if isinstance(a, dict) and isinstance(b, dict):
                            a_and_b = a.keys() & b.keys()
                            all_keys = a.keys() | b.keys()
                            return {k: merge(a[k], b[k]) if k in a_and_b else deepcopy(a[k] if k in a else b[k]) for k in all_keys}
                          return deepcopy(b)
                            
                        """
                        + configToScript(config)
                        +"\n\nconfig = merge(config_default, config)";
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
        keysConfig.forEach(key -> script.append("\n" + "  " + "\"" + key + "\"" + ":" + " \"" + defaultConfig.get(key) + "\","));
        script.append("\n}");
        return String.valueOf(script);
    }
}
