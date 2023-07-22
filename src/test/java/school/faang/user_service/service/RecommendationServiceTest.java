package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(MockitoExtension.class)
class RecommendationServiceTest {
    @InjectMocks
    private RecommendationService recommendationService;
    @Mock
    private RecommendationRepository recommendationRepository;
    @Mock
    private SkillOfferRepository skillOfferRepository;
    @Test
    public void delete() {
        long recommendationId = 1L;
        recommendationService.delete(recommendationId);
        Mockito.verify(recommendationRepository, Mockito.times(1)).deleteById(recommendationId);
    }
}