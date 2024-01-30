package school.faang.user_service.service.service;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.dto.SkillRequestDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.exception.RequestNotFoundException;
import school.faang.user_service.exception.RequestTimeOutException;
import school.faang.user_service.exception.SkillsNotFoundException;
import school.faang.user_service.exception.UserNotFoundException;
import school.faang.user_service.filter.FilterRecommendationRequest;
import school.faang.user_service.filter.recommendation.FilterRecommendationRequestCreateAt;
import school.faang.user_service.filter.recommendation.FilterRecommendationRequestId;
import school.faang.user_service.filter.recommendation.FilterRecommendationRequestMessage;
import school.faang.user_service.filter.recommendation.FilterRecommendationRequestUpdateAt;
import school.faang.user_service.mapper.RecommendationRequestMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;
import school.faang.user_service.service.RecommendationRequestService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RecommendationRequestServiceTest {

    @Mock
    RecommendationRequestRepository recommendationRequestRepository;
    @Mock
    SkillRequestRepository skillRequestRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    List<FilterRecommendationRequest> filterRecommendationRequestList;
    @Spy
    RecommendationRequestMapper recommendationRequestMapper;
    @InjectMocks
    RecommendationRequestService recommendationRequestService;

    private RequestFilterDto requestFilterDto;
    private List<RecommendationRequestDto> expected;
    private LocalDateTime localDateTime1;
    private LocalDateTime localDateTime2;
    private List<RecommendationRequest> recommendationRequests;
    private Stream<FilterRecommendationRequest> filterRecommendationRequestStream;

    @BeforeEach
    void setUp() {
        requestFilterDto = new RequestFilterDto();
        localDateTime1 = LocalDateTime.now().minusMonths(7);
        localDateTime2 = LocalDateTime.now();

        RecommendationRequest recommendationRequest1 = new RecommendationRequest();
        RecommendationRequest recommendationRequest2 = new RecommendationRequest();
        recommendationRequest2.setId(4L);
        recommendationRequest2.setMessage("message");
        recommendationRequest2.setCreatedAt(localDateTime1);
        recommendationRequest2.setUpdatedAt(localDateTime2);
        recommendationRequests = List.of(recommendationRequest1, recommendationRequest2);

        List<FilterRecommendationRequest> filterRecommendationRequests = List.of(new FilterRecommendationRequestMessage(), new FilterRecommendationRequestId(), new FilterRecommendationRequestUpdateAt(), new FilterRecommendationRequestCreateAt());
        filterRecommendationRequestStream = filterRecommendationRequests.stream();

        RecommendationRequestDto recommendationRequestDto = new RecommendationRequestDto(4L, "message", null,  null, null, null, localDateTime1, localDateTime2);
        expected = List.of(recommendationRequestDto);
    }

    @Test
    void receiverIsNullTest() {
        Assert.assertThrows(UserNotFoundException.class, () -> recommendationRequestService
                .create(new RecommendationRequestDto(5L, "message", "status", new ArrayList<SkillRequestDto>(), 5L, null, LocalDateTime.now(), LocalDateTime.now().plusMonths(7))));
    }

    @Test
    void requesterIsNullTest() {
        Assert.assertThrows(UserNotFoundException.class, () -> recommendationRequestService
                .create(new RecommendationRequestDto(5L, "message", "status", new ArrayList<SkillRequestDto>(), null, 5L, LocalDateTime.now(), LocalDateTime.now().plusMonths(7))));
    }

    @Test
    void timeOutCheckTrueTest() {
        Assert.assertThrows(RequestTimeOutException.class, () -> recommendationRequestService
                .create(new RecommendationRequestDto(5L, "message", "status", new ArrayList<SkillRequestDto>(), 6L, 5L, LocalDateTime.now(), LocalDateTime.now().plusMonths(6).minusDays(1))));
    }

    @Test
    void skillsCheckTest() {
        Assert.assertThrows(SkillsNotFoundException.class, () -> recommendationRequestService
                .create(new RecommendationRequestDto(5L, "message", "status", null, 6L, 5L, LocalDateTime.now(), LocalDateTime.now().plusMonths(7))));
    }

    @Test
    void getRequestTest() {
        long id = -5L;
        Assert.assertThrows(RequestNotFoundException.class, () -> {
            recommendationRequestService.getRequest(id);
        });
    }

    @Test
    void FilterIdTest() {
        when(recommendationRequestRepository.findAll()).thenReturn(recommendationRequests);
        when(filterRecommendationRequestList.stream()).thenReturn(filterRecommendationRequestStream);
        requestFilterDto.setId(4L);
        List<RecommendationRequestDto> actual = recommendationRequestService.getRequest(requestFilterDto);
        assertEquals(expected, actual);
    }

    @Test
    void FilterMessageTest() {
        when(recommendationRequestRepository.findAll()).thenReturn(recommendationRequests);
        when(filterRecommendationRequestList.stream()).thenReturn(filterRecommendationRequestStream);
        requestFilterDto.setMessage("message");
        List<RecommendationRequestDto> actual = recommendationRequestService.getRequest(requestFilterDto);
        assertEquals(expected, actual);
    }

    @Test
    void FilterCreateAtTest() {
        when(recommendationRequestRepository.findAll()).thenReturn(recommendationRequests);
        when(filterRecommendationRequestList.stream()).thenReturn(filterRecommendationRequestStream);
        requestFilterDto.setCreatedAt(localDateTime1);
        List<RecommendationRequestDto> actual = recommendationRequestService.getRequest(requestFilterDto);
        assertEquals(expected, actual);
    }

    @Test
    void FilterUpdateAtTest() {
        when(recommendationRequestRepository.findAll()).thenReturn(recommendationRequests);
        when(filterRecommendationRequestList.stream()).thenReturn(filterRecommendationRequestStream);
        requestFilterDto.setUpdatedAt(localDateTime2);
        List<RecommendationRequestDto> actual = recommendationRequestService.getRequest(requestFilterDto);
        assertEquals(expected, actual);
    }
}
