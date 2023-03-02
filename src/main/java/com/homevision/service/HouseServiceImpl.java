package com.homevision.service;

import com.homevision.api.dto.HousesResponse;
import com.homevision.client.api.homevision.AppHomeVisionApiClient;
import com.homevision.client.util.parallel.ParallelTaskRunner;
import com.trio.api.dto.ContactDto;
import com.trio.api.dto.ContactsSyncResponse;
import com.trio.client.api.mailchimp.vo.MailchimpContactVO;
import com.trio.client.util.parallel.response.SupplyAllResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Service
public class HouseServiceImpl implements HouseService {

    @Value("${client.app-homevision-staging.page-count}")
    private Integer pageCount;

    @Value("${client.app-homevision-staging.size}")
    private Integer size;

    @Autowired
    public AppHomeVisionApiClient appHomeVisionApiClientService;

    @Autowired
    private ParallelTaskRunner parallelTaskRunner;

    @Override
    public HousesResponse getHouses() {

        List<Supplier<MailchimpContactVO>> suppliers = mailchimpContactsIn.stream()
                .map(contactVO -> (Supplier<MailchimpContactVO>) () -> mailchimpClientService.addContact(listId, contactVO))
                .collect(Collectors.toList());

        SupplyAllResponse<MailchimpContactVO> response = parallelTaskRunner.supplyAllAndGetExceptions(suppliers);

        List<MailchimpContactVO> syncedContacts = response.getSuccessResponses().stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return ContactsSyncResponse.builder()
                .totalSyncedContacts(syncedContacts.size())
                .contacts(syncedContacts.stream()
                        .map(this::toContactDto)
                        .collect(Collectors.toList()))
                .build();
    }

    private ContactDto toContactDto(MailchimpContactVO mailchimpContactVO) {
        return ContactDto.builder()
                .firstName(mailchimpContactVO.getFullNameVO().getFirstName())
                .lastName(mailchimpContactVO.getFullNameVO().getLastName())
                .email(mailchimpContactVO.getEmailAddress())
                .build();
    }
}

