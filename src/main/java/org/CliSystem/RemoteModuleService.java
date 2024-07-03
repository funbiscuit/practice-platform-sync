package org.CliSystem;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class RemoteModuleService {

    private final Retrofit retrofit;

    public RemoteModuleService(String url) {
        retrofit = createRetro(url);
    }

    public List<ModuleDto> getModules() {
        ModuleApi service = retrofit.create(ModuleApi.class);
        Call<List<ModuleDto>> repos = service.getAll();
        try {
            Response<List<ModuleDto>> response = repos.execute();
            return response.body();
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

    public void save(ModuleObj module) {
        ModuleApi service = retrofit.create(ModuleApi.class);
        Call<ModuleDto> repos = service.saveModule(module);
        try {
            Response<ModuleDto> response = repos.execute();
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

    private Retrofit createRetro(String url) {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(ModuleDto.class, new ModuleDtoDeserializer())
                .create();
        return new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }
}
