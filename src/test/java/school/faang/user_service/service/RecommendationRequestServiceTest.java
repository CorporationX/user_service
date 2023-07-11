package school.faang.user_service.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;

class RecommendationRequestServiceTest {
    @InjectMocks
    private RecommendationRequestService recommendationRequestService;

    @Mock
    private RecommendationRequestRepository recommendationRequestRepository;

    @Mock
    private SkillRequestRepository skillRequestRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    void create_EmptyMessage_ThrowsIllegalArgumentException() {
        RecommendationRequestDto recommendationRequestDto = new RecommendationRequestDto();
        recommendationRequestDto.setMessage("");

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            recommendationRequestService.create(recommendationRequestDto);
        });

        Mockito.verify(recommendationRequestRepository, Mockito.never()).create(Mockito.anyLong(), Mockito.anyLong(), Mockito.anyString());
        Mockito.verify(skillRequestRepository, Mockito.never()).create(Mockito.anyLong(), Mockito.anyLong());
    }
}
