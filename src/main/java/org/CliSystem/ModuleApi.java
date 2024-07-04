package org.CliSystem;

import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;

public interface ModuleApi {

    @POST("api/v0/modules")
    Call<ModuleDto> saveModule(@Body ModuleObj moduleObj);

    @GET("api/v0/modules")
    Call<List<ModuleDto>> getAll();

    @DELETE("api/v0/modules/{moduleName}")
    Call<Void> deleteModule(@Path("moduleName") String name);

    @PUT("api/v0/modules/{moduleName}")
    Call<ModuleDto> updateModule(@Path("moduleName") String name, @Body ModuleObj moduleObj);
}
