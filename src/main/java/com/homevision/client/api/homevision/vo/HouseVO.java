package com.homevision.client.api.homevision.vo;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class HouseVO {

    private Long id;
    private String address;
    private String homeowner;
    private Double price;
    private String photoURL;
}
