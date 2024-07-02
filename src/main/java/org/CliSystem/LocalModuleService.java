package org.CliSystem;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class LocalModuleService {

    List<ModuleObj> parseModules(String modulesDir) {
        List<Path> fileList;
        try (Stream<Path> walk = Files.walk(Path.of(modulesDir))) {
            fileList = walk.filter(Files::isRegularFile).filter(path -> path.toString().endsWith(".py")).toList();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println(fileList);
        List<ModuleObj> moduleObjs = new ArrayList<>();
        for (Path path : fileList) {
            moduleObjs.add(new ModuleObj(pathToName(path), pathToScript(path), null));
        }
        return moduleObjs;
    }

    private String pathToName(Path path) {
        String sPath = path.toString();
        sPath = sPath.substring(0, sPath.length() - ".py".length());
        if (path.endsWith("__init__")) {
            sPath = sPath.substring(0, sPath.length() - "__init__".length() - 1);
        }
        int index = sPath.indexOf("modules");
        if (index != -1) {
            index += ("modules".length() + 1);
            return sPath.substring(index).replace("\\", ".");
        } else {
            throw new RuntimeException();
        }
    }

    private String pathToScript(Path path) {
        try {
            return new String(Files.readAllBytes(Paths.get(path.toString())));
        } catch (IOException e) {
            return String.valueOf(new RuntimeException());
        }
    }

}
