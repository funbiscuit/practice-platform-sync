package org.CliSystem;

import picocli.CommandLine;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.List;

public class ApiService implements Callback<List<ApiDto>> {

    public void save(String url, String path){
        Retrofit retrofit = createRetro(url);
        RequestInterface service = retrofit.create(RequestInterface.class);
        Call<List<ApiDto>> repos = service.saveModules(createDto(path));
        repos.enqueue(this);
    }

    private Retrofit createRetro(String url){
        return new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }
    // TODO добавить metadata и script
    private ApiDto createDto(String path){
        return ApiDto.builder()
                .name(path)
                .build();
    }

    // TODO сделать нормальную обработку на положительный запрос
    @Override
    public void onResponse(Call<List<ApiDto>> call, Response<List<ApiDto>> response) {
        if(response.isSuccessful()) {
            System.out.println("Succesfully!!!");
        }
        else {
            System.out.println(response.errorBody());
        }
    }

    @Override
    public void onFailure(Call<List<ApiDto>> call, Throwable throwable) {
        System.out.println("Some problems...");
    }
}
