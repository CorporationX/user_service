package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;
import school.faang.user_service.mapper.RecommendationRequestMapper;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RecommendationRequestServiceTest {
    @Mock
    private RecommendationRequestRepository recommendationRequestRepository;

    @Spy
    private RecommendationRequestMapper recommendationRequestMapper = RecommendationRequestMapper.INSTANCE;

    @InjectMocks
    private RecommendationRequestService recommendationRequestService;

    @Test
    void getRequestThrowsException() {
        when(recommendationRequestRepository.findById(anyLong())).thenReturn(Optional.empty());

        IllegalStateException illegalStateException = assertThrows(IllegalStateException.class, () ->
                recommendationRequestService.getRequest(anyLong()));

        assertEquals("There is no person with this id", illegalStateException.getMessage());
    }

    @Test
    void getRequest() {
        RecommendationRequest recommendationRequest = new RecommendationRequest();
        recommendationRequest.setId(1);

        when(recommendationRequestRepository.findById(1L)).thenReturn(Optional.of(recommendationRequest));
        RecommendationRequestDto recommendationWithValue = recommendationRequestService.getRequest(1);

        assertNotNull(recommendationWithValue);
        assertEquals(1, recommendationWithValue.getId());
    }

    @Test
    void listToListTest() {
        SkillRequest skillRequest = new SkillRequest();
        skillRequest.setId(1);

        List<SkillRequest> skills = List.of(skillRequest);

        RecommendationRequest recommendationRequest = new RecommendationRequest();
        recommendationRequest.setSkills(skills);
        recommendationRequest.setId(1);
        recommendationRequest.setMessage("test");

        RecommendationRequestDto recommendationRequestDto = recommendationRequestMapper.toDto(recommendationRequest);

        assertNotNull(recommendationRequestDto);
        assertEquals(1, recommendationRequestDto.getId());
        assertEquals("test", recommendationRequestDto.getMessage());
        assertEquals(1, recommendationRequestDto.getSkillsId().get(0));
    }
}
