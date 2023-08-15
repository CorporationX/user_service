package school.faang.user_service.mapper;

import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.recommendation.SkillOffer;

import static org.junit.jupiter.api.Assertions.*;

class SkillOfferMapperTest {

    private final SkillOfferMapper skillOfferMapper = SkillOfferMapper.INSTANCE;

    @Test
    void toDTOTest() {
        Skill skill = new Skill();
        skill.setId(2L);

        SkillOffer skillOffer = new SkillOffer();
        skillOffer.setId(1L);
        skillOffer.setSkill(skill);

        SkillOfferDto skillOfferDTO = skillOfferMapper.toDTO(skillOffer);

        assertNotNull(skillOfferDTO);
        assertEquals(1L, skillOfferDTO.getId());
        assertEquals(2L, skillOfferDTO.getSkillId());
    }

    @Test
    void toEntityTest() {
        SkillOfferDto skillOfferDto = new SkillOfferDto();
        skillOfferDto.setId(1L);

        SkillOffer skilloffer = skillOfferMapper.toEntity(skillOfferDto);

        assertNotNull(skilloffer);
        assertEquals(1L, skillOfferDto.getId());
        assertNull(skillOfferDto.getSkillId());
    }
}

