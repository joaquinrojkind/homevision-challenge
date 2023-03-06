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

    /**
     * Page count and page size as per requirements. These hardcoded values would normally
     * be sent by the client calling our API. However, I'm strictly sticking to the requirements
     * which indicates 10 pages of 10 houses each in a single execution.
     */
    public static final Integer PAGE_COUNT = 10;
    public static final Integer PAGE_SIZE = 10;
    public static final String PHOTO_FILE_EXTENSION = "jpg";

    @Autowired
    private AppHomeVisionApiClient appHomeVisionApiClient;
    @Autowired
    private ResilientCallExecutor resilientCallExecutor;
    @Autowired
    private ParallelTaskRunner parallelTaskRunner;

    @Override
    public HousesResponseDto getHouses() {
        List<HouseDto> houses = new ArrayList<>();
        for (int currentPage = 1; currentPage <= PAGE_COUNT; currentPage++) {
            // build retrofit calls to get houses
            Call<HousesResponseVO> call = appHomeVisionApiClient.getHouses(currentPage, PAGE_SIZE);
            houses.addAll(
                // execute calls and add results with houses
                resilientCallExecutor.executeCall(call).getHouses().stream()
                            .map(this::toHouseDto)
                            .collect(Collectors.toList())
            );
        }
        // build runnable list to download photos in parallel
        List<Runnable> runnables = houses.stream()
            .map(house -> (Runnable) () -> this.downloadPhoto(house))
            .collect(Collectors.toList());

        // run all runnables and download photos in parallel
        parallelTaskRunner.runAll(runnables);

        // return response with houses
        return HousesResponseDto.builder()
                .houses(houses)
                .build();
    }

    private void downloadPhoto(HouseDto house) {
        try {
            FileUtils.copyURLToFile(
                    new URL(house.getPhotoURL()),
                    new File(String.format("src/main/resources/photos/%s-%s.%s", house.getId(), house.getAddress(), PHOTO_FILE_EXTENSION)));
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

