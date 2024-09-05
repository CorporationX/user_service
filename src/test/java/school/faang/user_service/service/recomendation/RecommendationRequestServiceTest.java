package school.faang.user_service.service.recomendation;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import school.faang.user_service.mapper.recomendation.RecommendationRequestMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;

class RecommendationRequestServiceTest {
    @Mock
    RecommendationRepository recommendationRepository;
    @Mock
    RecommendationRequestRepository recommendationRequestRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    RecommendationRequestMapper recommendationRequestMapper;
    @Mock
    SkillRequestRepository skillRequestRepository;
    @Mock
    SkillRepository skillRepository;
    @InjectMocks
    RecommendationRequestService recommendationRequestService = new RecommendationRequestService(recommendationRequestRepository,userRepository,recommendationRequestMapper,skillRequestRepository,skillRepository);

}