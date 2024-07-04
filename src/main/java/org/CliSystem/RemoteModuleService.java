package org.CliSystem;

import com.fasterxml.jackson.databind.ObjectMapper;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.IOException;
import java.util.List;

public class RemoteModuleService {

    private final ModuleApi service;

    public RemoteModuleService(String url) {
        Retrofit retrofit = createRetro(url);
        service = retrofit.create(ModuleApi.class);
    }

    public List<ModuleDto> getModules() {
        Call<List<ModuleDto>> repos = service.getAll();
        try {
            Response<List<ModuleDto>> response = repos.execute();
            return response.body();
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

    public void save(ModuleObj module) {
        Call<ModuleDto> repos = service.saveModule(module);
        try {
            Response<ModuleDto> response = repos.execute();
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

    public void delete(String name) {
        Call<Void> repos = service.deleteModule(name);
        try {
            Response<Void> response = repos.execute();
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

    public void update(String name, ModuleObj moduleObj) {
        Call<ModuleDto> repos = service.updateModule(name, moduleObj);
        try {
            Response<ModuleDto> response = repos.execute();
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

    private Retrofit createRetro(String url) {
        return new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(JacksonConverterFactory.create(new ObjectMapper()))
                .build();
    }
}
