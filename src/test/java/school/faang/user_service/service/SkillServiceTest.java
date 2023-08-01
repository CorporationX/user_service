package school.faang.user_service.service;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.mapper.mymappers.Skill1Mapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


@ExtendWith(MockitoExtension.class)
class SkillServiceTest {

    @InjectMocks
    private SkillService skillService;

    @Mock
    private SkillRepository skillRepository;

    @Spy
    private SkillMapper skillMapper;

    @Mock
    private SkillOfferRepository skillOfferRepository;

    @Test
    void testCreate() {
        SkillDto skillDto = new SkillDto(1L, "privet");
        skillService.create(skillDto);
        Mockito.verify(skillRepository, Mockito.times(1)).save(skillMapper.toEntity(skillDto));
    }

    @Test
    void testCreateExistByTitle() {
        SkillDto skillDto = new SkillDto(1L, "privet");
        Mockito.when(skillRepository.existsByTitle(skillDto.getTitle()))
                .thenReturn(true);

        Assert.assertThrows(DataValidationException.class, () -> skillService.create(skillDto));
    }

    @Test
    void testGetUserSkills() {
        SkillDto skillDto = new SkillDto(1L, "privet");

        skillService.getUserSkills(skillDto.getId());
        Mockito.verify(skillRepository, Mockito.times(1))
                .findAllByUserId(skillDto.getId());
    }

    @Test
    void testGetOfferedSkills() {
        long userId = 1L;

        skillService.getOfferedSkills(userId);
        Mockito.verify(skillOfferRepository, Mockito.times(1))
                .findById(userId);
    }

    @Test
    void testAcquireSkillFromOffers() {
        Skill skill = new Skill(4L, "One", null, null, null, null, null, null);
        SkillDto skillDto = new SkillDto(4L, "One");

        Recommendation recommendation1 = Recommendation.builder().receiver(User.builder().id(1L).username("sdf").build()).build();
        SkillOffer skillOffer1 = new SkillOffer(skill, 4L, recommendation1);

        Mockito.when(skillRepository.findById(4L)).thenReturn(Optional.of(skill));
        Mockito.when(skillRepository.findUserSkill(4L, 4L)).thenReturn(Optional.empty());
        Mockito.when(skillOfferRepository.findAllOffersOfSkill(4L, 4L)).thenReturn(List.of(skillOffer1));
        Mockito.when(skillMapper.toDTO(skill)).thenReturn(skillDto);

        assertEquals(skillDto, skillService.acquireSkillFromOffers(4L, 4L));
    }

    @Test
    void testAcquireSkillFromOffers_SkillDoesNotExist() {
        Mockito.when(skillRepository.findById(5L)).thenReturn(Optional.empty());
        assertThrows(DataValidationException.class, () -> skillService.acquireSkillFromOffers(5, 0L));
    }

    @Test
    void testAcquireSkillFromOffersNotEnoughOffers() {
        Skill skill = new Skill(4L, "One", null, null, null, null, null, null);
        SkillDto skillDto = new SkillDto(4L, "One");

        Mockito.when(skillRepository.findById(4L)).thenReturn(Optional.of(skill));
        Mockito.when(skillRepository.findUserSkill(4L, 4L)).thenReturn(Optional.empty());
        Mockito.when(skillOfferRepository.findAllOffersOfSkill(4L, 4L)).thenReturn(Collections.EMPTY_LIST);
        Mockito.when(skillMapper.toDTO(skill)).thenReturn(skillDto);

        assertEquals(skillDto, skillService.acquireSkillFromOffers(4L, 4L));
    }
}