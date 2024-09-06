package school.faang.user_service.service;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.SkillCandidateMapper;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
public class SkillServiceTest {

    @Mock
    private SkillRepository skillRepository;

    @Mock
    private SkillOfferRepository skillOfferRepository;

    @Mock
    private UserRepository userRepository;

    @Spy
    private SkillMapper skillMapper;

    @Spy
    private SkillCandidateMapper skillCandidateMapper;

    @InjectMocks
    private SkillService skillService;

    private SkillDto skillDto;

    @BeforeEach
    void setUp() {
        skillDto = new SkillDto();
        skillDto.setTitle("title");
    }

    @Test
    void testExistingSkillCreate() {
        Mockito.when(skillRepository.existsByTitle("title")).thenReturn(true);

        Assert.assertThrows(DataValidationException.class,() -> skillService.create(skillDto));
    }

    @Test
    void testNonExistingSkillCreate() {
        Skill skill = skillMapper.toEntity(skillDto);

        Mockito.when(skillRepository.existsByTitle("title")).thenReturn(false);

        skillService.create(skillDto);

        Mockito.verify(skillRepository, Mockito.times(1)).save(skill);
    }

    @Test
    void testGetUserSkills() {
        skillService.getUserSkills(1L);

        Mockito.verify(skillRepository, Mockito.times(1)).findAllByUserId(1L);
    }

    @Test
    void testGetOfferSkills() {
        Skill firstSkill = new Skill();
        firstSkill.setTitle("title");
        firstSkill.setId(1L);

        Skill secondSkill = new Skill();
        secondSkill.setTitle("title2");
        secondSkill.setId(2L);

        Mockito.when(skillRepository.findSkillsOfferedToUser(1L)).thenReturn(List.of(firstSkill, secondSkill));

        SkillCandidateDto skillCandidateDto = new SkillCandidateDto();
        SkillCandidateDto skillCandidateDto2 = new SkillCandidateDto();

        Mockito.when(skillCandidateMapper.toSkillCandidateDto(firstSkill)).thenReturn(skillCandidateDto);
        Mockito.when(skillCandidateMapper.toSkillCandidateDto(secondSkill)).thenReturn(skillCandidateDto2);

        skillService.getOfferedSkills(1L);

        Mockito.verify(skillRepository, Mockito.times(1)).findSkillsOfferedToUser(1L);
        Mockito.verify(skillCandidateMapper, Mockito.times(1)).toSkillCandidateDto(firstSkill);
        Mockito.verify(skillCandidateMapper, Mockito.times(1)).toSkillCandidateDto(secondSkill);
    }

    @Test
    void testAcquireNullSkillFromOffers() {
        Assert.assertThrows(DataValidationException.class, () -> skillService.acquireSkillFromOffers(1L, 1L));
    }

    @Test
    void testAcquireSkillFromOffersMoreThanTwo() {
        Skill skill = new Skill();
        skill.setTitle("title");
        skill.setTitle("skillTitle");
        skill.setId(1L);
        Mockito.when(skillRepository.findUserSkill(1L, 1L)).thenReturn(Optional.of(skill));

        User user = new User();
        user.setId(1L);
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        SkillOffer skillOffer = new SkillOffer();
        skillOffer.setId(1);
        skillOffer.setSkill(skill);
        Mockito.when(skillOfferRepository.findAllOffersOfSkill(1L, 1L)).thenReturn(List.of(skillOffer, skillOffer, skillOffer));

        skillService.acquireSkillFromOffers(1L, 1L);

        Mockito.verify(userRepository, Mockito.times(4)).findById(skill.getId());
        Mockito.verify(skillMapper, Mockito.times(1)).toDto(skill);
    }

    @Test
    void testAcquireSkillFromOffersMoreThanOne() {
        Skill skill = new Skill();
        skill.setTitle("title");
        skill.setTitle("skillTitle");
        skill.setId(1L);
        Mockito.when(skillRepository.findUserSkill(1L, 1L)).thenReturn(Optional.of(skill));

        User user = new User();
        user.setId(1L);
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        SkillOffer skillOffer = new SkillOffer();
        skillOffer.setId(1);
        skillOffer.setSkill(skill);
        Mockito.when(skillOfferRepository.findAllOffersOfSkill(1L, 1L)).thenReturn(List.of(skillOffer, skillOffer));

        skillService.acquireSkillFromOffers(1L, 1L);

        Mockito.verify(userRepository, Mockito.times(1)).findById(user.getId());
        Mockito.verify(skillMapper, Mockito.times(1)).toDto(skill);
    }
}
