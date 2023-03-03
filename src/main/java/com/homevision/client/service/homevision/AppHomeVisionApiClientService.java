package com.homevision.client.service.homevision;

import com.homevision.client.api.homevision.vo.HousesResponseVO;

public interface AppHomeVisionApiClientService {

    HousesResponseVO getHouses(Integer page, Integer perPage);
}
