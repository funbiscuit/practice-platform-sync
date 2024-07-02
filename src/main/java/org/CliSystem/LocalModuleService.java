package org.CliSystem;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class LocalModuleService {

    List<ModuleObj> parseModules(String modulesDir){
        List<String> fileList = displayDirectory(new File(modulesDir), new ArrayList<>());
        List<ModuleObj> moduleObjs = new ArrayList<>();
        for (String path : fileList) {
            moduleObjs.add(new ModuleObj(pathToName(path),pathToScript(path),null));
        }
        return moduleObjs;
    }

    private String pathToName(String path){

        if (path.endsWith(".py")) { path = path.substring(0, path.length() - ".py".length());}
        if (path.endsWith("__init__")) { path = path.substring(0, path.length() - "__init__".length() - 1);}
        int index = path.indexOf("modules");
        if (index != -1) {
            index += ("modules".length()+1);
            return path.substring(index).replace("\\", ".");
        } else {
            throw new RuntimeException();
        }
    }

    private String pathToScript(String path){
        try {
            return new String(Files.readAllBytes(Paths.get(path)));
        } catch (IOException e) {
            return "Файл пуст!";
        }
    }

    private List<String> displayDirectory(File modulesDir, List<String> paths)
    {
        try {
            File[] files = modulesDir.listFiles();
            for (File file : files) {
                if (file.isDirectory()) {
                    displayDirectory(file, paths);
                }
                else {
                    paths.add(file.getCanonicalPath());
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return paths;
    }
}
