package school.faang.user_service.service.skill;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import school.faang.user_service.dto.SkillCandidateDto;
import school.faang.user_service.dto.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.handler.exception.DataValidationException;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.service.skill.SkillService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;


@SpringBootTest
public class SkillServiceTest {
    @Mock
    private SkillRepository skillRepository;
    @Mock
    private SkillMapper skillMapper;
    @Mock
    private SkillOfferRepository skillOfferRepository;
    @InjectMocks
    private SkillService skillService;

    @Test
    public void testNullTitle() {
        SkillDto skillDto = new SkillDto();
        skillDto.setId(1L);
        skillDto.setTitle(null);
        Assert.assertThrows(DataValidationException.class, () -> skillService.create(skillDto));
    }

    @Test
    public void testEmptyTitle() {
        SkillDto skillDto = new SkillDto();
        skillDto.setId(1L);
        skillDto.setTitle("   ");
        Assert.assertThrows(DataValidationException.class, () -> skillService.create(skillDto));
    }

    @Test
    public void testExistTitle() {
        SkillDto skillDto = new SkillDto();
        skillDto.setTitle("title");
        skillDto.setId(1L);

        Mockito.when(skillRepository.existsByTitle("title")).thenReturn(true);
        Assert.assertThrows(DataValidationException.class, () -> skillService.create(skillDto));
    }

    @Test
    public void testSkillSaved() {
        SkillDto skillDto = new SkillDto();
        skillDto.setId(1L);
        skillDto.setTitle("mySkill");

        Skill skillEntity = new Skill();
        skillEntity.setId(skillDto.getId());
        skillEntity.setTitle(skillDto.getTitle());

        Mockito.when(skillMapper.toEntitySkill(skillDto)).thenReturn(skillEntity);
        Mockito.when(skillRepository.save(skillEntity)).thenReturn(skillEntity);
        Mockito.when(skillMapper.toDtoSkill(skillEntity)).thenReturn(skillDto);

        SkillDto resultDto = skillService.create(skillDto);

        Mockito.verify(skillMapper, Mockito.times(1)).toEntitySkill(skillDto);
        Mockito.verify(skillRepository, Mockito.times(1)).save(skillEntity);
        Mockito.verify(skillMapper, Mockito.times(1)).toDtoSkill(skillEntity);

        Assert.assertEquals(skillDto, resultDto);
    }

    @Test
    public void testGetUserSkills() {
        long userId = 1;
        List<Skill> skillsEntity = new ArrayList<>();
        List<SkillDto> skillDtos = new ArrayList<>();

        Mockito.when(skillRepository.findAllByUserId(userId)).thenReturn(skillsEntity);
        Mockito.when(skillMapper.toDtoSkill(skillsEntity)).thenReturn(skillDtos);
        List<SkillDto> resultDto = skillService.getUserSkills(userId);

        Mockito.verify(skillRepository, Mockito.times(1)).findAllByUserId(userId);
        Mockito.verify(skillMapper, Mockito.times(1)).toDtoSkill(skillsEntity);

        Assert.assertEquals(skillDtos, resultDto);
    }

    @Test
    public void testGetOfferedSkills() {
        long userId = 1L;
        Skill skillEntity1 = new Skill();
        skillEntity1.setTitle("title1");
        skillEntity1.setId(userId);

        Skill skillEntity2 = new Skill();
        skillEntity2.setTitle("title2");
        skillEntity2.setId(userId);

        List<Skill> skillsEntity = List.of(skillEntity1, skillEntity1, skillEntity1, skillEntity2);

        SkillDto skillDto1 = new SkillDto();
        skillDto1.setTitle("title1");
        skillDto1.setId(userId);

        SkillDto skillDto2 = new SkillDto();
        skillDto2.setTitle("title2");
        skillDto2.setId(userId);

        SkillCandidateDto skillCandidateDto1 = new SkillCandidateDto();
        skillCandidateDto1.setSkill(skillDto1);
        skillCandidateDto1.setOffersAmount(3);

        SkillCandidateDto skillCandidateDto2 = new SkillCandidateDto();
        skillCandidateDto2.setSkill(skillDto2);
        skillCandidateDto2.setOffersAmount(1);

        List<SkillCandidateDto> skillCandidateDtos = List.of(skillCandidateDto1, skillCandidateDto2);

        Mockito.when(skillRepository.findSkillsOfferedToUser(userId)).thenReturn(skillsEntity);
        Mockito.when(skillMapper.toDtoSkill(skillEntity2)).thenReturn(skillDto2);
        Mockito.when(skillMapper.toDtoSkill(skillEntity1)).thenReturn(skillDto1);

        List<SkillCandidateDto> result = skillService.getOfferedSkills(userId);

        Mockito.verify(skillRepository, Mockito.times(1)).findSkillsOfferedToUser(userId);
        Mockito.verify(skillMapper, Mockito.times(3)).toDtoSkill(skillEntity1);
        Mockito.verify(skillMapper, Mockito.times(1)).toDtoSkill(skillEntity2);
        Assert.assertEquals(skillCandidateDtos, result);
    }

    @Test
    public void testAcquireSkillFromOffers() {
        long skillId = 1L;
        long userId = 1L;

        Skill skill = new Skill();
        skill.setId(skillId);
        skill.setTitle("title1");

        SkillDto skillDto = new SkillDto();
        skillDto.setId(userId);
        skillDto.setTitle("title1");

        Mockito.when(skillRepository.findById(skillId)).thenReturn(Optional.of(skill));
        Mockito.when(skillRepository.findUserSkill(skillId, userId)).thenReturn(Optional.empty());
        Mockito.when(skillOfferRepository.findAllOffersOfSkill(skillId, userId)).thenReturn(Arrays.asList(new SkillOffer(), new SkillOffer()));
        Mockito.when(skillMapper.toDtoSkill(skill)).thenReturn(skillDto);

        SkillDto result = skillService.acquireSkillFromOffers(skillId, userId);

        Mockito.verify(skillRepository, Mockito.times(1)).findById(skillId);
        Mockito.verify(skillRepository, Mockito.times(1)).findUserSkill(skillId, userId);
        Mockito.verify(skillOfferRepository, Mockito.times(1)).findAllOffersOfSkill(skillId, userId);
        Mockito.verify(skillMapper, Mockito.times(1)).toDtoSkill(skill);

        Assert.assertEquals(skillDto, result);


    }
}
