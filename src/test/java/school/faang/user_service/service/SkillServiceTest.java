package school.faang.user_service.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.dto.skill.SkillCandidateDto;
import school.faang.user_service.entity.dto.skill.SkillDto;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class SkillServiceTest {

    @Mock
    private SkillRepository skillRepository;

    @Mock
    private SkillOfferRepository skillOfferRepository;

    @Mock
    private SkillMapper skillMapper;

    @InjectMocks
    private SkillService skillService;
    private SkillOffer skillOffer1;
    private SkillOffer skillOffer2;
    private SkillOffer skillOffer3;
    private SkillOffer skillOffer4;
    private Skill skill;

    @Mock
    private UserSkillGuaranteeRepository userSkillGuaranteeRepository;

    @BeforeEach
    void setUp() {
        skill = new Skill(1L, "One", null, null, null, null, null, null);
        Recommendation recommendation1 = Recommendation.builder().receiver(User.builder().id(1L).username("Вася").build()).build();
        Recommendation recommendation2 = Recommendation.builder().receiver(User.builder().id(1L).username("Вася").build()).build();
        Recommendation recommendation3 = Recommendation.builder().receiver(User.builder().id(1L).username("Вася").build()).build();
        Recommendation recommendation4 = Recommendation.builder().receiver(User.builder().id(1L).username("Вася").build()).build();
        skillOffer1 = new SkillOffer(1L, skill, recommendation1);
        skillOffer2 = new SkillOffer(1L, skill, recommendation2);
        skillOffer3 = new SkillOffer(1L, skill, recommendation3);
        skillOffer4 = new SkillOffer(1L, skill, recommendation4);
    }

    @Test
    void testExistsByTitle() {
        Mockito.when(skillRepository.existsByTitle("crek")).thenReturn(true);

        assertThrows(
                DataValidationException.class,
                () -> skillService.create(new SkillDto(1L, "crek"))
        );
    }

    @Test
    void testCreate() {
        SkillDto skillDto = new SkillDto(1L, "crek");
        skillService.create(skillDto);
        Mockito.verify(skillRepository, Mockito.times(1))
                .save(skillMapper.toEntity(skillDto));
    }

    @Test
    void testGetUserSkills() {
        Mockito.when(skillRepository.findAllByUserId(1L)).thenReturn(Mockito.anyList());

        skillService.getUserSkills(1L);

        Mockito.verify(skillRepository, Mockito.times(1))
                .findAllByUserId(1L);
    }

    @Test
    void testGetOfferedSkills() {
        Skill skill1 = Skill.builder()
                .id(1L)
                .title("One")
                .build();
        Skill skill2 = Skill.builder()
                .id(1L)
                .title("One")
                .build();
        SkillDto skillDto1 = new SkillDto(1L, "One");
        SkillDto skillDto2 = new SkillDto(1L, "One");
        SkillCandidateDto skillCandidateDto = new SkillCandidateDto(skillDto1, 2L);
        List<SkillCandidateDto> expected = List.of(skillCandidateDto);

        List<Skill> skills = new ArrayList<>();
        skills.add(skill1);
        skills.add(skill2);

        Mockito.when(skillRepository.findSkillsOfferedToUser(1L)).thenReturn(skills);
        Mockito.when(skillMapper.toDto(skill1)).thenReturn(skillDto1);
        Mockito.when(skillMapper.toDto(skill2)).thenReturn(skillDto2);

        skillService.getOfferedSkills(1L);

        Mockito.verify(skillRepository, Mockito.times(1))
                .findSkillsOfferedToUser(1L);

        List<SkillCandidateDto> actual = skillService.getOfferedSkills(1L);

        Assertions.assertEquals(expected, actual);
    }

    @Test
    void testAcquireSkillFromOffers() {
        SkillDto skillDto = new SkillDto(1L, "One");

        Mockito.when(skillRepository.findUserSkill(1L, 1L)).thenReturn(Optional.empty());
        Mockito.when(skillOfferRepository.findAllOffersOfSkill(1L, 1L))
                .thenReturn(List.of(skillOffer1, skillOffer2, skillOffer3, skillOffer4));
        Mockito.when(skillMapper.toDto(skill)).thenReturn(skillDto);
        Mockito.when(skillRepository.findById(1L)).thenReturn(Optional.of(skill));
        assertEquals(skillDto, skillService.acquireSkillFromOffers(1L, 1L));
    }

    @Test
    void testAcquireSkillFromOffersFindUserSkill() {
        Skill skill = new Skill(1L, "One", null, null, null, null, null, null);
        SkillDto skillDto = new SkillDto(1L, "One");

        Mockito.when(skillMapper.toDto(skill)).thenReturn(skillDto);
        Mockito.when(skillRepository.findUserSkill(1L, 1L)).thenReturn(Optional.of(skill));

        assertEquals(skillDto, skillService.acquireSkillFromOffers(1L, 1L));
    }

    @Test
    void testAcquireSkillFromOffersNegative() {
        Mockito.when(skillRepository.findById(1L)).thenReturn(Optional.empty());
        Mockito.when(skillOfferRepository.findAllOffersOfSkill(1L, 1L))
                .thenReturn(List.of(skillOffer1, skillOffer2, skillOffer3, skillOffer4));

        assertThrows(DataValidationException.class, () -> skillService.acquireSkillFromOffers(1L, 1L));
    }

    @Test
    void addUserSkillGuarantee() {
        skillService.addUserSkillGuarantee(skill, List.of(skillOffer1));
        Mockito.verify(userSkillGuaranteeRepository, Mockito.times(1))
                .save(UserSkillGuarantee.builder()
                        .user(skillOffer1.getRecommendation().getReceiver())
                        .skill(skill)
                        .guarantor(skillOffer1.getRecommendation().getAuthor())
                        .build());
    }

}