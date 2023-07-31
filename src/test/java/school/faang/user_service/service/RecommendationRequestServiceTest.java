package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.dto.filter.RecommendationRequestFilterDto;
import school.faang.user_service.dto.filter.RequestFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.filter.*;
import school.faang.user_service.mapper.RecommendationRequestMapper;
import school.faang.user_service.mapper.RecommendationRequestMapperImpl;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class RecommendationRequestServiceTest {
    @InjectMocks
    private RecommendationRequestService recommendationRequestService;
    @Mock
    private RecommendationRequestRepository recommendationRequestRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private SkillRepository skillRepository;
    @Spy
    private RecommendationRequestMapperImpl recommendationRequestMapper;
//    @Mock
//    private RecommendationRequestMapper recommendationRequestMapper;
    @Mock
    private RecommendationRequestDto recommendationRequestDto;

    @Test
    void testRecommendationRequestFilter() {
        List<RecommendationRequestFilter> recommendationRequestFilters = List.of(new RecommendationRequestMessageFilter());

        RecommendationRequest recommendationRequest1 = new RecommendationRequest();
        RecommendationRequest recommendationRequest2 = new RecommendationRequest();
        RecommendationRequest recommendationRequest3 = new RecommendationRequest();

        recommendationRequest1.setMessage("Hello");
        recommendationRequest2.setMessage("Bye");
        recommendationRequest3.setMessage("");

        List<RecommendationRequest> requests = List.of(recommendationRequest1, recommendationRequest2, recommendationRequest3);

        RecommendationRequestFilterDto recommendationRequestFilterDto =
                RecommendationRequestFilterDto.builder().message("Hello").build();

        Mockito.when(recommendationRequestRepository.findAll()).thenReturn(requests);
        //Mockito.when(recommendationRequestMapper.toDtoList(any())).thenReturn(new ArrayList<>());

        recommendationRequestService = new RecommendationRequestService(recommendationRequestRepository, recommendationRequestMapper, recommendationRequestFilters);

        List<RecommendationRequestDto> eventsByFilter = recommendationRequestService.getFilterRequest(recommendationRequestFilterDto);
        assertEquals(1, eventsByFilter.size());
    }
}