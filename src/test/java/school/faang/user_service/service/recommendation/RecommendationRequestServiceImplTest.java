package school.faang.user_service.service.recommendation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.model.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.model.dto.recommendation.RejectionDto;
import school.faang.user_service.model.dto.recommendation.RequestFilterDto;
import school.faang.user_service.model.entity.RequestStatus;
import school.faang.user_service.model.entity.User;
import school.faang.user_service.model.entity.recommendation.RecommendationRequest;
import school.faang.user_service.model.entity.recommendation.SkillRequest;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.filter.recommendation.RecommendationRequestFilter;
import school.faang.user_service.mapper.recommendation.RecommendationRequestMapper;
import school.faang.user_service.publisher.RecommendationRequestedEventPublisher;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;
import school.faang.user_service.service.impl.recommendation.RecommendationRequestServiceImpl;
import school.faang.user_service.validator.recommendation.RecommendationRequestValidator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class RecommendationRequestServiceImplTest {
    @Mock
    private RecommendationRequestMapper requestMapper;

    @Mock
    private RecommendationRequestValidator requestValidator;

    @Mock
    private SkillRequestRepository skillRequestRepository;

    @Mock
    private RecommendationRequestRepository recommendationRequestRepository;

    @Mock
    private List<RecommendationRequestFilter> recommendationsFilters;

    @Mock
    private RecommendationRequestedEventPublisher recommendationReqPublisher;

    @InjectMocks
    private RecommendationRequestServiceImpl requestService;

    private RecommendationRequestDto recommendationRequestDto;
    private RecommendationRequest recommendationRequest;
    private RequestFilterDto filter;
    private long id;
    private RejectionDto rejectionDto;

    @BeforeEach
    public void setup() {
        recommendationRequestDto = RecommendationRequestDto.builder()
                .id(1L)
                .skillsId(List.of(1L, 2L))
                .requesterId(1L)
                .receiverId(2L)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        recommendationRequest = RecommendationRequest.builder()
                .id(1L)
                .skills(List.of(new SkillRequest()))
                .requester(User.builder().id(1L).build())
                .receiver(User.builder().id(2L).build())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .status(RequestStatus.ACCEPTED)
                .build();

        filter = RequestFilterDto.builder()
                .status(RequestStatus.ACCEPTED)
                .id(5L)
                .build();

        id = 1;
        rejectionDto = new RejectionDto("some reason");
    }

    @Test
    void testCreate() {
        when(requestMapper.toEntity(recommendationRequestDto)).thenReturn(recommendationRequest);
        when(recommendationRequestRepository.save(recommendationRequest)).thenReturn(recommendationRequest);
        when(requestMapper.toDto(recommendationRequest)).thenReturn(recommendationRequestDto);
        when(skillRequestRepository.create(recommendationRequestDto.id(), recommendationRequest.getId())).thenReturn(new SkillRequest());

        RecommendationRequestDto savedRecommendationRequestDto = requestService.create(recommendationRequestDto);

        verify(requestValidator, times(1)).validateRecommendationRequest(recommendationRequestDto);
        verify(recommendationRequestRepository, times(1)).save(recommendationRequest);
        verify(skillRequestRepository, times(1))
                .create(recommendationRequestDto.id(), recommendationRequest.getId());
    }

    @Test
    void testGetResult() {
        List<RecommendationRequest> requestList = List.of(recommendationRequest);
        List<RecommendationRequestDto> requestDtosList = List.of(recommendationRequestDto);

        when(recommendationRequestRepository.findAll()).thenReturn(requestList);
        when(requestMapper.toDto(requestList)).thenReturn(requestDtosList);

        requestService.getRequests(filter);

        verify(recommendationRequestRepository, times(1)).findAll();
        verify(requestMapper, times(1)).toDto(requestList);
    }

    @Test
    void testGetRequestWithWrongId() {
        when(recommendationRequestRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        assertThrows(DataValidationException.class, () -> requestService.getRequest(Mockito.anyLong()));
    }

    @Test
    void testGetRequestWithOk() {
        when(recommendationRequestRepository.findById(Mockito.anyLong())).thenReturn(Optional.ofNullable(recommendationRequest));
        when(requestMapper.toDto(recommendationRequest)).thenReturn(recommendationRequestDto);

        assertEquals(recommendationRequestDto, requestService.getRequest(Mockito.anyLong()));
    }

    @Test
    void testRejectRequestWithWrongId() {
        when(recommendationRequestRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(DataValidationException.class, () -> requestService.rejectRequest(id, rejectionDto));
    }

    @Test
    void testRejectRequestOk() {
        when(recommendationRequestRepository.findById(id)).thenReturn(Optional.of(recommendationRequest));
        when(recommendationRequestRepository.save(recommendationRequest)).thenReturn(recommendationRequest);
        when(requestMapper.toDto(recommendationRequest)).thenReturn(recommendationRequestDto);

        requestService.rejectRequest(id, rejectionDto);

        verify(recommendationRequestRepository, times(1)).findById(id);
        verify(recommendationRequestRepository, times(1)).save(recommendationRequest);
        verify(requestMapper, times(1)).toDto(recommendationRequest);
    }
}
