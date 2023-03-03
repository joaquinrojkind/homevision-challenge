package com.homevision.client.service.homevision;

import com.homevision.client.api.homevision.AppHomeVisionApiClient;
import com.homevision.client.api.homevision.vo.HousesResponseVO;
import com.homevision.client.util.resiliency.ResilientCallExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import retrofit2.Call;

@Component
public class AppHomeVisionApiClientServiceImpl implements AppHomeVisionApiClientService {

    @Autowired
    private AppHomeVisionApiClient appHomeVisionApiClient;
    @Autowired
    private ResilientCallExecutor resilientCallExecutor;

    @Override
    public HousesResponseVO getHouses(Integer page, Integer perPage) {
        Call<HousesResponseVO> call = appHomeVisionApiClient.getHouses(page, perPage);
        return resilientCallExecutor.executeCall(call);
    }
}
