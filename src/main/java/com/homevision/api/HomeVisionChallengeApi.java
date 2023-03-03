package com.homevision.api;

import com.homevision.api.dto.HousesResponseDto;
import com.homevision.service.HouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/homevision-challenge")
public class HomeVisionChallengeApi {

    @Autowired
    private HouseService houseService;

    @GetMapping("")
    public ResponseEntity<String> getAppStatus() {
        return ResponseEntity.ok("ok");
    }

    @GetMapping("/houses")
    public ResponseEntity<HousesResponseDto> getHouses() {
        return ResponseEntity.ok(houseService.getHouses());
    }
}
