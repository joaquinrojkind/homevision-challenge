package com.homevision.service;

import com.homevision.api.dto.HousesResponseDto;
import com.homevision.client.api.homevision.AppHomeVisionApiClient;
import com.homevision.client.api.homevision.vo.HouseVO;
import com.homevision.client.api.homevision.vo.HousesResponseVO;
import com.homevision.client.util.concurrency.ParallelTaskRunner;
import com.homevision.client.util.resiliency.ResilientCallExecutor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class HouseServiceImplTest {

    @Mock
    private AppHomeVisionApiClient appHomeVisionApiClient;
    @Mock
    private ResilientCallExecutor resilientCallExecutor;
    @Mock
    private ParallelTaskRunner parallelTaskRunner;
    @InjectMocks
    private HouseServiceImpl houseService;

    @Captor
    private ArgumentCaptor<List<Runnable>> runnables;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetHouses_success() {
        // Build 10 pages of 10 houses each
        List<HousesResponseVO> housesResponseVOS = new ArrayList<>();
        for (int currentPage = 1; currentPage <= HouseServiceImpl.PAGE_COUNT; currentPage++) {
            List<HouseVO> houseVOS = new ArrayList<>();
            for (int currentHouse = 1; currentHouse <= HouseServiceImpl.PAGE_SIZE; currentHouse++) {
                int houseId = currentPage == 1 ? currentHouse : currentHouse + ((currentPage - 1) * HouseServiceImpl.PAGE_SIZE);
                houseVOS.add(HouseVO.builder()
                        .id(Long.valueOf(houseId))
                        .address(houseId + "-address")
                        .photoURL(houseId + "-photoUrl")
                        .build());
            }
            housesResponseVOS.add(HousesResponseVO.builder().houses(houseVOS).build());
        }
        // Mock the executor to return one page for each invocation
        when(resilientCallExecutor.executeCall(any()))
                .thenReturn(housesResponseVOS.get(0))
                .thenReturn(housesResponseVOS.get(1))
                .thenReturn(housesResponseVOS.get(2))
                .thenReturn(housesResponseVOS.get(3))
                .thenReturn(housesResponseVOS.get(4))
                .thenReturn(housesResponseVOS.get(5))
                .thenReturn(housesResponseVOS.get(6))
                .thenReturn(housesResponseVOS.get(7))
                .thenReturn(housesResponseVOS.get(8))
                .thenReturn(housesResponseVOS.get(9));

        // Call the service under test
        HousesResponseDto result = houseService.getHouses();

        // Verify amount of invocations to each dependency
        verify(appHomeVisionApiClient, times(10)).getHouses(anyInt(), anyInt());
        verify(resilientCallExecutor, times(10)).executeCall(any());
        verify(parallelTaskRunner, times(1)).runAll(runnables.capture());

        // Capture the runnable list for photo downloading and assert its size
        assertThat(runnables.getValue().size()).isEqualTo(100);

        // Make a few useful assertions on the resulting house list

        assertThat(result.getHouses().size()).isEqualTo(100);

        assertThat(result.getHouses().get(0).getId()).isEqualTo(1);
        assertThat(result.getHouses().get(0).getAddress()).isEqualTo("1-address");
        assertThat(result.getHouses().get(0).getPhotoURL()).isEqualTo("1-photoUrl");

        assertThat(result.getHouses().get(99).getId()).isEqualTo(100);
        assertThat(result.getHouses().get(99).getAddress()).isEqualTo("100-address");
        assertThat(result.getHouses().get(99).getPhotoURL()).isEqualTo("100-photoUrl");
    }
}
