package com.homevision.service;

import com.homevision.api.dto.HouseDto;
import com.homevision.api.dto.HousesResponseDto;
import com.homevision.client.api.homevision.AppHomeVisionApiClient;
import com.homevision.client.api.homevision.vo.HouseVO;
import com.homevision.client.api.homevision.vo.HousesResponseVO;
import com.homevision.client.util.concurrency.ParallelTaskRunner;
import com.homevision.client.util.resiliency.ResilientCallExecutor;
import com.homevision.service.exception.DownloadPhotoException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import retrofit2.Call;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class HouseServiceImpl implements HouseService {

    @Value("${client.app-homevision-staging.pageCount}")
    private Integer pageCount;

    @Value("${client.app-homevision-staging.pageSize}")
    private Integer pageSize;

    @Autowired
    private AppHomeVisionApiClient appHomeVisionApiClient;
    @Autowired
    private ResilientCallExecutor resilientCallExecutor;
    @Autowired
    private ParallelTaskRunner parallelTaskRunner;

    @Override
    public HousesResponseDto getHouses() {
        List<HouseDto> houses = new ArrayList<>();
        for (int currentPage = 1; currentPage <= pageCount; currentPage++) {
            Call<HousesResponseVO> call = appHomeVisionApiClient.getHouses(currentPage, pageSize);
            houses.addAll(
                resilientCallExecutor.executeCall(call).getHouses().stream()
                            .map(this::toHouseDto)
                            .collect(Collectors.toList())
            );
        }
        List<Runnable> runnables = houses.stream()
            .map(house -> (Runnable) () -> this.downloadPhoto(house))
            .collect(Collectors.toList());

        parallelTaskRunner.runAll(runnables);

        return HousesResponseDto.builder()
                .houses(houses)
                .build();
    }

    private void downloadPhoto(HouseDto house) {
        try {
            FileUtils.copyURLToFile(
                    new URL(house.getPhotoURL()),
                    new File(String.format("src/main/resources/photos/%s-%s.jpg", house.getId(), house.getAddress())));
        } catch (IOException e) {
            log.error("Error while downloading photo from url %s, house id %d, exception: %s", house.getPhotoURL(), house.getId(), e);
            throw DownloadPhotoException.builder()
                    .photoUrl(house.getPhotoURL())
                    .build();
        }
    }

    private HouseDto toHouseDto(HouseVO houseVO) {
        return HouseDto.builder()
                .id(houseVO.getId())
                .address(houseVO.getAddress())
                .homeOwner(houseVO.getHomeowner())
                .price(houseVO.getPrice())
                .photoURL(houseVO.getPhotoURL())
                .build();
    }
}

