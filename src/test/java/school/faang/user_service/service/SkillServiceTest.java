package school.faang.user_service.service;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
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
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SkillServiceTest {
    @InjectMocks
    private SkillService skillService;
    @Mock
    private SkillRepository skillRepository;
    @Spy
    private SkillMapper skillMapper = Mappers.getMapper(SkillMapper.class);
    @Captor
    private ArgumentCaptor<Skill> captorSkill;
    private SkillDto skillDto;
    private Skill skill;
    @Mock
    private SkillOfferRepository skillOfferRepository;
    @Mock
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        skill = Skill.builder()
                .id(0)
                .title("test")
                .build();
        skillDto = SkillDto.builder()
                .id(0)
                .title("test")
                .build();
    }

    @Test
    void testCreateDataValidationExceptionExistTitle() {
        when(skillRepository.existsByTitle(skillDto.getTitle())).thenReturn(true);
        assertThrows(DataValidationException.class, () -> skillService.create(skillDto));
    }

    @Test
    void testCreateSuccessful() {
        when(skillRepository.existsByTitle(skillDto.getTitle()))
                .thenReturn(false);
        Skill entity = skillMapper.toEntity(skillDto);
        when(skillRepository.save(skillMapper.toEntity(skillDto)))
                .thenReturn(entity);

        SkillDto resultDto = skillService.create(skillDto);
        verify(skillRepository).save(captorSkill.capture());
        Skill skillCaptor = captorSkill.getValue();
        assertEquals(skill, skillCaptor);
        assertEquals(skillDto, resultDto);
    }

    @Test
    void testGetUserSkillsSuccessful() {
        when(skillRepository.findAllByUserId(0)).thenReturn(Collections.singletonList(skill));
        List<SkillDto> skillDtoList = skillService.getUserSkills(0L);
        assertTrue(skillDtoList.contains(skillDto));
    }

    @Test
    void testGetOfferedSkills() {
        Skill skill1 = getSkill(1L, "test1");
        Skill skill2 = getSkill(1L, "test1");
        Skill skill3 = getSkill(1L, "test1");
        Skill skill4 = getSkill(2L, "test2");
        List<Skill> skills = List.of(skill1, skill2, skill3, skill4);

        SkillCandidateDto candidateDto = new SkillCandidateDto(
                skillMapper.toDto(skill1),
                3L);
        List<SkillCandidateDto> listCandidateDto = new ArrayList<>();
        listCandidateDto.add(candidateDto);

        when(skillRepository.findSkillsOfferedToUser(1L)).thenReturn(skills);
        List<SkillCandidateDto> skillCandidateDto = skillService.getOfferedSkills(1L);
        assertEquals(listCandidateDto, skillCandidateDto);
    }

    @Test
    void testAcquireSkillFromOffersStateException() {
        when(skillRepository.findUserSkill(0, 0))
                .thenReturn(Optional.of(new Skill()));
        assertThrows(IllegalStateException.class,
                () -> skillService.acquireSkillFromOffers(0, 0));
    }

    @Test
    void testAcquireSkillFromOffersArgumentExcept() {
        when(skillRepository.findUserSkill(0, 0))
                .thenReturn(Optional.empty());

        List<SkillOffer> offers = List.of(SkillOffer.builder().build());
        when(skillOfferRepository.findAllOffersOfSkill(0, 0))
                .thenReturn(offers);

        assertThrows(IllegalArgumentException.class,
                () -> skillService.acquireSkillFromOffers(0, 0));
    }

    @Test
    void testAcquireSkillFromOffersSuccessful() {
        when(skillRepository.findUserSkill(0, 0))
                .thenReturn(Optional.empty());
        SkillOffer offer1 = getSkillOffer(1, skill, 4);
        SkillOffer offer2 = getSkillOffer(2, skill, 5);
        SkillOffer offer3 = getSkillOffer(3, skill, 6);
        List<SkillOffer> offers = List.of(offer1, offer2, offer3);
        when(skillOfferRepository.findAllOffersOfSkill(0, 0))
                .thenReturn(offers);
        when(userRepository.findUser(0)).thenReturn(new User());

        SkillDto resultSkillDto = skillService.acquireSkillFromOffers(0, 0);
        verify(skillMapper).toDto(Mockito.any(Skill.class));
        assertEquals(skillDto, resultSkillDto);
    }

    @NotNull
    private SkillOffer getSkillOffer(long offerId, Skill skill, long recommendationId) {
        return new SkillOffer(offerId, skill, Recommendation.builder()
                .id(recommendationId)
                .build());
    }

    private static Skill getSkill(Long num, String test) {
        return Skill.builder()
                .id(num)
                .title(test)
                .build();
    }
}