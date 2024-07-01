package org.CliSystem;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

import java.util.List;

public interface RequestInterface {

    @POST("api/v0/modules")
    Call<ModuleDto> saveModules(@Body ModuleObj moduleObj);
}
