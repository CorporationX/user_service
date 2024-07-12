package school.faang.user_service.service.skill;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.skill.SkillMapperImpl;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SkillServiceTest {

    @InjectMocks
    private SkillService skillService;

    @Mock
    private SkillRepository skillRepository;

    @Mock
    private SkillOfferRepository skillOfferRepository;

    @Mock
    private UserSkillGuaranteeRepository skillGuaranteeRepository;

    @Spy
    private SkillMapperImpl skillMapper;

    @Captor
    private ArgumentCaptor<List<UserSkillGuarantee>> captor;

    private Skill skill;
    private SkillDto skillDto;
    private long userId = 1;
    private long skillId = 1;

    @BeforeEach
    void setUp() {
        skill = new Skill();
        skillDto = new SkillDto();
    }

    @Test
    void testCreateWhenExistsByTitle() {
        skillDto.setTitle("Title");

        when(skillRepository.existsByTitle(skillDto.getTitle())).thenReturn(true);

        assertThrows(DataValidationException.class, () -> skillService.create(skillDto));
    }

    @Test
    void testCreateSaveToDb() {
        skillDto.setId(userId);
        skillDto.setTitle("Title");
        Skill skill = skillMapper.toEntity(skillDto);

        when(skillRepository.existsByTitle(skillDto.getTitle())).thenReturn(false);
        when(skillRepository.save(skill)).thenReturn(skill);

        skillService.create(skillDto);

        verify(skillRepository, times(1)).existsByTitle(skillDto.getTitle());
        verify(skillRepository, times(1)).save(skill);
    }

    @Test
    void testGetUserSkills() {
        List<SkillDto> skillDtos = List.of(skillMapper.toDto(skill));

        when(skillRepository.findAllByUserId(userId)).thenReturn(List.of(skill));
        List<SkillDto> returnedSkillDtos = skillService.getUserSkills(userId);

        verify(skillRepository, times(1)).findAllByUserId(userId);
        assertEquals(skillDtos, returnedSkillDtos);
    }

    @Test
    void testGetOfferedSkills() {
        List<SkillCandidateDto> skillCandidateDtos = List.of(skillMapper.toSkillCandidateDto(skill));

        when(skillRepository.findSkillsOfferedToUser(userId)).thenReturn(List.of(skill));
        List<SkillCandidateDto> returnedSkillCandidateDtos = skillService.getOfferedSkills(userId);

        verify(skillRepository, times(1)).findSkillsOfferedToUser(userId);
        assertEquals(skillCandidateDtos, returnedSkillCandidateDtos);
    }

    @Test
    void testAcquireSkillFromOrderWhenSkillExists() {
        skillDto.setId(skillId);
        skillDto.setTitle("Title");
        skill.setId(1L);
        skill.setTitle("Title");

        when(skillRepository.findUserSkill(skillId, userId)).thenReturn(Optional.of(skill));

        SkillDto returnedSkillDto = skillService.acquireSkillFromOffer(skillId, userId);

        verify(skillRepository, times(1)).findUserSkill(skillId, userId);
        assertEquals(skillDto, returnedSkillDto);
    }

    @Test
    void testAcqiureSkillFromOrderNotEnoughSkills() {
        List<SkillOffer> offers = List.of(new SkillOffer());

        acquireSkillMockMethods(offers);

        assertThrows(DataValidationException.class, () -> skillService.acquireSkillFromOffer(skillId, userId));
    }

    @Test
    void testAcquireSkillFromOrderAccess() {
        Recommendation recommendation = new Recommendation();
        recommendation.setAuthor(new User());
        recommendation.setReceiver(new User());

        SkillOffer skillOfferFirst = new SkillOffer();
        SkillOffer skillOfferSecond = new SkillOffer();
        SkillOffer skillOfferThird = new SkillOffer();

        skillOfferFirst.setRecommendation(recommendation);
        skillOfferSecond.setRecommendation(recommendation);
        skillOfferThird.setRecommendation(recommendation);

        List<SkillOffer> offers = List.of(skillOfferFirst, skillOfferSecond, skillOfferThird);

        acquireSkillMockMethods(offers);
        doNothing().when(skillRepository).assignSkillToUser(skillId, userId);
        when(skillGuaranteeRepository.saveAll(captor.capture())).thenReturn(null);


        skillService.acquireSkillFromOffer(skillId, userId);


        verify(skillRepository, times(1)).findUserSkill(skillId, userId);
        verify(skillRepository, times(1)).findById(skillId);
        verify(skillOfferRepository, times(1)).findAllOffersOfSkill(skillId, userId);
        verify(skillRepository, times(1)).assignSkillToUser(skillId, userId);
        verify(skillGuaranteeRepository, times(1)).saveAll(captor.capture());
    }

    private void acquireSkillMockMethods(List<SkillOffer> offers) {
        when(skillRepository.findUserSkill(skillId, userId)).thenReturn(Optional.empty());
        when(skillRepository.findById(skillId)).thenReturn(Optional.of(skill));
        when(skillOfferRepository.findAllOffersOfSkill(skillId, userId)).thenReturn(offers);
    }
}