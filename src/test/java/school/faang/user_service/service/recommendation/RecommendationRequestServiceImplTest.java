package school.faang.user_service.service.recommendation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.recommendation.RecommendationRequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.filter.recommendation.RecommendationRequestFilter;
import school.faang.user_service.filter.recommendation.RecommendationRequestReceiverFilter;
import school.faang.user_service.mapper.recommendation.RecommendationRequestMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;
import school.faang.user_service.validator.recommendation.RecommendationRequestValidator;

import java.util.List;

@ExtendWith(MockitoExtension.class)
public class RecommendationRequestServiceImplTest {
    @Spy
    private RecommendationRequestMapper recommendationRequestMapper;
    @Mock
    private RecommendationRequestValidator recommendationRequestValidator;
    @Mock
    private RecommendationRequestRepository repository;
    @Mock
    private SkillRequestRepository skillRequestRepository;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private RecommendationRequestServiceImpl recommendationRequestService;

    @BeforeEach
    void setUp() {

    }
}
