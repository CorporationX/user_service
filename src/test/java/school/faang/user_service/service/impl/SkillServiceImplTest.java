package school.faang.user_service.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.entity.Skill;
import school.faang.user_service.dto.entity.UserSkillGuarantee;
import school.faang.user_service.dto.entity.recommendation.SkillOffer;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SkillServiceImplTest {

    @InjectMocks
    private SkillServiceImpl skillService;

    @Mock
    private SkillRepository skillRepository;
    @Mock
    private SkillOfferRepository skillOfferRepository;
    @Mock
    private UserSkillGuaranteeRepository skillGuaranteeRepository;
    @Mock
    private SkillMapper skillMapper;

    @Test
    void test_create_success() {
        String title = "title";
        SkillDto dto = new SkillDto();
        dto.setTitle(title);
        Skill skill = new Skill();

        when(skillRepository.existsByTitle(title)).thenReturn(false);
        when(skillMapper.toSkillEntity(dto)).thenReturn(skill);
        when(skillRepository.save(skill)).thenReturn(skill);
        when(skillMapper.toSkillDto(skill)).thenReturn(dto);

        SkillDto result = skillService.create(dto);

        assertNotNull(result);
        assertEquals(title, result.getTitle());

        verify(skillRepository, times(1)).existsByTitle(title);
        verify(skillRepository, times(1)).save(skill);
        verify(skillMapper, times(1)).toSkillEntity(dto);
        verify(skillMapper, times(1)).toSkillDto(skill);
    }

    @Test
    void test_create_throwsException_whenSkillExists() {
        String title = "title";
        SkillDto dto = new SkillDto();
        dto.setTitle(title);

        when(skillRepository.existsByTitle(title)).thenReturn(true);

        assertThrows(DataValidationException.class, () -> skillService.create(dto));

        verify(skillRepository, times(1)).existsByTitle(title);
        verify(skillRepository, never()).save(any());
    }

    @Test
    void test_getUserSkills() {
        long userId = 1L;
        List<Skill> skills = List.of(new Skill());
        List<SkillDto> skillDtos = List.of(new SkillDto());

        when(skillRepository.findAllByUserId(userId)).thenReturn(skills);
        when(skillMapper.toSkillListDto(skills)).thenReturn(skillDtos);

        List<SkillDto> result = skillService.getUserSkills(userId);

        assertNotNull(result);
        assertEquals(skillDtos.size(), result.size());

        verify(skillRepository, times(1)).findAllByUserId(userId);
        verify(skillMapper, times(1)).toSkillListDto(skills);
    }

    @Test
    void test_getOfferedSkills() {
        long userId = 1L;
        Skill skill = new Skill();
        List<Skill> skills = List.of(skill);
        SkillDto skillDto = new SkillDto();
        long offersAmount = 1L;

        when(skillRepository.findSkillsOfferedToUser(userId)).thenReturn(skills);
        when(skillMapper.toSkillDto(skill)).thenReturn(skillDto);

        List<SkillCandidateDto> result = skillService.getOfferedSkills(userId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(skillDto, result.get(0).getSkill());
        assertEquals(offersAmount, result.get(0).getOffersAmount());

        verify(skillRepository, times(1)).findSkillsOfferedToUser(userId);
        verify(skillMapper, times(skills.size())).toSkillDto(any(Skill.class));
    }

    @Test
    void test_acquireSkillFromOffers_whenSkillAlreadyExists() {
        long skillId = 1L;
        long userId = 1L;
        Skill skill = new Skill();
        SkillDto skillDto = new SkillDto();

        when(skillRepository.findUserSkill(skillId, userId)).thenReturn(Optional.of(skill));
        when(skillMapper.toSkillDto(skill)).thenReturn(skillDto);

        SkillDto result = skillService.acquireSkillFromOffers(skillId, userId);

        assertNotNull(result);
        assertEquals(skillDto, result);

        verify(skillRepository, times(1)).findUserSkill(skillId, userId);
        verify(skillMapper, times(1)).toSkillDto(skill);

        verify(skillOfferRepository, never()).findAllOffersOfSkill(anyLong(), anyLong());
        verify(skillRepository, never()).assignSkillToUser(anyLong(), anyLong());
        verify(skillGuaranteeRepository, never()).save(any(UserSkillGuarantee.class));
    }

    @Test
    void test_acquireSkillFromOffers_success() {
        long skillId = 1L;
        long userId = 1L;
        Skill skill = new Skill();
        SkillDto skillDto = new SkillDto();
        SkillOffer offer1 = new SkillOffer();
        SkillOffer offer2 = new SkillOffer();
        SkillOffer offer3 = new SkillOffer();
        List<SkillOffer> skillOffers = List.of(offer1, offer2, offer3);

        when(skillRepository.findUserSkill(skillId, userId))
                .thenReturn(Optional.empty())
                .thenReturn(Optional.of(skill));
        when(skillOfferRepository.findAllOffersOfSkill(skillId, userId)).thenReturn(skillOffers);
        when(skillMapper.toSkillDto(skill)).thenReturn(skillDto);

        SkillDto result = skillService.acquireSkillFromOffers(skillId, userId);

        assertNotNull(result);
        assertEquals(skillDto, result);

        verify(skillRepository, times(2)).findUserSkill(skillId, userId);
        verify(skillOfferRepository, times(1)).findAllOffersOfSkill(skillId, userId);
        verify(skillRepository, times(1)).assignSkillToUser(skillId, userId);
        verify(skillGuaranteeRepository, times(skillOffers.size())).save(any(UserSkillGuarantee.class));
        verify(skillMapper, times(1)).toSkillDto(skill);
    }

    @Test
    void test_acquireSkillFromOffers_throwsException_whenNotEnoughOffers() {
        long skillId = 1L;
        long userId = 1L;
        SkillOffer offer1 = new SkillOffer();
        List<SkillOffer> skillOffers = List.of(offer1);

        when(skillRepository.findUserSkill(skillId, userId)).thenReturn(Optional.empty());
        when(skillOfferRepository.findAllOffersOfSkill(skillId, userId)).thenReturn(skillOffers);

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> skillService.acquireSkillFromOffers(skillId, userId));

        assertEquals("Недостаточно предложений для приобретения навыка", exception.getMessage());

        verify(skillRepository, times(1)).findUserSkill(skillId, userId);
        verify(skillOfferRepository, times(1)).findAllOffersOfSkill(skillId, userId);
        verify(skillRepository, never()).assignSkillToUser(anyLong(), anyLong());
        verify(skillGuaranteeRepository, never()).save(any(UserSkillGuarantee.class));
        verify(skillMapper, never()).toSkillDto(any());
    }

}