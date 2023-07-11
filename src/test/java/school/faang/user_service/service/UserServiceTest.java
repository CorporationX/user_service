package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidException;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private SkillOfferRepository skillOfferRepository;
    @Mock
    private SkillRepository skillRepository;
    @Mock
    private UserSkillGuaranteeRepository userSkillGuaranteeRepository;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private SkillService skillService;

    @Test
    void createTest_Should_Return_SkillDto() {
        SkillDto skillDto = new SkillDto(1L, "title");
        Skill skill = SkillMapper.INSTANCE.toEntity(skillDto);

        when(skillRepository.existsByTitle(skillDto.getTitle())).thenReturn(false);
        when(skillRepository.save(skill)).thenReturn(skill);

        SkillDto result = skillService.create(skillDto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("title", result.getTitle());

        verify(skillRepository).existsByTitle(skillDto.getTitle());
        verify(skillRepository).save(any(Skill.class));
    }

    @Test
    void createTest_Should_Throw_DataValidException() {
        SkillDto skillDto = new SkillDto(1L, "title");

        when(skillRepository.existsByTitle(skillDto.getTitle())).thenReturn(true);

        assertThrows(DataValidException.class, () -> {
            skillService.create(skillDto);
        });

        verify(skillRepository).existsByTitle(skillDto.getTitle());
    }

    @Test
    void getUserSkillsTest() {
        long userId = 1L;
        List<Skill> skills = List.of(
                Skill.builder().title("Skill1").build(),
                Skill.builder().title("Skill2").build()
        );

        when(skillRepository.findAllByUserId(userId)).thenReturn(skills);

        List<SkillDto> userSkills = skillService.getUserSkills(userId);

        assertEquals(2, userSkills.size());

        verify(skillRepository).findAllByUserId(userId);
    }

    @Test
    void getOfferedSkillsTest() {
        List<SkillOffer> offers = List.of(
                mock(SkillOffer.class, withSettings().defaultAnswer(RETURNS_DEEP_STUBS)),
                mock(SkillOffer.class, withSettings().defaultAnswer(RETURNS_DEEP_STUBS)),
                mock(SkillOffer.class, withSettings().defaultAnswer(RETURNS_DEEP_STUBS))
        );

        when(skillOfferRepository.findAllOffersToUser(anyLong())).thenReturn(offers);

        List<SkillCandidateDto> candidates = skillService.getOfferedSkills(anyLong());

        assertNotNull(candidates);
        assertEquals(3, candidates.size());

        verify(skillOfferRepository).findAllOffersToUser(anyLong());
    }

    @Test
    public void testAcquireSkillFromOffers() {
        List<SkillOffer> offers = List.of(
                mock(SkillOffer.class, withSettings().defaultAnswer(RETURNS_DEEP_STUBS)),
                mock(SkillOffer.class, withSettings().defaultAnswer(RETURNS_DEEP_STUBS)),
                mock(SkillOffer.class, withSettings().defaultAnswer(RETURNS_DEEP_STUBS))
        );
        User user = mock(User.class);
        Skill skill = mock(Skill.class);

        when(skillRepository.findUserSkill(anyLong(), anyLong())).thenReturn(Optional.empty())
                .thenReturn(Optional.of(Skill.builder().id(1L).build()));
        when(skillOfferRepository.findAllOffersOfSkill(anyLong(), anyLong())).thenReturn(offers);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(skillRepository.findById(anyLong())).thenReturn(Optional.of(skill));

        SkillDto acquiredSkill = skillService.acquireSkillFromOffers(anyLong(), anyLong());

        assertNotNull(acquiredSkill);
        assertEquals(1L, acquiredSkill.getId());

        verify(skillRepository, times(2)).findUserSkill(anyLong(), anyLong());
        verify(skillOfferRepository).findAllOffersOfSkill(anyLong(), anyLong());
        verify(userRepository).findById(anyLong());
        verify(skillRepository).assignSkillToUser(anyLong(), anyLong());
        verify(userSkillGuaranteeRepository, times(3)).save(any(UserSkillGuarantee.class));
        verify(skillOfferRepository, times(3)).deleteById(anyLong());
    }
}