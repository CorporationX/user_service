package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
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
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SkillServiceTest {
    @Mock
    private SkillRepository skillRepository;
    @Mock
    private SkillMapper skillMapper;
    @Mock
    private SkillOfferRepository skillOfferRepository;
    @Mock
    private UserSkillGuaranteeRepository skillGuaranteeRepository;
    @InjectMocks
    private SkillService skillService;
    long userId;
    long skillId;
    long skillId2;
    private Skill skill;
    private Skill skill2;
    private SkillDto skillDto;
    private SkillDto skillDto2;
    private SkillOffer skillOffer;
    private SkillOffer skillOffer2;
    private SkillOffer skillOffer3;
    private SkillOffer skillOffer4;

    @BeforeEach
    void setUp() {
        userId = 1L;
        skillId = 1L;
        skill = new Skill();
        skill.setId(skillId);
        skill.setTitle("Java");

        skillId2 = 2L;
        skill2 = new Skill();
        skill2.setId(skillId2);
        skill2.setTitle("Spring");

        skillDto = new SkillDto(skillId, "Java");
        skillDto2 = new SkillDto(skillId2, "Spring");

        Recommendation recommendation1 = Recommendation.builder().receiver(User.builder().id(userId).username("Ivan").build()).build();
        Recommendation recommendation2 = Recommendation.builder().receiver(User.builder().id(userId).username("Ivan").build()).build();
        Recommendation recommendation3 = Recommendation.builder().receiver(User.builder().id(userId).username("Ivan").build()).build();
        Recommendation recommendation4 = Recommendation.builder().receiver(User.builder().id(userId).username("Ivan").build()).build();

        skillOffer = new SkillOffer(1L, skill, recommendation1);
        skillOffer2 = new SkillOffer(1L, skill, recommendation2);
        skillOffer3 = new SkillOffer(1L, skill, recommendation3);
        skillOffer4 = new SkillOffer(1L, skill, recommendation4);
    }

    @Test
    void testCreateSkillsSuccess() {
        when(skillRepository.existsByTitle("Java")).thenReturn(false);
        when(skillMapper.toEntity(skillDto)).thenReturn(skill);
        when(skillRepository.save(skill)).thenReturn(skill);
        when(skillMapper.toDto(skill)).thenReturn(skillDto);

        SkillDto createdSkill = skillService.create(skillDto);

        assertThat(createdSkill.getTitle()).isEqualTo("Java");
        assertThat(createdSkill).isNotNull();

        verify(skillRepository).existsByTitle("Java");
        verify(skillMapper).toEntity(any());
        verify(skillRepository).save(any());
        verify(skillMapper).toDto(any());
    }

    @Test
    void testCreateSkillsAlreadyExists() {
        when(skillRepository.existsByTitle("Java")).thenReturn(true);

        assertThatThrownBy(() -> skillService.create(skillDto)).isInstanceOf(DataValidationException.class)
                .hasMessage("Skill with this title already exist");

        verify(skillRepository).existsByTitle("Java");
        verify(skillMapper, never()).toEntity(any());
        verify(skillRepository, never()).save(any());
        verify(skillMapper, never()).toDto(any());
    }

    @Test
    void testGetUserSkillsSuccess() {
        when(skillRepository.findAllByUserId(userId)).thenReturn(List.of(skill, skill2));
        when(skillMapper.toDto(skill)).thenReturn(skillDto);
        when(skillMapper.toDto(skill2)).thenReturn(skillDto2);

        List<SkillDto> result = skillService.getUserSkills(userId);

        assertThat(result.size()).isEqualTo(2);
        assertThat(result.get(0).getTitle()).isEqualTo("Java");
        assertThat(result.get(1).getTitle()).isEqualTo("Spring");
    }

    @Test
    void testGetUserSkillsEmptyList() {
        when(skillRepository.findAllByUserId(userId)).thenReturn(Collections.emptyList());

        List<SkillDto> result = skillService.getUserSkills(userId);

        assertThat(result.size()).isEqualTo(0);
    }

    @Test
    void testGetOfferedSkillsSuccess() {
        List<Skill> skillsOfferedToUser = Arrays.asList(skill, skill2);

        when(skillRepository.findSkillsOfferedToUser(userId)).thenReturn(skillsOfferedToUser);
        when(skillMapper.toDto(skill)).thenReturn(skillDto);
        when(skillMapper.toDto(skill2)).thenReturn(skillDto2);

        List<SkillCandidateDto> offeredSkills = skillService.getOfferedSkills(userId);
        assertThat(offeredSkills.size()).isEqualTo(2);

        SkillCandidateDto result1 = offeredSkills.get(0);
        assertThat(result1.getSkillDto().getTitle()).isEqualTo("Java");
        assertThat(result1.getOffersAmount()).isEqualTo(1);

        SkillCandidateDto result2 = offeredSkills.get(1);
        assertThat(result2.getSkillDto().getTitle()).isEqualTo("Spring");
        assertThat(result2.getOffersAmount()).isEqualTo(1);
    }

    @Test
    void testGetOfferedSkillsEmptyList() {
        when(skillRepository.findSkillsOfferedToUser(userId)).thenReturn(Collections.emptyList());

        List<SkillCandidateDto> offeredSkills = skillService.getOfferedSkills(userId);
        assertThat(offeredSkills.size()).isEqualTo(0);
    }

    @Test
    void testAcquireSkillFromOffers_ExistingSkill() {
        when(skillRepository.findUserSkill(skillId, userId)).thenReturn(Optional.of(skill));
        when(skillMapper.toDto(skill)).thenReturn(skillDto);

        SkillDto result = skillService.acquireSkillFromOffers(skillId, userId);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(skillId);

        verify(skillRepository).findUserSkill(skillId, userId);
        verify(skillMapper).toDto(skill);
    }

    @Test
    void testAcquireSkillFromOffersSuccess() {
        List<SkillOffer> offers = List.of(skillOffer, skillOffer2, skillOffer3, skillOffer4);

        when(skillRepository.findUserSkill(skillId, userId)).thenReturn(Optional.empty());
        when(skillOfferRepository.findAllOffersOfSkill(skillId, userId)).thenReturn(offers);
        when(skillRepository.findById(skillId)).thenReturn(Optional.of(skill));
        when(skillMapper.toDto(skill)).thenReturn(skillDto);

        SkillDto result = skillService.acquireSkillFromOffers(skillId, userId);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(skillId);
        assertThat(result).isEqualTo(skillDto);


        verify(skillRepository).findUserSkill(skillId, userId);
        verify(skillOfferRepository).findAllOffersOfSkill(skillId, userId);
        verify(skillRepository).assignSkillToUser(skillId, userId);
        verify(skillMapper).toDto(skill);
        verify(skillGuaranteeRepository, times(offers.size())).save(any(UserSkillGuarantee.class));
    }
}
