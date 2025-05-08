package school.faang.user_service.service;

import org.joda.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.mapper.RecommendationMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TestRecommendationMapper {
    private final RecommendationMapper recommendationMapper = Mappers.getMapper(RecommendationMapper.class);

    @Test
    void testToDto() {
        Recommendation recommendation = new Recommendation();
        recommendation.setId(1L);
        recommendation.setContent("Great developer!");
        recommendation.setUpdatedAt(LocalDateTime.now());

        RecommendationDto recommendationDto = recommendationMapper.toDto(recommendation);

        assertEquals(1L, recommendationDto.getId());
        assertEquals("Great developer!", recommendationDto.getContent());
    }
}