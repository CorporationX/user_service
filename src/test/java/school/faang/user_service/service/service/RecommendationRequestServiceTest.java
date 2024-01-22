package school.faang.user_service.service.service;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.dto.SkillRequestDto;
import school.faang.user_service.exception.RequestTimeOutException;
import school.faang.user_service.exception.SkillsNotFoundException;
import school.faang.user_service.exception.UserNotFoundException;
import school.faang.user_service.mapper.RecommendationRequestMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;
import school.faang.user_service.service.RecommendationRequestService;

import java.time.LocalDateTime;
import java.util.ArrayList;

@ExtendWith(MockitoExtension.class)
public class RecommendationRequestServiceTest {

    @Mock
    RecommendationRequestRepository recommendationRequestRepository;
    @Mock
    SkillRequestRepository skillRequestRepository;
    @Mock
    UserRepository userRepository;
    @Spy
    RecommendationRequestMapper recommendationRequestMapper;
    @InjectMocks
    RecommendationRequestService recommendationRequestService;

    @Test
    void receiverIsNullTest() {
        Assert.assertThrows(UserNotFoundException.class, ()-> recommendationRequestService.create(new RecommendationRequestDto(5L, "message", "status", new ArrayList<SkillRequestDto>(), 5L, null, LocalDateTime.now(), LocalDateTime.now().plusMonths(7))));
    }

    @Test
    void requesterIsNullTest() {
        Assert.assertThrows(UserNotFoundException.class, ()-> recommendationRequestService.create(new RecommendationRequestDto(5L, "message", "status", new ArrayList<SkillRequestDto>(), null, 5L, LocalDateTime.now(), LocalDateTime.now().plusMonths(7))));
    }

    @Test
    void timeOutCheckTrueTest() {
        Assert.assertThrows(RequestTimeOutException.class, ()-> recommendationRequestService.create(new RecommendationRequestDto(5L, "message", "status", new ArrayList<SkillRequestDto>(), 6L, 5L, LocalDateTime.now(), LocalDateTime.now().plusMonths(6).minusDays(1))));
    }

    @Test
    void skillsCheckTest() {
        Assert.assertThrows(SkillsNotFoundException.class, ()-> recommendationRequestService.create(new RecommendationRequestDto(5L, "message", "status", null, 6L, 5L, LocalDateTime.now(), LocalDateTime.now().plusMonths(7))));
    }
}
