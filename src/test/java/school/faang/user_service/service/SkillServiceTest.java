package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.skill.DataValidationException;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.service.skillService.SkillServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SkillServiceTest {

    @Mock
    private SkillRepository skillRepository;

    @Mock
    private SkillOfferRepository skillOfferRepository;

    @Mock
    private UserSkillGuaranteeRepository userSkillGuaranteeRepository;

    @Mock
    private UserRepository userRepository;

    @Spy
    private SkillMapper skillMapper = Mappers.getMapper(SkillMapper.class);

    @InjectMocks
    private SkillServiceImpl skillService;

    @Test
    public void testCreateWithInvalidTitle() {
        SkillDto skill = SkillDto.builder().title("").build();

        assertThrows(DataValidationException.class, () -> skillService.create(skill));
    }

    @Test
    public void testCreateWithExistingTitle() {
        SkillDto skill = SkillDto.builder().title("title").build();
        when(skillRepository.existsByTitle(skill.getTitle())).thenReturn(true);

       assertThrows(DataValidationException.class, () -> skillService.create(skill));
    }

    @Test
    public void testCreateSkillSuccessfully() {
        SkillDto skillDto = SkillDto.builder().title("title").build();
        Skill expectedSkill = Skill.builder().id(1L).title(skillDto.getTitle()).build();
        when(skillRepository.existsByTitle(skillDto.getTitle())).thenReturn(false);
        when(skillRepository.save(skillMapper.toSkill(skillDto))).thenReturn(expectedSkill);

        SkillDto actual = skillService.create(skillDto);

        assertEquals(expectedSkill.getId(), actual.getId());
        assertEquals(expectedSkill.getTitle(), actual.getTitle());
    }

    @Test
    public void testGetUsersSkillsWithNullableUserId() {
        Long userId = null;

        assertThrows(IllegalArgumentException.class, () -> skillService.getUsersSkills(userId));
    }

    @Test
    public void testGetUsersSkillsWithNotExistingUserId() {
        Long userId = 1L;
        when(userRepository.existsById(userId)).thenReturn(false);

        assertThrows(DataValidationException.class, () -> skillService.getUsersSkills(userId));
    }

    @Test
    public void testGetUsersSkillsSuccessfully() {
        Long userId = 2L;
        List<Skill> skillList = List.of(Skill.builder().id(2L).title("title").build());
        List<SkillDto> expectedList = List.of(SkillDto.builder().id(2L).title("title").build());
        when(userRepository.existsById(userId)).thenReturn(true);
        when(skillRepository.findAllByUserId(userId)).thenReturn(skillList);

        List<SkillDto> actualUserSkills = skillService.getUsersSkills(userId);

        assertIterableEquals(expectedList, actualUserSkills);
    }

    @Test
    public void testGetOfferedSkillsWithNullableUserId() {
        Long userId = null;

        assertThrows(IllegalArgumentException.class, () -> skillService.getOfferedSkills(userId));
    }

    @Test
    public void getOfferedSkillWithNotExistingUserId() {
        Long userId = 1L;
        when(userRepository.existsById(userId)).thenReturn(false);

        assertThrows(DataValidationException.class, () -> skillService.getOfferedSkills(userId));
    }

    @Test
    public void getOfferedSkillsSuccessfully() {
        Long userId = 1L;
        List<Skill> foundedOfferedSkills = List.of(Skill.builder().id(1L).title("title").build());
        List<SkillCandidateDto> expectedSkillCandidate = List.of(SkillCandidateDto.builder()
                .skill(SkillDto.builder().id(1L).title("title").build())
                .offersAmount(1)
                .build());
        when(userRepository.existsById(userId)).thenReturn(true);
        when(skillRepository.findSkillsOfferedToUser(userId)).thenReturn(foundedOfferedSkills);

        List<SkillCandidateDto> actualSkillCandidate = skillService.getOfferedSkills(userId);

        assertIterableEquals(expectedSkillCandidate, actualSkillCandidate);
    }

    @Test
    public void testAcquireSkillFromOffersWithNotExistingUserId() {
        long userId = 1L;
        long skillId = 1L;
        when(userRepository.existsById(userId)).thenReturn(false);

        assertThrows(DataValidationException.class, () -> skillService.acquireSkillFromOffers(userId, skillId));
    }

    @Test
    public void testAcquireSkillFromOffersWithNoExistingSkillId() {
        long userId = 1L;
        long skillId = 1L;
        when(userRepository.existsById(userId)).thenReturn(true);
        when(skillRepository.findById(skillId)).thenReturn(Optional.empty());

        assertThrows(DataValidationException.class, () -> skillService.acquireSkillFromOffers(skillId, userId));
    }

    @Test
    public void testAcquireSkillFromOffersUserWithoutSkill() {
        long userId = 1L;
        long skillId = 1L;
        when(userRepository.existsById(userId)).thenReturn(true);
        when(skillRepository.findById(skillId)).thenReturn(Optional.of(Skill.builder().build()));
        when(skillRepository.findUserSkill(skillId, userId)).thenReturn(Optional.of(Skill.builder().build()));

        assertThrows(DataValidationException.class, () -> skillService.acquireSkillFromOffers(skillId, userId));
    }
    @Test
    public void testAcquireSkillFromOffersWithoutEnoughOffers() {
        long userId = 1L;
        long skillId = 1L;
        when(userRepository.existsById(userId)).thenReturn(true);
        when(skillRepository.findById(skillId)).thenReturn(Optional.of(Skill.builder().build()));
        when(skillRepository.findUserSkill(skillId, userId)).thenReturn(Optional.empty());
        when(skillOfferRepository.findAllOffersOfSkill(userId, skillId)).thenReturn(List.of(SkillOffer.builder().build()));

        //Optional<SkillDto> actual = skillService.acquireSkillFromOffers(skillId, userId);

        assertThrows(DataValidationException.class, () -> skillService.acquireSkillFromOffers(skillId, userId));
    }

    @Test
    public void testAcquireSkillFromOffersWithtEnoughOffers() {
        long userId = 1L;
        long skillId = 1L;
        Skill skill = Skill.builder().id(skillId).title("title").build();
        User receiver = User.builder().id(userId).build();
        User author2 = User.builder().id(2L).build();
        User author3 = User.builder().id(3L).build();
        User author4 = User.builder().id(4L).build();
        Recommendation recommendation2 = Recommendation.builder().id(2L).receiver(receiver).author(author2).build();
        Recommendation recommendation3 = Recommendation.builder().id(3L).receiver(receiver).author(author3).build();
        Recommendation recommendation4 = Recommendation.builder().id(4L).receiver(receiver).author(author4).build();
        List<SkillOffer> offers = List.of(new SkillOffer(1L, skill, recommendation2), new SkillOffer(2L, skill, recommendation3), new SkillOffer(4L, skill, recommendation4));
        when(userRepository.existsById(userId)).thenReturn(true);
        when(skillRepository.findById(skillId)).thenReturn(Optional.of(skill));
        when(skillRepository.findUserSkill(skillId, userId)).thenReturn(Optional.empty());
        when(skillOfferRepository.findAllOffersOfSkill(userId, skillId)).thenReturn(offers);

        SkillDto actual = skillService.acquireSkillFromOffers(skillId, userId);

        assertEquals(skill.getId(), actual.getId());
        assertEquals(skill.getTitle(), actual.getTitle());
        verify(userSkillGuaranteeRepository, times(1)).saveAll(anyList());
    }
}
