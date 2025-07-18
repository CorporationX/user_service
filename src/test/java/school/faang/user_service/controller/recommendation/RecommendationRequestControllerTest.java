package school.faang.user_service.controller.recommendation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.recommendation.CreateRecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RecommendationRequestFilterDto;
import school.faang.user_service.dto.recommendation.RejectionDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.service.recommendation.RecommendationRequestService;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@ExtendWith(MockitoExtension.class)
public class RecommendationRequestControllerTest {

    @InjectMocks
    private RecommendationRequestController recommendationRequestController;
    @Mock
    private RecommendationRequestService recommendationRequestService;

    @Test
    void testCreate() {
        CreateRecommendationRequestDto requestDto =
                new CreateRecommendationRequestDto("Сообщение", 1L);

        assertDoesNotThrow(() -> recommendationRequestService.create(requestDto));
    }

    @Test
    void testGetByFilters() throws Exception {
        RecommendationRequestFilterDto dto = new RecommendationRequestFilterDto(1L, 2L,
                "Hello", RequestStatus.ACCEPTED);

        assertDoesNotThrow(() -> recommendationRequestService.getByFilters(dto));
    }

    @Test
    void testGetById() throws Exception {
        Long id = 1L;

        assertDoesNotThrow(() -> recommendationRequestService.getById(id));
    }

    @Test
    void testAccept() throws Exception {
        Long id = 1L;

        assertDoesNotThrow(() -> recommendationRequestService.accept(id));
    }

    @Test
    void testReject() throws Exception {
        Long id = 1L;
        RejectionDto reason = new RejectionDto("Not");

        assertDoesNotThrow(() -> recommendationRequestService.reject(id, reason));
    }
}
