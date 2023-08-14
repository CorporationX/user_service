package school.faang.user_service.service.recommendation;

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
import school.faang.user_service.mapper.recommendation.RecommendationRequestMapper;
import school.faang.user_service.mapper.recommendation.RecommendationRequestMapperImpl;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class RecommendationRequestServiceTest {

    @Spy
    private RecommendationRequestMapper recommendationRequestMapper = new RecommendationRequestMapperImpl();

    @Mock
    private RecommendationRequestRepository recommendationRequestRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SkillRequestRepository skillRequestRepository;

    @InjectMocks
    private RecommendationRequestService recommendationRequestService;

    @Test
    void create_EmptyMessage_ThrowsIllegalArgumentException() {
        RecommendationRequestDto recommendationRequestDto = new RecommendationRequestDto();
        recommendationRequestDto.setMessage("");

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            recommendationRequestService.create(recommendationRequestDto);
        });

        Mockito.verify(recommendationRequestRepository, Mockito.never())
                .create(Mockito.anyLong(), Mockito.anyLong(), Mockito.anyString());
        Mockito.verify(skillRequestRepository, Mockito.never())
                .create(Mockito.anyLong(), Mockito.anyLong());
    }
}