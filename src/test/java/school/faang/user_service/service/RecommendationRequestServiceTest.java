package school.faang.user_service.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.mapper.recommendation.RecommendationRequestMapper;
import school.faang.user_service.mapper.recommendation.RecommendationRequestMapperImpl;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;
import school.faang.user_service.validator.RecommendationRequestValidator;
import school.faang.user_service.validator.SkillValidator;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class RecommendationRequestServiceTest {

    @InjectMocks
    private RecommendationRequestService recommendationRequestService;

    @Mock
    private RecommendationRequestRepository recommendationRequestRepository;

    @Spy
    private RecommendationRequestMapper recommendationRequestMapper;

    @Mock
    private RecommendationRequestValidator recommendationRequestValidator;

    @Mock
    private SkillValidator skillValidator;

    RecommendationRequestDto recommendationRequestDto;

    @BeforeEach
    void setUp() {
        recommendationRequestDto = RecommendationRequestDto.builder()
                .id(1L)
                .message("message")
                .status(RequestStatus.ACCEPTED)
                .skillId(List.of(1L))
                .requesterId(1L)
                .receiverId(1L)
                .createdAt(LocalDateTime.now().minusMonths(7))
                .build();
    }

    @Test
    void tesCreate() {
        recommendationRequestService.create(recommendationRequestDto);
        Mockito.verify(recommendationRequestRepository).save(recommendationRequestMapper.toEntity(recommendationRequestDto));
    }
}