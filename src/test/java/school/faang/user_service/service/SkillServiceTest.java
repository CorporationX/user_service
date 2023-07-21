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
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SkillServiceTest {
    @InjectMocks
    private SkillService skillService;
    @Mock
    private SkillRepository skillRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private SkillOfferRepository skillOfferRepository;
    @Mock
    private UserSkillGuaranteeRepository userSkillGuaranteeRepository;
    @Spy
    private SkillMapper skillMapper = Mappers.getMapper(SkillMapper.class);

    @Test
    void testCreateSkillToDb() {
        SkillDto skillDto = new SkillDto(1L, "Skill", List.of(1L), null, null);
        Skill skill = skillMapper.toEntity(skillDto);

        User user = new User();
        user.setId(1L);
        List<User> users = List.of(user);
        skill.setUsers(users);

        when(skillRepository.existsByTitle(skillDto.getTitle())).thenReturn(false);
        when(skillRepository.save(skill)).thenReturn(skill);
        when(userRepository.findAllById(skillDto.getUserIds())).thenReturn(users);

        SkillDto createdSkill = skillService.create(skillDto);

        assertNotNull(createdSkill);
        assertEquals(skillDto.getTitle(), createdSkill.getTitle());
        assertEquals(skillDto.getUserIds(), createdSkill.getUserIds());
    }

    @Test
    void testValidationSkillWithBlankTitle() {
        SkillDto skillDto = new SkillDto(1L, "", List.of(1L), null, null);

        DataValidationException validationException = assertThrows(DataValidationException.class,
                () -> skillService.create(skillDto));

        assertEquals("Enter skill title, please", validationException.getMessage());
    }

    @Test
    void testValidationSkillNotFoundTitle() {
        SkillDto skillDto = new SkillDto(1L, "Skill", List.of(1L), null, null);

        when(skillRepository.existsByTitle(anyString())).thenReturn(true);

        DataValidationException validationException = assertThrows(DataValidationException.class,
                () -> skillService.create(skillDto));

        assertEquals("Skill with title " + skillDto.getTitle() + " already exists",
                validationException.getMessage());
    }

    @Test
    void testGetUserSkills() {
        long userId = 1L;
        User user = new User();
        user.setId(userId);

        List<Skill> skills = List.of(
                new Skill(1L, "Hard Skill", List.of(user),
                        null, null, null, null, null),
                new Skill(2L, "Soft Skill", List.of(user),
                        null, null, null, null, null)
        );

        when(skillRepository.findAllByUserId(userId)).thenReturn(skills);

        List<SkillDto> userSkillsDto = skillService.getUserSkills(userId);

        assertNotNull(userSkillsDto);
        assertEquals(skills.size(), userSkillsDto.size());
        assertEquals(skills.get(0).getTitle(), userSkillsDto.get(0).getTitle());
        assertEquals(skills.get(1).getTitle(), userSkillsDto.get(1).getTitle());
    }

    @Test
    void testGetOfferedSkills() {
        long userId = 1L;
        User user = new User();
        user.setId(userId);

        List<Skill> skillsOfferedToUser = List.of(
                new Skill(1L, "Hard Skill", List.of(user),
                        null, null, null, null, null),
                new Skill(2L, "Soft Skill", List.of(user),
                        null, null, null, null, null),
                new Skill(1L, "Hard Skill", List.of(user),
                        null, null, null, null, null)
        );

        when(skillRepository.findSkillsOfferedToUser(userId)).thenReturn(skillsOfferedToUser);

        List<SkillCandidateDto> offeredSkillsDto = skillService.getOfferedSkills(userId);

        String expectedTitle = "Soft Skill";
        String actualTitle = offeredSkillsDto.get(0).getSkill().getTitle();

        assertNotNull(offeredSkillsDto);
        assertEquals(2, offeredSkillsDto.size());
        assertEquals(1, offeredSkillsDto.get(0).getOffersAmount());
        assertEquals(2, offeredSkillsDto.get(1).getOffersAmount());
        assertEquals(expectedTitle, actualTitle);
    }

    @Test
    void testAcquireSkillFromOffers() {
        long userId = 1L;
        long skillId = 1L;
        SkillDto skillDto = new SkillDto(1L, "Hard Skill", List.of(1L), null, null);
        SkillOffer skillOffer = new SkillOffer(1L, new Skill(), new Recommendation());

        List<SkillOffer> skillOffers = List.of(skillOffer, skillOffer, skillOffer);

        when(skillRepository.findUserSkill(skillId, userId)).thenReturn(Optional.empty());
        when(skillOfferRepository.findAllOffersOfSkill(skillId, userId)).thenReturn(skillOffers);
        when(skillMapper.toDto(any())).thenReturn(skillDto);

        SkillDto acquireSkillDto = skillService.acquireSkillFromOffers(skillId, userId);

        assertNotNull(skillDto);
        assertEquals(skillDto, acquireSkillDto);

        verify(skillRepository, times(1)).findUserSkill(skillId, userId);
        verify(skillOfferRepository, times(1)).findAllOffersOfSkill(skillId, userId);
        verify(skillMapper, times(1)).toDto(any());
        verify(skillRepository, times(1)).assignSkillToUser(skillId, userId);
    }

    @Test
    void testAcquireSkillFromOffersNotEnoughOffers() {
        long userId = 1L;
        long skillId = 1L;

        when(skillRepository.findUserSkill(skillId, userId)).thenReturn(Optional.empty());
        when(skillOfferRepository.findAllOffersOfSkill(skillId, userId)).thenReturn(List.of(new SkillOffer()));

        SkillDto acquireSkillDto = skillService.acquireSkillFromOffers(skillId, userId);

        verify(skillRepository, times(0)).assignSkillToUser(skillId, userId);
    }

    @Test
    void testAcquireSkillFromOffersException() {
        long userId = 1L;
        long skillId = 1L;

        when(skillRepository.findUserSkill(skillId, userId)).thenReturn(Optional.of(new Skill()));

        assertThrows(DataValidationException.class, () -> skillService.acquireSkillFromOffers(skillId, userId));
    }
}