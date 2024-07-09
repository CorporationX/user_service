package school.faang.user_service.service;

import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
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
import school.faang.user_service.mapper.ListOfSkillsCandidateMapperImpl;
import school.faang.user_service.mapper.SkillCandidateMapperImpl;
import school.faang.user_service.mapper.SkillMapperImpl;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.spy;

@ExtendWith(MockitoExtension.class)
public class SkillServiceTest {
    @Mock
    private SkillRepository skillRepository;

    @Spy
    private SkillMapperImpl skillMapper;

    @Spy
    private SkillCandidateMapperImpl skillCandidateMapper;

    @Spy
    private SkillOfferRepository skillOfferRepository;

    @Spy
    private UserSkillGuaranteeRepository userSkillGuaranteeRepository;

    @Spy
    private ListOfSkillsCandidateMapperImpl listOfSkillsCandidateMapper;

    @InjectMocks
    private SkillService skillService;

    @Test
    public void testCreate() {
        SkillDto skill = new SkillDto(1L, "title");
        skillService.create(skill);
        Mockito.verify(skillRepository, Mockito.times(1)).save(skillMapper.toEntity(skill));
    }

    @Test
    public void testGetUserSkills() {
        SkillDto skill = new SkillDto(1L, "title");
        skillService.getUserSkills(skill.getId());
        Mockito.verify(skillRepository, Mockito.times(1)).findAllByUserId(skill.getId());
    }

    @Test
    public void testGetOfferedSkills() {
        long userId = 1L;
        Skill firstSkill = new Skill();
        firstSkill.setTitle("title");
        firstSkill.setId(1L);
        List<Skill> listOfOneSkill = new ArrayList<>();
        listOfOneSkill.add(firstSkill);
        Mockito.when(skillRepository.findSkillsOfferedToUser(userId)).thenReturn(listOfOneSkill);
        skillService.getOfferedSkills(userId);
        Mockito.verify(skillRepository, Mockito.times(1)).findSkillsOfferedToUser(userId);
    }

    @Test
    public void testGetSkillByIdThrowsException() {
        Optional<Skill> skill = Optional.empty();
        Mockito.when(skillRepository.findById(1L)).thenReturn(skill);
        Assert.assertThrows(IllegalArgumentException.class, () -> skillService.getSkillById(1L));
    }

    @Test
    public void testGetSkillById() {
        Skill skill = new Skill();
        skill.setId(1L);
        Optional<Skill> someSkill = Optional.of(skill);
        Mockito.when(skillRepository.findById(1L)).thenReturn(someSkill);
        Assert.assertEquals(skillMapper.toDto(skill), skillService.getSkillById(1L));
    }

    @Test
    public void testAddGuaranteeThrowsException() {
        List<SkillOffer> noOffers = new ArrayList<>();
        Assert.assertThrows(IllegalArgumentException.class, () -> skillService.addGuarantee(noOffers));
    }

    @Test
    public void testAddGuarantee() {
        User user1 = User.builder().id(1L).build();
        User user2 = User.builder().id(2L).build();
        User user3 = User.builder().id(3L).build();
        Skill skill1 = Skill.builder().id(1L).title("title1").build();
        Skill skill2 = Skill.builder().id(2L).title("title2").build();
        Recommendation recommendation1 = Recommendation.builder().author(user1).receiver(user3).build();
        Recommendation recommendation2 = Recommendation.builder().author(user2).receiver(user3).build();
        SkillOffer offer1 = SkillOffer.builder().skill(skill1).recommendation(recommendation1).build();
        SkillOffer offer2 = SkillOffer.builder().skill(skill2).recommendation(recommendation2).build();
        List<SkillOffer> offers = List.of(offer1, offer2);
        Mockito.when(userSkillGuaranteeRepository.save(UserSkillGuarantee.builder()
                .user(user3).guarantor(user1).skill(skill1).build())).thenReturn(UserSkillGuarantee.builder()
                .user(user3).guarantor(user1).skill(skill1).build());
        Mockito.when(userSkillGuaranteeRepository.save(UserSkillGuarantee.builder()
                .user(user3).guarantor(user2).skill(skill2).build())).thenReturn(UserSkillGuarantee.builder()
                .user(user3).guarantor(user2).skill(skill2).build());
        skillService.addGuarantee(offers);
        Mockito.verify(userSkillGuaranteeRepository, Mockito.times(1)).save(UserSkillGuarantee.builder()
                .user(user3).guarantor(user1).skill(skill1).build());
        Mockito.verify(userSkillGuaranteeRepository, Mockito.times(1)).save(UserSkillGuarantee.builder()
                .user(user3).guarantor(user2).skill(skill2).build());
        Mockito.verify(userSkillGuaranteeRepository, Mockito.times(2))
                .save(Mockito.any(UserSkillGuarantee.class));
    }

    @Test
    public void testAcquireSkillFromOffersForLearnedSkill() {
        long skillId = 1L;
        Skill skill = Skill.builder().id(skillId).build();
        Optional<Skill> realSkill = Optional.of(skill);
        long userId = 2L;
        Mockito.when(skillRepository.findUserSkill(skillId, userId)).thenReturn(realSkill);
        Assertions.assertEquals(skillMapper.toDto(realSkill.get()), skillService.acquireSkillFromOffers(skillId, userId));
    }

    @Test
    public void testAcquireSkillFromOffersNotEnoughOffers() {
        long skillId = 1L;
        long userId = 2L;
        List<SkillOffer> zeroOffers = new ArrayList<>();
        Mockito.when(skillRepository.findUserSkill(skillId, userId)).thenReturn(Optional.empty());
        Mockito.when(skillOfferRepository.findAllOffersOfSkill(1L, 2L)).thenReturn(zeroOffers);
        Assert.assertThrows(DataValidationException.class, () -> skillService.acquireSkillFromOffers(skillId, userId));
    }

    @Test
    public void testAcquireSkillFromOffers() {
        SkillService skillServiceTest = spy(skillService);
        Skill skill = Skill.builder().id(1L).title("title").build();
        User user = User.builder().id(1L).build();
        User otherUser = User.builder().id(2L).build();
        Recommendation recommendation = Recommendation.builder().author(user).receiver(otherUser).build();
        SkillOffer offer = SkillOffer.builder().skill(skill).recommendation(recommendation).build();
        List<SkillOffer> threeOffers = List.of(offer, offer, offer);
        Mockito.when(skillRepository.findUserSkill(1L, 1L)).thenReturn(Optional.empty());
        Mockito.when(skillOfferRepository.findAllOffersOfSkill(1L, 1L)).thenReturn(threeOffers);
        Mockito.doNothing().when(skillServiceTest).addGuarantee(threeOffers);
        Mockito.doReturn(skillMapper.toDto(skill)).when(skillServiceTest).getSkillById(1L);
        skillServiceTest.acquireSkillFromOffers(1L, 1L);
        Mockito.verify(skillRepository, Mockito.times(1)).assignSkillToUser(1L, 1L);
        Mockito.verify(skillServiceTest, Mockito.times(1)).addGuarantee(threeOffers);
    }

    @Test
    public void testMapperSkillToDto() {
        Skill skill = new Skill();
        skill.setTitle("title");
        skill.setId(1L);
        SkillDto skillDto = skillMapper.toDto(skill);
        assertThat(skill.getId() == skillDto.getId()).isTrue();
        assertThat(skill.getTitle().equals(skillDto.getTitle())).isTrue();
    }

    @Test
    public void testMapperDtoToSkill() {
        SkillDto skillDto = new SkillDto(1L, "title");
        Skill skill = skillMapper.toEntity(skillDto);
        assertThat(skill.getTitle().equals(skillDto.getTitle())).isTrue();
        assertThat(skill.getId() == skillDto.getId()).isTrue();
    }

    @Test
    public void testMapperSkillToSkillCandidateDto() {
        Skill skill = new Skill();
        skill.setId(1L);
        skill.setTitle("title");
        SkillCandidateDto expected = new SkillCandidateDto();
        expected.setSkill(new SkillDto(1L, "title"));
        expected.setOffersAmount(1L);
        SkillCandidateDto actual = skillCandidateMapper.skillToSkillCandidateDto(skill);
        assertThat(actual.getSkill().equals(expected.getSkill())).isTrue();
        assertThat(actual.getOffersAmount() == expected.getOffersAmount()).isTrue();
    }

    @Test
    public void testMapperListOfSkillsToCandidateDto() {
        Skill skill = new Skill();
        skill.setId(1L);
        skill.setTitle("title");
        SkillCandidateDto skillCandidateDto = skillCandidateMapper.skillToSkillCandidateDto(skill);
        skillCandidateDto.setOffersAmount(2);
        List<Skill> skills = new ArrayList<>();
        skills.add(skill);
        skills.add(skill);
        List<SkillCandidateDto> actual = listOfSkillsCandidateMapper.listToSkillCandidateDto(skills);
        List<SkillCandidateDto> expected = new ArrayList<>();
        expected.add(skillCandidateDto);
        Assertions.assertEquals(expected, actual);
    }
}
