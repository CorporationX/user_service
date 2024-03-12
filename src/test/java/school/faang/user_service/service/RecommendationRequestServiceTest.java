package school.faang.user_service.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;

@ExtendWith(MockitoExtension.class)
class RecommendationRequestServiceTest {

    @Mock
    private RecommendationRequestRepository recommendationRequestRepository;

    @Mock
    private SkillRequestRepository skillRequestRepository;

    @InjectMocks
    private RecommendationRequestService recommendationRequestService;

    @Test
    void testCreate_WithValidData() {
        Long requesterId = 1L;
        Long receiverId = 2L;
        List<String> skills = Arrays.asList("1", "2", "3");

        recommendationRequestService.create(requesterId, receiverId, skills);

        verify(recommendationRequestRepository).existsByRequesterIdAndReceiverId(requesterId, receiverId);
        verify(recommendationRequestRepository).createRequest(requesterId, receiverId);
        for (String skill : skills) {
            Long skillId = Long.valueOf(skill);
            verify(skillRequestRepository).create(requesterId, skillId);
        }
    }

    @Test
    void testCreate_WithExistingRequest() {
        Long requesterId = 1L;
        Long receiverId = 2L;
        List<String> skills = Arrays.asList("1", "2", "3");
        when(recommendationRequestRepository.existsByRequesterIdAndReceiverId(requesterId, receiverId)).thenReturn(true);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> recommendationRequestService.create(requesterId, receiverId, skills));
        assertEquals("Запрос на рекомендацию уже отправлен и еще не закрыт", exception.getMessage());
        verify(recommendationRequestRepository, never()).createRequest(anyLong(), anyLong());
        verify(skillRequestRepository, never()).create(anyLong(), anyLong());
    }
}
