package org.CliSystem.Service;

import org.CliSystem.ModuleObj;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.file.PathUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

public class GitService {

    public Map<String, ModuleObj> cloneRepo(String gitUrl, String branch) {
        try {
            Path tempDir = Files.createTempDirectory("platform_sync_");
            Git git = Git.cloneRepository()
                    .setURI(gitUrl)
                    .setDirectory(tempDir.toFile())
                    .setBranch(branch)
                    .call();
            git.getRepository().close();
            LocalModuleService localModuleService = new LocalModuleService();
            Map<String, ModuleObj> localModules = localModuleService.parseModules(String.valueOf(tempDir));
            FileUtils.deleteDirectory(tempDir.toFile());
            return localModules;
        } catch (GitAPIException e) {
            throw new RuntimeException("Failed to get repository: " + gitUrl, e);
        } catch (IOException e) {
            throw new RuntimeException("Failed to create or delete temporary folder", e);
        }
    }
}
