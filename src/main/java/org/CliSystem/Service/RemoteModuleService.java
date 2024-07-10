package org.CliSystem.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.CliSystem.ModuleApi;
import org.CliSystem.ModuleDto;
import org.CliSystem.ModuleObj;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RemoteModuleService {

    private final ModuleApi service;

    public RemoteModuleService(String url) {
        Retrofit retrofit = createRetro(url);
        service = retrofit.create(ModuleApi.class);
    }

    public Map<String, ModuleDto> getModules() {
        Call<List<ModuleDto>> repos = service.getAll();
        Map<String, ModuleDto> remoteModules = new HashMap<>();
        try {
            Response<List<ModuleDto>> response = repos.execute();
            if (response.body() != null) {
                response.body().forEach(moduleDto -> remoteModules.put(moduleDto.name(), moduleDto));
                return remoteModules;
            }
            return null;
        } catch (IOException e) {
            throw new RuntimeException("Failed to get remote modules", e);
        }
    }

    public void save(ModuleObj module) {
        Call<ModuleDto> repos = service.saveModule(module);
        try {
            Response<ModuleDto> response = repos.execute();
            System.out.println("Save module: " + response.body().name());
        } catch (IOException e) {
            throw new RuntimeException("Failed to save module " + module.name(), e);
        }
    }

    public void delete(String name) {
        Call<Void> repos = service.deleteModule(name);
        try {
            Response<Void> response = repos.execute();
            System.out.println("Delete module: " + name);
        } catch (IOException e) {
            throw new RuntimeException("Delete to save module " + name, e);
        }
    }

    public void update(String name, ModuleObj moduleObj) {
        Call<ModuleDto> repos = service.updateModule(name, moduleObj);
        try {
            Response<ModuleDto> response = repos.execute();
            System.out.println("Update module: " + name);
        } catch (IOException e) {
            throw new RuntimeException("Failed to update module " + name, e);
        }
    }

    private Retrofit createRetro(String url) {
        return new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(JacksonConverterFactory.create(new ObjectMapper()))
                .build();
    }
}
