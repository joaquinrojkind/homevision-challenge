package com.homevision.api.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class HouseDto {

    private Long id;
    private String address;
    private String homeOwner;
    private Double price;
    private String photoURL;
}
