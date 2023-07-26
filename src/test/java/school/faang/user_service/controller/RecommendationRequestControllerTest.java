package school.faang.user_service.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import school.faang.user_service.controller.recommendation.RecommendationRequestController;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.service.RecommendationRequestService;
import java.time.LocalDateTime;

public class RecommendationRequestControllerTest {
    private RequestFilterDto filterDto;
    @Mock
    private RecommendationRequestService recommendationRequestService;
    @InjectMocks
    private RecommendationRequestController recommendationRequestController;

    @BeforeEach
    void setUp() {
        filterDto = RequestFilterDto.builder()
                .status(RequestStatus.REJECTED)
                .requesterId(4L)
                .receiverId(11L)
                .createdAt(LocalDateTime.now().minusMonths(1))
                .build();
    }

    @Test
    public void testRecommendationRequestsFiltered() {
        recommendationRequestController.getRecommendationRequests(filterDto);
        Mockito.verify(recommendationRequestService, Mockito.times(1)).getRequests(filterDto);
    }
}
