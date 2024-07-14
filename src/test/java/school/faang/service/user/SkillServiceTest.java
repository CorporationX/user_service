package school.faang.service.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.service.user.dto.skill.SkillCandidateDto;
import school.faang.service.user.dto.skill.SkillDto;
import school.faang.service.user.entity.Skill;
import school.faang.service.user.entity.User;
import school.faang.service.user.entity.UserSkillGuarantee;
import school.faang.service.user.entity.recommendation.Recommendation;
import school.faang.service.user.entity.recommendation.SkillOffer;
import school.faang.service.user.exception.DataValidationException;
import school.faang.service.user.mapper.SkillMapper;
import school.faang.service.user.repository.SkillRepository;
import school.faang.service.user.repository.UserSkillGuaranteeRepository;
import school.faang.service.user.repository.recommendation.SkillOfferRepository;
import school.faang.service.user.service.SkillService;

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
    long firstSkillId;
    long secondSkillId;
    private Skill firstSkill;
    private Skill secondSkill;
    private SkillDto skillDto;
    private SkillDto secondSkillDto;
    private SkillOffer skillOffer;
    private SkillOffer skillOffer2;
    private SkillOffer skillOffer3;
    private SkillOffer skillOffer4;

    @BeforeEach
    void setUp() {
        userId = 1L;
        firstSkillId = 1L;
        firstSkill = new Skill();
        firstSkill.setId(firstSkillId);
        firstSkill.setTitle("Java");

        secondSkillId = 2L;
        secondSkill = new Skill();
        secondSkill.setId(secondSkillId);
        secondSkill.setTitle("Spring");

        skillDto = new SkillDto(firstSkillId, "Java");
        secondSkillDto = new SkillDto(secondSkillId, "Spring");

        Recommendation recommendation1 = Recommendation.builder().receiver(User.builder().id(userId).username("Ivan").build()).build();
        Recommendation recommendation2 = Recommendation.builder().receiver(User.builder().id(userId).username("Ivan").build()).build();
        Recommendation recommendation3 = Recommendation.builder().receiver(User.builder().id(userId).username("Ivan").build()).build();
        Recommendation recommendation4 = Recommendation.builder().receiver(User.builder().id(userId).username("Ivan").build()).build();

        skillOffer = new SkillOffer(1L, firstSkill, recommendation1);
        skillOffer2 = new SkillOffer(1L, firstSkill, recommendation2);
        skillOffer3 = new SkillOffer(1L, firstSkill, recommendation3);
        skillOffer4 = new SkillOffer(1L, firstSkill, recommendation4);
    }

    @Test
    void testCreateSkillsSuccess() {
        when(skillRepository.existsByTitle("Java")).thenReturn(false);
        when(skillMapper.toEntity(skillDto)).thenReturn(firstSkill);
        when(skillRepository.save(firstSkill)).thenReturn(firstSkill);
        when(skillMapper.toDto(firstSkill)).thenReturn(skillDto);

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
        when(skillRepository.findAllByUserId(userId)).thenReturn(List.of(firstSkill, secondSkill));
        when(skillMapper.toDto(firstSkill)).thenReturn(skillDto);
        when(skillMapper.toDto(secondSkill)).thenReturn(secondSkillDto);

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
        List<Skill> skillsOfferedToUser = Arrays.asList(firstSkill, secondSkill);

        when(skillRepository.findSkillsOfferedToUser(userId)).thenReturn(skillsOfferedToUser);
        when(skillMapper.toDto(firstSkill)).thenReturn(skillDto);
        when(skillMapper.toDto(secondSkill)).thenReturn(secondSkillDto);

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
        when(skillRepository.findUserSkill(firstSkillId, userId)).thenReturn(Optional.of(firstSkill));
        when(skillMapper.toDto(firstSkill)).thenReturn(skillDto);

        SkillDto result = skillService.acquireSkillFromOffers(firstSkillId, userId);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(firstSkillId);

        verify(skillRepository).findUserSkill(firstSkillId, userId);
        verify(skillMapper).toDto(firstSkill);
    }

    @Test
    void testAcquireSkillFromOffersSuccess() {
        List<SkillOffer> offers = List.of(skillOffer, skillOffer2, skillOffer3, skillOffer4);

        when(skillRepository.findUserSkill(firstSkillId, userId)).thenReturn(Optional.empty());
        when(skillOfferRepository.findAllOffersOfSkill(firstSkillId, userId)).thenReturn(offers);
        when(skillRepository.findById(firstSkillId)).thenReturn(Optional.of(firstSkill));
        when(skillMapper.toDto(firstSkill)).thenReturn(skillDto);

        SkillDto result = skillService.acquireSkillFromOffers(firstSkillId, userId);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(firstSkillId);
        assertThat(result).isEqualTo(skillDto);


        verify(skillRepository).findUserSkill(firstSkillId, userId);
        verify(skillOfferRepository).findAllOffersOfSkill(firstSkillId, userId);
        verify(skillRepository).assignSkillToUser(firstSkillId, userId);
        verify(skillMapper).toDto(firstSkill);
        verify(skillGuaranteeRepository, times(offers.size())).save(any(UserSkillGuarantee.class));
    }
}
