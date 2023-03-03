package com.homevision.api.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class HousesResponseDto {

    private List<HouseDto> houses;
}
