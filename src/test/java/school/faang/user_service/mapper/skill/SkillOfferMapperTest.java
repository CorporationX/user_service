package school.faang.user_service.mapper.skill;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import school.faang.user_service.dto.skill.SkillOfferDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class SkillOfferMapperTest {

    private static final long ID = 1L;
    private static final int DTOS_SIZE = 1;
    private final SkillOfferMapper skillOfferMapper = Mappers.getMapper(SkillOfferMapper.class);
    private Skill skill;
    private Recommendation recommendation;
    private SkillOffer skillOffer;
    private List<SkillOffer> skillOffers;

    @BeforeEach
    public void init() {
        skill = Skill.builder()
                .id(ID)
                .build();
        recommendation = Recommendation.builder()
                .id(ID)
                .build();
        skillOffer = SkillOffer.builder()
                .id(ID)
                .skill(skill)
                .recommendation(recommendation)
                .build();

        skillOffers = List.of(skillOffer);
    }

    @Nested
    class ToDto {

        @Test
        @DisplayName("Успех маппинга SkillOffer в SkillOfferDto")
        public void whenSkillOfferIsNotNullThenReturnSkillOfferDto() {
            SkillOfferDto skillOfferDto = skillOfferMapper.toDto(skillOffer);

            assertNotNull(skillOfferDto);
            assertEquals(skillOffer.getId(), skillOfferDto.getId());
            assertEquals(skillOffer.getSkill().getId(), skillOfferDto.getSkillId());
            assertEquals(skillOffer.getRecommendation().getId(), skillOfferDto.getRecommendationId());
        }

        @Test
        @DisplayName("Успех маппинга List<SkillOffer> в List<SkillOfferDto>")
        public void whenListOfSkillOfferIsNotNullThenReturnSkillOfferDtos() {
            List<SkillOfferDto> skillOfferDtos = skillOfferMapper.toDtos(skillOffers);

            assertNotNull(skillOfferDtos);
            assertEquals(DTOS_SIZE, skillOfferDtos.size());
            assertEquals(skillOffer.getId(), skillOfferDtos.get(0).getId());
            assertEquals(skillOffer.getSkill().getId(), skillOfferDtos.get(0).getSkillId());
            assertEquals(skillOffer.getRecommendation().getId(), skillOfferDtos.get(0).getRecommendationId());
        }
    }
}