package school.faang.user_service.service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.service.SkillService;
import school.faang.user_service.service.UserService;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SkillServiceTest {

    @Mock
    private SkillRepository skillRepository;

    @Mock
    private SkillMapper skillMapper;

    @Mock
    private SkillOfferRepository skillOfferRepository;

    @Mock
    private UserSkillGuaranteeRepository userSkillGuaranteeRepository;

    @Mock
    private UserService userService;

    private SkillService skillService;

    @BeforeEach
    public void setUp() {
        skillService = new SkillService(skillRepository, skillMapper, skillOfferRepository, userSkillGuaranteeRepository, userService);
    }

    @Test
    public void testCreateSkill() {
        SkillDto skillDto = new SkillDto(1L, "Java");
        Skill skill = new Skill();
        skill.setId(1L);
        skill.setTitle("Java");

        when(skillRepository.existsByTitle(skillDto.getTitle())).thenReturn(false);
        when(skillMapper.toEntity(skillDto)).thenReturn(skill);
        when(skillRepository.save(skill)).thenReturn(skill);
        when(skillMapper.toDto(skill)).thenReturn(skillDto);

        SkillDto result = skillService.create(skillDto);

        assertEquals(skillDto, result);
        verify(skillRepository, times(1)).existsByTitle(skillDto.getTitle());
        verify(skillMapper, times(1)).toEntity(skillDto);
        verify(skillRepository, times(1)).save(skill);
        verify(skillMapper, times(1)).toDto(skill);
    }

    @Test
    public void testCreateSkillWithExistingTitle() {
        SkillDto skillDto = new SkillDto(1L, "Java");

        when(skillRepository.existsByTitle(skillDto.getTitle())).thenReturn(true);

        assertThrows(DataValidationException.class, () -> skillService.create(skillDto));
        verify(skillRepository, times(1)).existsByTitle(skillDto.getTitle());
        verify(skillMapper, never()).toEntity(any());
        verify(skillRepository, never()).save(any());
        verify(skillMapper, never()).toDto(any());
    }

    @Test
    public void testGetUserSkills() {
        long userId = 1L;
        Skill skill = new Skill();
        skill.setId(1L);
        skill.setTitle("Java");
        SkillDto skillDto = new SkillDto(1L, "Java");

        when(skillRepository.findAllByUserId(userId)).thenReturn(Collections.singletonList(skill));
        when(skillMapper.toDto(skill)).thenReturn(skillDto);

        List<SkillDto> result = skillService.getUserSkills(userId);

        assertEquals(1, result.size());
        assertEquals(skillDto, result.get(0));
        verify(skillRepository, times(1)).findAllByUserId(userId);
        verify(skillMapper, times(1)).toDto(skill);
    }

    @Test
    public void testGetOfferedSkills() {
        long userId = 1L;
        Skill skill = new Skill();
        skill.setId(1L);
        skill.setTitle("Java");
        SkillDto skillDto = new SkillDto(1L, "Java");

        when(skillRepository.findSkillsOfferedToUser(userId)).thenReturn(Collections.singletonList(skill));
        when(skillMapper.toDto(skill)).thenReturn(skillDto);

        List<SkillCandidateDto> result = skillService.getOfferedSkills(userId);

        assertEquals(1, result.size());
        assertEquals(skillDto, result.get(0).getSkill());
        assertEquals(1, result.get(0).getOffersAmount());
        verify(skillRepository, times(1)).findSkillsOfferedToUser(userId);
        verify(skillMapper, times(1)).toDto(skill);
    }

    @Test
    public void testAcquireSkillFromOffers() {
        long skillId = 1L;
        long userId = 1L;
        Skill skill = new Skill();
        skill.setId(skillId);
        skill.setTitle("Java");
        SkillDto skillDto = new SkillDto(skillId, "Java");
        User user = new User();
        user.setId(userId);
        SkillOffer offer1 = new SkillOffer();
        offer1.setSkill(skill);
        offer1.setRecommendation(new Recommendation());
        offer1.getRecommendation().setAuthor(new User());
        SkillOffer offer2 = new SkillOffer();
        offer2.setSkill(skill);
        offer2.setRecommendation(new Recommendation());
        offer2.getRecommendation().setAuthor(new User());
        SkillOffer offer3 = new SkillOffer();
        offer3.setSkill(skill);
        offer3.setRecommendation(new Recommendation());
        offer3.getRecommendation().setAuthor(new User());
        List<SkillOffer> offers = Arrays.asList(offer1, offer2, offer3);

        when(skillRepository.findUserSkill(skillId, userId)).thenReturn(Optional.of(skill));
        when(skillOfferRepository.findAllOffersOfSkill(skillId, userId)).thenReturn(offers);
        when(userService.getUserById(userId)).thenReturn(user);
        when(skillMapper.toDto(skill)).thenReturn(skillDto);

        SkillDto result = skillService.acquireSkillFromOffers(skillId, userId);

        assertEquals(skillDto, result);
        verify(skillRepository, times(1)).findUserSkill(skillId, userId);
        verify(skillOfferRepository, times(1)).findAllOffersOfSkill(skillId, userId);
        verify(skillRepository, never()).assignSkillToUser(anyLong(), anyLong());
        verify(userService, times(1)).getUserById(userId);
        verify(skillMapper, times(1)).toDto(skill);
        verify(userSkillGuaranteeRepository, times(3)).save(any(UserSkillGuarantee.class));
    }

    @Test
    public void testAcquireSkillFromOffersWithNotEnoughOffers() {
        long skillId = 1L;
        long userId = 1L;
        List<SkillOffer> offers = Collections.singletonList(new SkillOffer());

        when(skillRepository.findUserSkill(skillId, userId)).thenReturn(Optional.empty());
        when(skillOfferRepository.findAllOffersOfSkill(skillId, userId)).thenReturn(offers);

        assertThrows(EntityNotFoundException.class, () -> skillService.acquireSkillFromOffers(skillId, userId));
        verify(skillRepository, times(1)).findUserSkill(skillId, userId);
        verify(skillOfferRepository, times(1)).findAllOffersOfSkill(skillId, userId);
        verify(skillRepository, never()).assignSkillToUser(anyLong(), anyLong());
        verify(userService, never()).getUserById(anyLong());
        verify(skillMapper, never()).toDto(any());
        verify(userSkillGuaranteeRepository, never()).save(any(UserSkillGuarantee.class));
    }
}