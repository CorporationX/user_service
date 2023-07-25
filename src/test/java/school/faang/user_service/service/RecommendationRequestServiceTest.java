package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.mapper.recommendation.RecommendationRequestMapper;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;

public class RecommendationRequestServiceTest {
    private RecommendationRequestMapper recommendationRequestMapper;
    private RequestFilterDto filterDto;
    @Mock
    private RecommendationRequestRepository recommendationRequestRepository;
    @InjectMocks
    private RecommendationRequestService recommendationRequestService;

    @Test
    public void getRequests_ReturnsMappedRecommendationRequests() {

        RecommendationRequest recommendationRequest = new RecommendationRequest();
        Mockito.when(recommendationRequestRepository.findAll()).thenReturn(List.of(recommendationRequest));

        RecommendationRequestDto recommendationRequestDto = new RecommendationRequestDto();
        Mockito.when(recommendationRequestMapper.toDto(recommendationRequest)).thenReturn(recommendationRequestDto);

        RequestFilterDto filterDto = new RequestFilterDto();


        List<RecommendationRequestDto> result = recommendationRequestService.getRequests(filterDto);

        assertThat(result).containsExactly(recommendationRequestDto);
    }
}
