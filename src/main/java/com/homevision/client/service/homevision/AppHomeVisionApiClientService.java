package com.homevision.client.service.homevision;

import com.homevision.client.api.homevision.vo.HouseVO;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

public interface AppHomeVisionApiClientService {

    List<HouseVO> getHouses(Integer page, Integer perPage);
}
