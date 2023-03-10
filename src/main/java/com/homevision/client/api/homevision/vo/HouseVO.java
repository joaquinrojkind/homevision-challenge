package com.homevision.client.api.homevision.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class HouseVO {

    private Long id;
    private String address;
    private String homeowner;
    private Double price;
    private String photoURL;
}
