package school.faang.user_service.mapper.recommendation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.mapper.skill.SkillOfferMapper;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


@ExtendWith(MockitoExtension.class)
class RecommendationMapperTest {

    private static final long ID = 1L;
    private static final String CONTENT = "content";
    @InjectMocks
    private RecommendationMapperImpl recommendationMapper = new RecommendationMapperImpl(
            Mappers.getMapper(SkillOfferMapper.class));
    private Recommendation recommendation;
    private List<Recommendation> recommendations;

    @BeforeEach
    public void init() {
        recommendation = Recommendation.builder()
                .id(ID)
                .author(User.builder()
                        .id(ID)
                        .build())
                .receiver(User.builder()
                        .id(ID)
                        .build())
                .content(CONTENT)
                .skillOffers(List.of(SkillOffer.builder()
                        .id(ID)
                        .skill(Skill.builder()
                                .id(ID)
                                .build())
                        .recommendation(Recommendation.builder()
                                .id(ID)
                                .build())
                        .build()))
                .createdAt(LocalDateTime.of(2024, 10, 10, 10, 10))
                .build();

        recommendations = List.of(recommendation);
    }

    @Nested
    class ToDto {

        @Test
        @DisplayName("Успех маппинга Recommendation в RecommendationDto")
        public void whenRecommendationThenReturnRecommendationDto() {
            RecommendationDto recommendationDto = recommendationMapper.toDto(recommendation);

            assertNotNull(recommendationDto);
            assertEquals(recommendation.getId(), recommendationDto.getId());
            assertEquals(recommendation.getAuthor().getId(), recommendationDto.getAuthorId());
            assertEquals(recommendation.getReceiver().getId(), recommendationDto.getReceiverId());
            assertEquals(recommendation.getContent(), recommendationDto.getContent());
            assertEquals(recommendation.getCreatedAt(), recommendationDto.getCreatedAt());
            assertEquals(recommendation.getSkillOffers().size(), recommendationDto.getSkillOffers().size());
        }

        @Test
        @DisplayName("Успех маппинга List<Recommendation> в List<RecommendationDto>")
        public void whenListRecommendationsThenReturnRecommendationDtos() {
            List<RecommendationDto> recommendationDtos = recommendationMapper.toDtos(recommendations);

            assertNotNull(recommendationDtos);
            assertEquals(recommendations.size(), recommendationDtos.size());
        }
    }
}