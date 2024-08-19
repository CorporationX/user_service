package school.faang.user_service.service.service;

import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.SkillCandidateMapperImpl;
import school.faang.user_service.mapper.SkillMapperImpl;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.service.skill.SkillService;
import school.faang.user_service.validator.skill.SkillValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;

@ExtendWith(MockitoExtension.class)
public class SkillServiceTest {
    @Mock
    private SkillRepository skillRepository;

    @Mock
    private SkillValidator skillValidator;

    @Spy
    private SkillMapperImpl skillMapper;

    @Spy
    private SkillCandidateMapperImpl skillCandidateMapper;

    @Mock
    private SkillOfferRepository skillOfferRepository;

    @Mock
    private UserSkillGuaranteeRepository userSkillGuaranteeRepository;

    @InjectMocks
    private SkillService skillService;

    private SkillDto skillDto;

    @BeforeEach
    public void setUp() {
        skillDto = new SkillDto(1L, "title");
    }

    @Test
    public void testCreate() {
        Skill skill = skillMapper.toEntity(skillDto);
        Mockito.doNothing().when(skillValidator).validateSkillDto(skillDto);
        Mockito.when(skillRepository.save(skillMapper.toEntity(skillDto))).thenReturn(skill);
        SkillDto actualDto = skillService.create(skillDto);
        Mockito.verify(skillRepository, Mockito.times(1)).save(skillMapper.toEntity(skillDto));
        Assertions.assertEquals(skillDto, actualDto);
    }

    @Test
    public void testGetUserSkills() {
        skillService.getUserSkills(skillDto.getId());
        Mockito.verify(skillRepository, Mockito.times(1)).findAllByUserId(skillDto.getId());
    }

    @Test
    public void testGetOfferedSkills() {
        long userId = 1L;
        Skill firstSkill = new Skill();
        firstSkill.setTitle("title");
        firstSkill.setId(1L);
        List<Skill> listOfOneSkill = new ArrayList<>();
        listOfOneSkill.add(firstSkill);
        List<SkillCandidateDto> expectedSkill = new ArrayList<>();
        SkillCandidateDto firstSkillCandidateDto = new SkillCandidateDto(skillMapper.toDto(firstSkill), 1);
        expectedSkill.add(firstSkillCandidateDto);
        Mockito.when(skillRepository.findSkillsOfferedToUser(userId)).thenReturn(listOfOneSkill);
        List<SkillCandidateDto> actualSkill = skillService.getOfferedSkills(userId);
        Mockito.verify(skillRepository, Mockito.times(1)).findSkillsOfferedToUser(userId);
        Assertions.assertEquals(expectedSkill, actualSkill);
    }

    @Test
    public void testGetSkillByIdThrowsException() {
        Optional<Skill> skill = Optional.empty();
        Mockito.when(skillRepository.findById(1L)).thenReturn(skill);
        Assert.assertThrows(DataValidationException.class, () -> skillService.findById(1L));
    }

    @Test
    public void testGetSkillById() {
        Skill skill = new Skill();
        skill.setId(1L);
        Optional<Skill> someSkill = Optional.of(skill);
        Mockito.when(skillRepository.findById(1L)).thenReturn(someSkill);
        assertEquals(skill, skillService.findById(1L));
    }

    @Test
    public void testAcquireSkillFromOffers() {
        Skill skill = Skill.builder().id(1L).title("title").build();
        User user = User.builder().id(1L).build();
        User otherUser = User.builder().id(2L).build();
        Recommendation recommendation = Recommendation.builder().author(otherUser).receiver(user).build();
        SkillOffer offer = SkillOffer.builder().skill(skill).recommendation(recommendation).build();
        UserSkillGuarantee guarantee = UserSkillGuarantee.builder()
                .user(user).guarantor(otherUser).skill(offer.getSkill()).build();
        List<SkillOffer> threeOffers = List.of(offer, offer, offer);

        Mockito.when(skillRepository.findById(1L)).thenReturn(Optional.of(skill));
        Mockito.when(skillRepository.findUserSkill(1L, 1L)).thenReturn(Optional.empty());
        Mockito.when(skillOfferRepository.countAllOffersOfSkill(1L,1L)).thenReturn(3);
        Mockito.when(skillOfferRepository.findAllOffersOfSkill(1L, 1L)).thenReturn(threeOffers);
        Mockito.doNothing().when(skillRepository).assignSkillToUser(1L,1L);
        Mockito.when(userSkillGuaranteeRepository.save(Mockito.any(UserSkillGuarantee.class))).thenReturn(guarantee);
        skillService.acquireSkillFromOffers(1L, 1L);

        Mockito.verify(skillRepository, Mockito.times(1)).assignSkillToUser(1L, 1L);
        Mockito.verify(skillRepository, Mockito.times(1)).findById(1L);
        Mockito.verify(skillRepository, Mockito.times(1)).findUserSkill(1L, 1L);
        Mockito.verify(skillOfferRepository, Mockito.times(1)).findAllOffersOfSkill(1L, 1L);
        Assertions.assertEquals(guarantee, skill.getGuarantees().get(0));
    }
}
