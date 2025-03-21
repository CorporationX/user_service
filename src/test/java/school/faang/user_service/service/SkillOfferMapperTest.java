package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.SkillOfferMapperImpl;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static school.faang.user_service.service.RecommendationService.ID_NULL_EXCEPTION;

@ExtendWith(MockitoExtension.class)
public class SkillOfferMapperTest {
    @Spy
    private SkillOfferMapperImpl skillOfferMapper = new SkillOfferMapperImpl();

    @Test
    public void testToSkillOffer() {
        SkillOfferDto skillOfferDto = prepareSkillOfferDto();

        SkillOffer result = skillOfferMapper.toSkillOffer(skillOfferDto);

        assertNotNull(result);
        assertEquals(skillOfferDto.getId(), result.getId());
        assertEquals(skillOfferDto.getSkillId(), result.getSkill().getId());
    }

    @Test
    public void testToSkillOfferDto() {
        SkillOffer skillOffer = prepareSkillOffer();

        SkillOfferDto result = skillOfferMapper.toSkillOfferDto(skillOffer);

        assertNotNull(result);
        assertEquals(result.getId(), skillOffer.getId());
        assertEquals(result.getSkillId(), skillOffer.getSkill().getId());
    }

    @Test
    public void testToSkillOfferList() {
        SkillOfferDto skillOfferDto = prepareSkillOfferDto();
        List<SkillOfferDto> skillOfferDtoList = List.of(skillOfferDto);

        List<SkillOffer> skillOfferList = skillOfferMapper.toSkillOfferList(skillOfferDtoList);
        SkillOffer result = skillOfferList.get(0);

        assertNotNull(result);
        assertEquals(skillOfferDto.getId(), result.getId());
        assertEquals(skillOfferDto.getSkillId(), result.getSkill().getId());
    }

    @Test
    public void testToSkillOfferDtoList() {
        SkillOffer skillOffer = prepareSkillOffer();
        List<SkillOffer> skillOfferList = List.of(skillOffer);

        List<SkillOfferDto> skillOfferDtoList = skillOfferMapper.toSkillOfferDtoList(skillOfferList);
        SkillOfferDto result = skillOfferDtoList.get(0);

        assertNotNull(result);
        assertEquals(result.getId(), skillOffer.getId());
        assertEquals(result.getSkillId(), skillOffer.getSkill().getId());
    }

    @Test
    public void testMapIdToSkillWithIdNull() {
        Long id = null;

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> skillOfferMapper.mapIdToSkill(id));

        assertEquals(ID_NULL_EXCEPTION, exception.getMessage());
    }

    @Test
    public void testMapIdToSKill() {
        Long id = 1L;
        Skill expetedSkill = Skill.builder().id(1L).build();

        Skill result = skillOfferMapper.mapIdToSkill(id);

        assertNotNull(result);
        assertEquals(expetedSkill, result);
    }

    private SkillOfferDto prepareSkillOfferDto() {
        return SkillOfferDto.builder()
                .id(1L)
                .skillId(1L)
                .build();
    }

    private SkillOffer prepareSkillOffer() {
        Skill skill = Skill.builder().id(1L).build();
        return SkillOffer.builder()
                .id(1L)
                .skill(skill)
                .build();
    }
}
