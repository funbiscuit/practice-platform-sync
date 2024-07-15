package org.CliSystem.Service;

import org.CliSystem.ModuleObj;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class LocalModuleService {

    public Map<String, ModuleObj> parseModules(String modulesDir) {
        List<Path> fileList;
        try (Stream<Path> walk = Files.walk(Path.of(modulesDir))) {
            fileList = walk.filter(Files::isRegularFile).filter(path -> path.toString().endsWith(".py")).toList();
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse local modules in " + modulesDir, e);
        }
        Map<String, ModuleObj> remoteModules = new HashMap<>();
        String script;
        ModuleObj moduleObj;
        for (Path path : fileList) {
            script = pathToScript(path);
            moduleObj = new ModuleObj(pathToName(path), script, createMetadata(script));
            remoteModules.put(moduleObj.name(), moduleObj);
        }
        return remoteModules;
    }

    public Map<String, String> createMetadata(String script) {
        Map<String, String> metadata = new HashMap<>();
        metadata.put("CheckSum", DigestUtils.sha3_256Hex(script));
        return metadata;
    }

    public String pathToName(Path path) {
        List<String> elements = StreamSupport.stream(path.spliterator(), false)
                .map(Path::toString)
                .toList();
        String sPath = String.join(".", elements.subList(elements.indexOf("modules") + 1, elements.size()));
        sPath = sPath.substring(0, sPath.length() - ".py".length());
        if (sPath.endsWith("__init__")) {
            sPath = sPath.substring(0, sPath.length() - "__init__".length() - 1);
        }
        return sPath;
    }

    private String pathToScript(Path path) {
        try {
            return new String(Files.readAllBytes(Paths.get(path.toString())));
        } catch (IOException e) {
            throw new RuntimeException("Failed to get script from " + path, e);
        }
    }

}
