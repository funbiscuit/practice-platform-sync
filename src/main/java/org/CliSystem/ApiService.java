package org.CliSystem;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class ApiService{

    public String save(String url, String path){
        Retrofit retrofit = createRetro(url);
        RequestInterface service = retrofit.create(RequestInterface.class);
        Call<ModuleDto> repos = service.saveModules(createModule(path));
        try {
            Response<ModuleDto> response = repos.execute();
            return response.body().toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Что-то пошло не так";
    }

    private ModuleObj createModule(String path){
        return new ModuleObj(pathToName(path),pathToScript(path),null);
    }

    private String pathToScript(String path){
        try {
            return new String(Files.readAllBytes(Paths.get(path)));
        } catch (IOException e) {
            return "Файл пуст!";
        }
    }

    private String pathToName(String path){

        if (path.endsWith(".py")) { path = path.substring(0, path.length() - ".py".length());}
        if (path.endsWith("init")) { path = path.substring(0, path.length() - "init".length() - 1);}
        int index = path.indexOf("modules");
        if (index != -1) {
            return path.substring(index).replace("/", ".");
        } else {
            return "modules." + path.replace("/", ".");
        }
    }

    private Retrofit createRetro(String url){
        return new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

}
