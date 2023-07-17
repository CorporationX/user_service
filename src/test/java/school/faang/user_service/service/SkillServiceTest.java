package school.faang.user_service.service;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mappers.SkillMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;


@ExtendWith(MockitoExtension.class)
class SkillServiceTest {

    @InjectMocks
    private SkillService skillService;

    @Mock
    private SkillRepository skillRepository;

    @Mock
    private SkillMapper skillMapper;

    @Mock
    private SkillOfferRepository skillOfferRepository;

    @Test
    void testCreate() {
        skillService.create(new SkillDto());
        Mockito.verify(skillRepository, Mockito.times(1))
                .save(skillMapper.toEntity(new SkillDto()));
    }

    @Test
    void testCreateExistByTitle() {
        SkillDto skillDto = new SkillDto();
        skillDto.setTitle("privet");

        Mockito.when(skillRepository.existsByTitle(skillDto.getTitle()))
                .thenReturn(true);

        Assert.assertThrows(DataValidationException.class, () -> skillService.create(skillDto));
    }

    @Test
    void testGetUserSkills() {
        SkillDto skillDto = new SkillDto();
        skillDto.setId(4L);

        skillService.getUserSkills(skillDto.getId());
        Mockito.verify(skillRepository, Mockito.times(1))
                .findAllByUserId(skillDto.getId());
    }

    @Test
    void testGetOfferedSkills() {
        SkillDto skillDto = new SkillDto();
        skillDto.setId(4L);

        skillService.getOfferedSkills(skillDto.getId());
        Mockito.verify(skillRepository, Mockito.times(1))
                .findAllByUserId(skillDto.getId());
    }

    @Test
    void testAcquireSkillFromOffers() {
        Skill skill = new Skill(4L, "One", null, null, null, null, null, null);
        SkillDto skillDto = new SkillDto();
        skillDto.setId(4L);
        skillDto.setTitle("One");

        Recommendation recommendation1 = Recommendation.builder().receiver(User.builder().id(1L).username("sdf").build()).build();
        SkillOffer skillOffer1 = new SkillOffer(4L, skill, recommendation1);

        Mockito.when(skillRepository.findUserSkill(4L, 4L))
                .thenReturn(Optional.empty());
        Mockito.when(skillOfferRepository.findAllOffersOfSkill(4L, 4L))
                .thenReturn(List.of(skillOffer1));
        Mockito.when(skillMapper.toDTO(skill))
                .thenReturn(skillDto);
        Mockito.when(skillRepository.findById(4L))
                .thenReturn(Optional.of(skill));
        assertEquals(skillDto, skillService.acquireSkillFromOffers(4L, 4L));
    }
}