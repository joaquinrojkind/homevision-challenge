package com.homevision.client.api.homevision;

import com.homevision.client.api.homevision.vo.HouseVO;
import org.springframework.web.bind.annotation.PathVariable;
import retrofit2.Call;
import retrofit2.http.GET;

import java.util.List;

public interface AppHomeVisionApiClient {

    @GET("/houses")
    Call<List<HouseVO>> getHouses(@PathVariable("page") Integer page, @PathVariable("per_page") Integer perPage);
}
