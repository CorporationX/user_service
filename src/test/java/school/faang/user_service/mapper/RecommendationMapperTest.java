package school.faang.user_service.mapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.entity.recommendation.Recommendation;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class RecommendationMapperTest {
    @InjectMocks
    private final RecommendationMapper recommendationMapper = new RecommendationMapperImpl();
    @Mock
    private RecommendationDto recommendationDto;
    @Mock
    private Recommendation recommendation;

    @Test
    void testToDto(){
        RecommendationDto dto = recommendationMapper.toDto(recommendation);

        assertEquals(recommendationDto.getAuthorId(), dto.getAuthorId());
        assertEquals(recommendationDto.getReceiverId(), dto.getReceiverId());
        assertEquals(recommendationDto.getContent(), dto.getContent());
    }

    @Test
    void testToDtoNull(){
        RecommendationDto dto = recommendationMapper.toDto(null);

        assertEquals(null, dto);
    }
}
