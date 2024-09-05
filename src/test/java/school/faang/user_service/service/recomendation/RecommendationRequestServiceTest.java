package school.faang.user_service.service.recomendation;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;
import school.faang.user_service.UserServiceApplication;
import school.faang.user_service.dto.recomendation.RecommendationRequestDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.mapper.recomendation.RecommendationRequestMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;

import java.time.LocalDateTime;
import java.util.List;

@Component
@ContextConfiguration(classes = UserServiceApplication.class)
@RequiredArgsConstructor
public class RecommendationRequestServiceTest {
    @Mock
    UserRepository userRepository;
    @Mock
    RecommendationRequestRepository recommendationRequestRepository;
    @Mock
    RecommendationRequestMapper recommendationRequestMapper;
    @Mock
    SkillRequestRepository skillRequestRepository;
    @Mock
    SkillRepository skillRepository;
    @InjectMocks
    RecommendationRequestService recommendationRequestService = new RecommendationRequestService(recommendationRequestRepository, userRepository, recommendationRequestMapper, skillRequestRepository, skillRepository);
    static RecommendationRequestDto recommendationRequestDto;

    @BeforeAll
    public static void setUp() {
        recommendationRequestDto = new RecommendationRequestDto();
        recommendationRequestDto.setReceiverId(2L);
        recommendationRequestDto.setId(1L);
        recommendationRequestDto.setCreatedAt(LocalDateTime.now());
        recommendationRequestDto.setRequesterId(5L);
        recommendationRequestDto.setMessage("testMessage");
        recommendationRequestDto.setStatus(RequestStatus.PENDING);
        recommendationRequestDto.setSkillsId(List.of(1L, 2L));
    }

    @Test
    @DisplayName("Try to create request with no such skills")
    public void createNoSuchSkills() {

        recommendationRequestService.create(recommendationRequestDto);
    }
}