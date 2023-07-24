package school.faang.user_service.mappers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class SkillOfferMapperTest {

    @InjectMocks
    private SkillOfferMapper skillOfferMapper = Mappers.getMapper(SkillOfferMapper.class);


    @Test
    void testToEntity() {

        SkillOfferDto dto = new SkillOfferDto();
        dto.setSkill(1L);
        dto.setRecommendation(2L);

        SkillOffer entity = skillOfferMapper.toEntity(dto);

        assertEquals(dto.getSkill(), entity.getSkill().getId());
        assertEquals(dto.getRecommendation(), entity.getRecommendation().getId());
    }

    @Test
    void testToDto() {

        SkillOffer entity = new SkillOffer();
        Skill skill = new Skill();
        skill.setId(1L);
        Recommendation recommendation = new Recommendation();
        recommendation.setId(2L);
        entity.setSkill(skill);
        entity.setRecommendation(recommendation);

        SkillOfferDto dto = skillOfferMapper.toDto(entity);

        assertEquals(entity.getSkill().getId(), dto.getSkill());
        assertEquals(entity.getRecommendation().getId(), dto.getRecommendation());
    }

    @Test
    void testToSkillOfferDtos() {

        List<SkillOffer> skillOffers = new ArrayList<>();
        SkillOffer offer1 = new SkillOffer();
        offer1.setId(1L);
        Skill skill1 = new Skill();
        skill1.setId(101L);
        offer1.setSkill(skill1);
        Recommendation recommendation1 = new Recommendation();
        recommendation1.setId(201L);
        offer1.setRecommendation(recommendation1);
        skillOffers.add(offer1);

        SkillOffer offer2 = new SkillOffer();
        offer2.setId(2L);
        Skill skill2 = new Skill();
        skill2.setId(102L);
        offer2.setSkill(skill2);
        Recommendation recommendation2 = new Recommendation();
        recommendation2.setId(202L);
        offer2.setRecommendation(recommendation2);
        skillOffers.add(offer2);

        List<SkillOfferDto> dtos = skillOfferMapper.toSkillOfferDtos(skillOffers);

        assertEquals(2, dtos.size());
        assertEquals(skillOffers.get(0).getSkill().getId(), dtos.get(0).getSkill());
        assertEquals(skillOffers.get(0).getRecommendation().getId(), dtos.get(0).getRecommendation());
        assertEquals(skillOffers.get(1).getSkill().getId(), dtos.get(1).getSkill());
        assertEquals(skillOffers.get(1).getRecommendation().getId(), dtos.get(1).getRecommendation());
    }
}