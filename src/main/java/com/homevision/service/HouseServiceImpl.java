package com.homevision.service;

import com.homevision.api.dto.HouseDto;
import com.homevision.api.dto.HousesResponseDto;
import com.homevision.client.api.homevision.vo.HouseVO;
import com.homevision.client.service.homevision.AppHomeVisionApiClientService;
import com.homevision.client.util.parallel.ParallelTaskRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class HouseServiceImpl implements HouseService {

    @Value("${client.app-homevision-staging.page-count}")
    private Integer pageCount;

    @Value("${client.app-homevision-staging.size}")
    private Integer size;

    @Autowired
    public AppHomeVisionApiClientService appHomeVisionApiClientService;

    @Autowired
    private ParallelTaskRunner parallelTaskRunner;

    @Override
    public HousesResponseDto getHouses() {

//        List<Supplier<MailchimpContactVO>> suppliers = mailchimpContactsIn.stream()
//                .map(contactVO -> (Supplier<MailchimpContactVO>) () -> mailchimpClientService.addContact(listId, contactVO))
//                .collect(Collectors.toList());
//
//        SupplyAllResponse<MailchimpContactVO> response = parallelTaskRunner.supplyAllAndGetExceptions(suppliers);
//
//        List<MailchimpContactVO> syncedContacts = response.getSuccessResponses().stream()
//                .filter(Objects::nonNull)
//                .collect(Collectors.toList());
//
//        return ContactsSyncResponse.builder()
//                .totalSyncedContacts(syncedContacts.size())
//                .contacts(syncedContacts.stream()
//                        .map(this::toContactDto)
//                        .collect(Collectors.toList()))
//                .build();

        List<HouseDto> houses = new ArrayList<>();
        for (int i = 1; i <= pageCount; i++) {
            houses.addAll(
                    appHomeVisionApiClientService.getHouses(i, size).getHouses().stream()
                            .map(this::toHouseDto)
                            .collect(Collectors.toList())
            );
        }
        return HousesResponseDto.builder()
                .houses(houses)
                .build();
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

