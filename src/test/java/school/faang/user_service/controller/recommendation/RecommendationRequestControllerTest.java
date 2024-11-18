package school.faang.user_service.controller.recommendation;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.service.RecommendationRequestService;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class RecommendationRequestControllerTest {

    @Mock
    private RecommendationRequestService recommendationRequestService;

    @Mock
    private RejectionDto rejectionDto;

    @Mock
    private RecommendationRequest recRequestEntity;

    @InjectMocks
    private RecommendationRequestController recommendationRequestController;

    private RecommendationRequestDto recRequestDto;

    private RequestFilterDto filters;

    long recRequestId = 1100L;

    @BeforeEach
    void setUp() {
        recRequestDto = new RecommendationRequestDto();
        recRequestDto.setId(recRequestId);
        long requesterId = 100L;
        recRequestDto.setRequesterId(requesterId);
        long receiverId = 200L;
        recRequestDto.setReceiverId(receiverId);
        recRequestDto.setSkillsId(List.of(11L, 12L, 14L));
        recRequestDto.setMessage("Some message");
        filters = new RequestFilterDto();
        rejectionDto = new RejectionDto();
    }

    @Test
    public void testValidateRequestPositive() {
        recommendationRequestController.validateRequest(recRequestDto);
    }

    @Test
    public void testValidateRequestNegative() {
        recRequestDto.setMessage(null);
        assertThrows(IllegalArgumentException.class,
                () -> recommendationRequestController.validateRequest(recRequestDto));

        recRequestDto.setMessage("   ");
        assertThrows(IllegalArgumentException.class,
                () -> recommendationRequestController.validateRequest(recRequestDto));
    }

    @Test
    public void testRequestRecommendation() {
        Mockito.when(recommendationRequestService.create(recRequestDto))
                .thenReturn(recRequestDto);

        recommendationRequestController.requestRecommendation(recRequestDto);

        Mockito.verify(recommendationRequestService, Mockito.times(1))
                .create(recRequestDto);
    }

    @Test
    public void testGetRecommendationRequests() {
        Mockito.when(recommendationRequestService.getRequests(filters))
                .thenReturn(new ArrayList<RecommendationRequestDto>());

        recommendationRequestController.getRecommendationRequests(filters);

        Mockito.verify(recommendationRequestService, Mockito.times(1))
                .getRequests(filters);
    }

    @Test
    public void testGetRecommendationRequestPositive() {
        Mockito.when(recommendationRequestService.getRequest(recRequestId))
                .thenReturn(recRequestDto);

        RecommendationRequestDto result = recommendationRequestController
                .getRecommendationRequest(recRequestId);

        Assertions.assertEquals(recRequestId, result.getId());
        Mockito.verify(recommendationRequestService, Mockito.times(1))
                .getRequest(recRequestId);
    }

    @Test
    public void testRejectRequestPositive() {
        rejectionDto.setReason("Test reason");
        Mockito.when(recommendationRequestService.rejectRequest(recRequestId, rejectionDto))
                .thenReturn(recRequestDto);

        recommendationRequestController.rejectRequest(recRequestId, rejectionDto);

        Mockito.verify(recommendationRequestService, Mockito.times(1))
                .rejectRequest(recRequestId, rejectionDto);
    }

    @Test
    public void testRejectRequestNegative() {
        assertThrows(IllegalArgumentException.class,
                () -> recommendationRequestController.rejectRequest(recRequestId, rejectionDto));

        Mockito.verify(recommendationRequestService, Mockito.times(0))
                .rejectRequest(recRequestId, rejectionDto);
    }
}