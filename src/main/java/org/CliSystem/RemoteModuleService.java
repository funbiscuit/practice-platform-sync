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

    private final String url;

    public RemoteModuleService(String url) {
        this.url = url;
    }

    public List<ModuleDto> getModules() {
        Retrofit retrofit = createRetro(url);
        ModuleApi service = retrofit.create(ModuleApi.class);
        Call<List<ModuleDto>> repos = service.checkModules();
        try {
            Response<List<ModuleDto>> response = repos.execute();
            return response.body();
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

    public void saveAllNewModules(String path) {
        LocalModuleService localModuleService = new LocalModuleService();
        List<ModuleObj> localModules = localModuleService.parseModules(path);
        List<ModuleObj> moduleObjs = filterNotInLocal(getModules(), localModules);
        moduleObjs.forEach(this::save);
    }

    private void save(ModuleObj module) {
        Retrofit retrofit = createRetro(url);
        ModuleApi service = retrofit.create(ModuleApi.class);
        Call<ModuleDto> repos = service.saveModule(module);
        try {
            Response<ModuleDto> response = repos.execute();
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

    private List<ModuleObj> filterNotInLocal(List<ModuleDto> moduleDtos, List<ModuleObj> moduleObjs) {

        if (moduleDtos.isEmpty()) {
            return moduleObjs;
        }

        Set<String> dtoNames = moduleDtos.stream()
                .map(ModuleDto::name)
                .collect(Collectors.toSet());

        return moduleObjs.stream()
                .filter(moduleObj -> !dtoNames.contains(moduleObj.name()))
                .collect(Collectors.toList());
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
