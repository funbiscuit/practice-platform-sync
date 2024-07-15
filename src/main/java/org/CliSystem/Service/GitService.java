package org.CliSystem.Service;

import org.CliSystem.ModuleObj;
import org.CliSystem.Yaml.Package;
import org.apache.commons.io.file.PathUtils;
import org.apache.commons.io.file.StandardDeleteOption;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

public class GitService {

    public Map<String, ModuleObj> parseRepo(Package pac) {
        try {
            Path tempDir = Files.createTempDirectory("platform_sync_");
            Git git = Git.cloneRepository()
                    .setURI(pac.name())
                    .setDirectory(tempDir.toFile())
                    .setBranch(pac.ref().branch())
                    .call();
            git.getRepository().close();
            LocalModuleService localModuleService = new LocalModuleService();
            Map<String, ModuleObj> localModules = localModuleService.parseModules(String.valueOf(tempDir));
            if (pac.config() != null) {
                ConfigService configService = new ConfigService();
                ModuleObj pack = configService.parsePackage(tempDir, pac.config(), localModules);
                if (localModules.containsKey(pack.name())) {
                    ModuleObj toDelete = localModules.get(pack.name());
                    localModules.put(toDelete.name() + "_default", new ModuleObj(toDelete.name() + "_default", toDelete.script(), toDelete.metadata()));
                    localModules.remove(toDelete.name());
                }
                localModules.put(pack.name(), pack);
            }
            PathUtils.deleteDirectory(tempDir, StandardDeleteOption.OVERRIDE_READ_ONLY);
            return localModules;
        } catch (GitAPIException e) {
            throw new RuntimeException("Failed to get repository: " + pac.name(), e);
        } catch (IOException e) {
            throw new RuntimeException("Failed to create or delete temporary folder", e);
        }
    }
}
