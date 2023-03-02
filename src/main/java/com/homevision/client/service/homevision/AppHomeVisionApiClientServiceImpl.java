package com.homevision.client.service.homevision;

import com.homevision.client.api.homevision.AppHomeVisionApiClient;
import com.homevision.client.api.homevision.vo.HouseVO;
import org.springframework.stereotype.Component;
import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;
import java.util.List;

@Component
public class AppHomeVisionApiClientServiceImpl implements AppHomeVisionApiClientService {

    private AppHomeVisionApiClient appHomeVisionApiClient;

    public AppHomeVisionApiClientServiceImpl(AppHomeVisionApiClient appHomeVisionApiClient) {
        this.appHomeVisionApiClient = appHomeVisionApiClient;
    }

    @Override
    public List<HouseVO> getHouses(Integer page, Integer perPage) {
        Call<List<HouseVO>> call = appHomeVisionApiClient.getHouses(page, perPage);
        Response<List<HouseVO>> response;
        List<HouseVO> houses;
        try {
            response = call.execute();
            houses = response.body();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return houses;
    }
}
