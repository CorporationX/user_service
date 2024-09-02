package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;
import school.faang.user_service.mapper.RecommendationRequestMapper;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;
import school.faang.user_service.validator.recommendation.RecommendationRequestValidator;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class RecommendationRequestServiceTest {
    @Mock
    private RecommendationRequestMapper requestMapper;

    @Mock
    private RecommendationRequestValidator requestValidator;

    @Mock
    private SkillRequestRepository skillRequestRepository;

    @Mock
    private RecommendationRequestRepository recommendationRequestRepository;

    private RecommendationRequestDto recommendationRequestDto;
    private RecommendationRequest recommendationRequest;

    @InjectMocks
    private RecommendationRequestService requestService;

    @BeforeEach
    public void setup() {
        recommendationRequestDto = RecommendationRequestDto.builder()
                .id(1L)
                .skillsId(List.of(1L, 2L))
                .requesterId(1L)
                .receiverId(1L)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        recommendationRequest = RecommendationRequest.builder()
                .id(1L)
                .skills(List.of(new SkillRequest()))
                .requester(new User())
                .receiver(new User())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    void testCreate(){
        when(requestMapper.toEntity(recommendationRequestDto)).thenReturn(recommendationRequest);
        when(recommendationRequestRepository.save(recommendationRequest)).thenReturn(recommendationRequest);
        when(requestMapper.toDto(recommendationRequest)).thenReturn(recommendationRequestDto);
        when(skillRequestRepository.create(recommendationRequestDto.getId(), recommendationRequest.getId())).thenReturn(new SkillRequest());

        RecommendationRequestDto savedRecommendationRequestDto = requestService.create(recommendationRequestDto);

        verify(requestValidator, times(1)).validateRecommendationRequest(recommendationRequestDto);
        verify(recommendationRequestRepository, times(1)).save(recommendationRequest);
        verify(skillRequestRepository, times(1))
                .create(recommendationRequestDto.getId(), recommendationRequest.getId());
    }
}
