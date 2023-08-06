package school.faang.user_service.service;

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
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.mapper.SkillMapperImpl;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Answers.RETURNS_DEEP_STUBS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.withSettings;

@ExtendWith(MockitoExtension.class)
class SkillServiceTest {
    @InjectMocks
    private SkillService skillService;
    @Mock
    private SkillRepository skillRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserSkillGuaranteeRepository userSkillGuaranteeRepository;
    @Mock
    private SkillOfferRepository skillOfferRepository;
    @Spy
    private SkillMapper mapper = new SkillMapperImpl();


    @Test
    void testCreate() {
        SkillDto skillDto = new SkillDto(1L, "Programming^%$^*^^£     C++, Python/C# Мой Скилл.    ");
        SkillDto skillDtoProcessed = new SkillDto(1L, "programming c++, python/c# мой скилл.");
        skillService.create(skillDto);
        Mockito.verify(skillRepository, Mockito.times(1)).save(mapper.toEntity(skillDtoProcessed));
    }

    @Test
    void testIsExistingSkill() {
        Mockito.when(skillRepository.existsByTitle("programming")).thenReturn(true);
        SkillDto skillDto = new SkillDto(1L, "Programming");
        assertThrows(DataValidationException.class, () -> skillService.create(skillDto));
    }

    @Test
    void testGetUserSkills() {
        long userId = 1L;
        Skill skill = Skill.builder()
                .id(1L)
                .title("Programming")
                .build();
        Skill skill1 = Skill.builder()
                .id(2L)
                .title("SomeTitle")
                .build();
        SkillDto skillDto1 = mapper.toDto(skill);
        SkillDto skillDto2 = mapper.toDto(skill1);
        Mockito.when(userRepository.existsById(any())).thenReturn(true);
        Mockito.when(skillRepository.findAllByUserId(userId)).thenReturn(List.of(skill, skill1));

        List<SkillDto> skills = List.of(skillDto1, skillDto2);
        assertEquals(skills, skillService.getUserSkills(userId));
        Mockito.verify(skillRepository).findAllByUserId(userId);
    }

    @Test
    void testGetUserSkillsThrownUserDoesNotExistException() {
        long userId = 1L;
        Mockito.when(userRepository.existsById(any())).thenReturn(false);
        DataValidationException dataValidationException =
                assertThrows(DataValidationException.class, () -> skillService.getUserSkills(userId));
        Assertions.assertEquals(String.format("User with id=%d doesn't exist", userId), dataValidationException.getMessage());
    }

    @Test
    void testGetOfferedSkills() {
        Skill skill1 = Skill.builder()
                .id(1L)
                .title("One")
                .build();
        Skill skill2 = Skill.builder()
                .id(2L)
                .title("Two")
                .build();
        SkillOffer skillOffer = SkillOffer.builder()
                .skill(skill1)
                .build();
        SkillOffer skillOffer1 = SkillOffer.builder()
                .skill(skill2)
                .build();
        SkillOffer skillOffer2 = SkillOffer.builder()
                .skill(skill1)
                .build();
        SkillDto skillDto1 = mapper.toDto(skill1);
        SkillCandidateDto skillCandidateDto1 = new SkillCandidateDto(skillDto1, 2L);
        List<SkillOffer> skillOffers = new ArrayList<>();
        skillOffers.add(skillOffer);
        skillOffers.add(skillOffer1);
        skillOffers.add(skillOffer2);

        Mockito.when(skillOfferRepository.findAllByUserId(1L)).thenReturn(skillOffers);

        assertEquals(skillCandidateDto1, skillService.getOfferedSkills(1L).get(1));
        Mockito.verify(skillOfferRepository, Mockito.times(1))
                .findAllByUserId(1L);
    }

    @Test
    void testAcquireSkillFromOffers() {
        long skillId = 1L;
        long userId = 1L;
        User user = new User();
        user.setId(1L);
        Skill skill1 = Skill.builder()
                .id(1L)
                .title("One")
                .users(List.of(user))
                .build();

        SkillDto skillDto1 = new SkillDto(1L, "One");
        List<SkillOffer> offers = List.of(
                mock(SkillOffer.class, withSettings().defaultAnswer(RETURNS_DEEP_STUBS)),
                mock(SkillOffer.class, withSettings().defaultAnswer(RETURNS_DEEP_STUBS)),
                mock(SkillOffer.class, withSettings().defaultAnswer(RETURNS_DEEP_STUBS))
        );

        Mockito.when(skillRepository.findUserSkill(skillId, userId)).thenReturn(Optional.empty());
        Mockito.when(skillOfferRepository.findAllOffersOfSkill(skillId, userId)).thenReturn(offers);
        Mockito.when(skillRepository.findById(userId)).thenReturn(Optional.of(skill1));
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        Mockito.when(mapper.toDto(skill1)).thenReturn(skillDto1);

        SkillDto result = skillService.acquireSkillFromOffers(skillId, userId);
        Assertions.assertEquals(skillDto1, result);
        Mockito.verify(userSkillGuaranteeRepository, Mockito.times(3)).save(any());
        Mockito.verify(skillRepository, Mockito.times(1)).assignSkillToUser(skillId, userId);
    }

    @Test
    void testThrowsNotEnoughRecommendations() {
        long skillId = 1L;
        long userId = 1L;
        List<SkillOffer> offers = List.of(
                new SkillOffer(), new SkillOffer()
        );

        Mockito.when(skillRepository.findUserSkill(skillId, userId)).thenReturn(Optional.empty());
        Mockito.when(skillOfferRepository.findAllOffersOfSkill(skillId, userId)).thenReturn(offers);
        DataValidationException dataValidationException = assertThrows(DataValidationException.class,
                () -> skillService.acquireSkillFromOffers(skillId, userId));
        Assertions.assertEquals("Not enough offers to add skill", dataValidationException.getMessage());
    }
}