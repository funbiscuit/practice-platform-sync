package org.CliSystem.Service;

import org.CliSystem.ModuleObj;
import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class GitService {

    public Map<String, ModuleObj> cloneRepo(String gitUrl, String branch, String path) {
        File tempDir = new File(path + "_temp");
        try {
            Git git = Git.cloneRepository()
                    .setURI(gitUrl)
                    .setDirectory(tempDir)
                    .setBranch(branch)
                    .call();
            git.getRepository().close();
            LocalModuleService localModuleService = new LocalModuleService();
            Map<String, ModuleObj> localModules = localModuleService.parseModules(String.valueOf(tempDir));
            FileUtils.deleteDirectory(tempDir);
            return localModules;
        } catch (GitAPIException e) {
            throw new RuntimeException("Failed to get repository: " + gitUrl, e);
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete temporary folder: " + tempDir, e);
        }
    }
}
