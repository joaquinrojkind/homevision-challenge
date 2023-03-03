package com.homevision.client.api.homevision;

import com.homevision.client.api.homevision.vo.HousesResponseVO;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface AppHomeVisionApiClient {

    @GET("houses")
    Call<HousesResponseVO> getHouses(@Query("page") Integer page, @Query("per_page") Integer perPage);
}
