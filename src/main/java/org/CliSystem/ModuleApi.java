package org.CliSystem;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

import java.util.List;

public interface ModuleApi {

    @POST("api/v0/modules")
    Call<ModuleDto> saveModule(@Body ModuleObj moduleObj);

    @GET("api/v0/modules")
    Call<List<ModuleDto>> getAll();
}
