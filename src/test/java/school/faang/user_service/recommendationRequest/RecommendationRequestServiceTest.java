package school.faang.user_service.recommendationRequest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.mapper.RecommendationRequestMapper;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;
import school.faang.user_service.service.RecommendationRequestService;

@ExtendWith(MockitoExtension.class)
public class RecommendationRequestServiceTest {
   @Mock
   private RecommendationRequestRepository recommendationRequestRepository;
   @Mock
    private SkillRequestRepository skillRequestRepository;
   @Mock
    private RecommendationRequestMapper recommendationRequestMapper;
   @InjectMocks
    private RecommendationRequestService recommendationRequestService;

   @Test
    public void testValidateReceiverExistence(){

   }

    @Test
    public void testValidateRequesterExistence(){

    }

    @Test
    public void testValidateRequestsPeriod(){

    }

    @Test
    public void testValidateSkillsExistence(){

    }

}
