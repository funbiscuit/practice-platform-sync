package org.CliSystem;

import org.apache.commons.codec.digest.DigestUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class LocalModuleService {

    public List<ModuleObj> parseModules(String modulesDir) {
        List<Path> fileList;
        try (Stream<Path> walk = Files.walk(Path.of(modulesDir))) {
            fileList = walk.filter(Files::isRegularFile).filter(path -> path.toString().endsWith(".py")).toList();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        List<ModuleObj> moduleObjs = new ArrayList<>();
        String script;
        for (Path path : fileList) {
            script = pathToScript(path);
            moduleObjs.add(new ModuleObj(pathToName(path), script, createMetadata(script)));
        }
        return moduleObjs;
    }

    private HashMap<String, String> createMetadata(String script) {
        HashMap<String, String> metadata = new HashMap<>();
        metadata.put("CheckSum", DigestUtils.sha3_256Hex(script));
        return metadata;
    }

    private String pathToName(Path path) {
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
            throw new RuntimeException(e);
        }
    }

}
