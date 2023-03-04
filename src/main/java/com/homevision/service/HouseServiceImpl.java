package com.homevision.service;

import com.homevision.api.dto.HouseDto;
import com.homevision.api.dto.HousesResponseDto;
import com.homevision.client.api.homevision.AppHomeVisionApiClient;
import com.homevision.client.api.homevision.vo.HouseVO;
import com.homevision.client.api.homevision.vo.HousesResponseVO;
import com.homevision.client.util.parallelism.ParallelTaskRunner;
import com.homevision.client.util.resiliency.ResilientCallExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import retrofit2.Call;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class HouseServiceImpl implements HouseService {

    @Value("${client.app-homevision-staging.pageCount}")
    private Integer pageCount;

    @Value("${client.app-homevision-staging.size}")
    private Integer size;

    @Autowired
    private AppHomeVisionApiClient appHomeVisionApiClient;
    @Autowired
    private ResilientCallExecutor resilientCallExecutor;
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

        /**
         * The project's requirements state that the first 10 pages of the houses API must be requested, but there's
         * no specification as to how this needs to work. For standard pagination the client of our API would indicate
         * a page number and page size and the backend would invoke the houses API accordingly passing the given params.
         * So in order to get the first 10 pages with size 10 the client would make 10 calls to our API.
         *
         * For the sake of the exercise and to make the code a bit more interesting I decided that our new API endpoint
         * does not take any parameter and for a single call the backend always requests 10 pages with size 10 to the
         * houses API and then processes the bulk altogether. Since I built a small REST application instead of a plain script
         * this approach does not necessarily make sense in a real world scenario but should fit the project's requirements.
         */

        List<HouseDto> houses = new ArrayList<>();
        for (int currentPage = 1; currentPage <= pageCount; currentPage++) {
            Call<HousesResponseVO> call = appHomeVisionApiClient.getHouses(currentPage, size);
            houses.addAll(
                resilientCallExecutor.executeCall(call).getHouses().stream()
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

