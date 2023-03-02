package com.homevision.api.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class HousesResponse {

    private List<HouseDto> houses;
}
