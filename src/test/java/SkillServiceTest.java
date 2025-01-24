import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.controller.SkillController;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.mapper.SkillMapperImpl;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.service.skill.SkillService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SkillServiceTest {
    @InjectMocks
    private SkillService skillService;

    @InjectMocks
    private SkillController skillController;

    @Mock
    private SkillRepository skillRepository;

    @Mock
    private SkillOfferRepository skillOfferRepository;

    @Mock
    private UserSkillGuaranteeRepository userSkillGuaranteeRepository;

    @Spy
    private SkillMapperImpl skillMapper;

    @Captor
    private ArgumentCaptor<Skill> captor;

    @Test
    public void testCreateWithBlankTitle() {
        SkillDto skillDto = new SkillDto();
        skillDto.setTitle(" ");
        assertThrows(DataValidationException.class, () -> skillController.create(skillDto));
    }

    @Test
    public void testCreateWithExistingTitle() {
        SkillDto dto = prepareData(true);

        assertThrows(DataValidationException.class, () -> skillService.createSkill(dto));

        verify(skillRepository, times(1)).existsByTitle(dto.getTitle());
    }

    @Test
    public void testSavesSkillInDataBase() {
        SkillDto dto = prepareData(false);

        when(skillRepository.save(captor.capture()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        SkillDto result = skillService.createSkill(dto);

        verify(skillRepository, times(1)).save(captor.capture());

        Skill skill = captor.getValue();
        assertEquals("title", skill.getTitle());
        assertEquals(dto.getTitle(), result.getTitle());
    }

    public SkillDto prepareData(boolean existTitle) {
        SkillDto skillDto = new SkillDto();
        skillDto.setTitle("title");
        when(skillRepository.existsByTitle(skillDto.getTitle())).thenReturn(existTitle);
        return skillDto;
    }

    @Test
    public void testGetUserSkillsById() {
        long userId = 1L;
        List<Skill> allSkills = prepareListSkills();
        when(skillRepository.findAllByUserId(userId)).thenReturn(allSkills);

        SkillDto skillDto = new SkillDto(10L, "Java");
        SkillDto skillDto1 = new SkillDto(11L, "Python");
        when(skillMapper.toDto(allSkills.get(0))).thenReturn(skillDto);
        when(skillMapper.toDto(allSkills.get(1))).thenReturn(skillDto1);

        List<SkillDto> result = skillService.getUserSkills(userId);

        assertEquals(2, result.size());
        assertEquals("Java", result.get(0).getTitle());
        assertEquals("Python", result.get(1).getTitle());
        assertEquals(10L, result.get(0).getId());
        assertEquals(11L, result.get(1).getId());

        verify(skillMapper, times(2)).toDto(any(Skill.class));
        verify(skillRepository, times(1)).findAllByUserId(userId);
    }

    private List<Skill> prepareListSkills() {
        return List.of(Skill.builder()
                        .id(10L)
                        .title("Java")
                        .build(),
                Skill.builder()
                        .id(11L)
                        .title("Python")
                        .build());
    }

    @Test
    public void testGetOfferedSkills() {
        long userId = 1L;
        List<Skill> allOfferedSkills = prepareListSkills();
        when(skillRepository.findSkillsOfferedToUser(userId)).thenReturn(allOfferedSkills);

        SkillDto skillDto = new SkillDto(10L, "Java");
        SkillDto skillDto1 = new SkillDto(11L, "Python");
        when(skillMapper.toDto(allOfferedSkills.get(0))).thenReturn(skillDto);
        when(skillMapper.toDto(allOfferedSkills.get(1))).thenReturn(skillDto1);

        List<SkillCandidateDto> result = skillService.getOfferedSkills(userId);

        SkillCandidateDto javaSkill = result.stream()
                .filter(dto -> dto.getSkillDto() == skillDto)
                .findFirst()
                .orElseThrow();

        SkillCandidateDto pythonSkill = result.stream()
                .filter(dto -> dto.getSkillDto() == skillDto1)
                .findFirst()
                .orElseThrow();

        assertEquals(1, javaSkill.getOffersAmount());
        assertEquals(skillDto, javaSkill.getSkillDto());
        assertEquals(1, pythonSkill.getOffersAmount());
        assertEquals(skillDto1, pythonSkill.getSkillDto());

        verify(skillRepository, times(1)).findSkillsOfferedToUser(userId);
        verify(skillMapper, times(2)).toDto(any(Skill.class));
    }

    @Test
    public void testExistOfferedSkill() {
        Skill testSkill = new Skill();
        testSkill.setId(1L);

        List<Skill> skillsUser = new ArrayList<>();
        skillsUser.add(testSkill);

        User testUser = new User();
        testUser.setId(100L);
        testUser.setSkills(skillsUser);

        when(skillRepository.findUserSkill(testSkill.getId(), testUser.getId()))
                .thenReturn(Optional.of(testSkill));

        assertThrows(DataValidationException.class,
                () -> skillService.acquireSkillFromOffer(testSkill.getId(), testUser.getId()));

        verify(skillRepository, times(1))
                .findUserSkill(testSkill.getId(), testUser.getId());
    }

    @Test
    public void testNotEnoughOfferedToAcquire() {
        Skill proposedSkill = new Skill();
        proposedSkill.setId(1L);
        proposedSkill.setTitle("Java");

        User testUser = new User();
        testUser.setId(100L);

        when(skillRepository.findUserSkill(proposedSkill.getId(), testUser.getId()))
                .thenReturn(Optional.empty());

        SkillOffer firstSkillOffer = new SkillOffer();
        firstSkillOffer.setSkill(proposedSkill);

        SkillOffer secondSkillOffer = new SkillOffer();
        secondSkillOffer.setSkill(proposedSkill);

        List<SkillOffer> skillOffers = List.of(firstSkillOffer, secondSkillOffer);

        when(skillOfferRepository.findAllOffersOfSkill(proposedSkill.getId(), testUser.getId()))
                .thenReturn(skillOffers);


        assertThrows(DataValidationException.class,
                () -> skillService.acquireSkillFromOffer(proposedSkill.getId(), testUser.getId()));

        verify(skillRepository, times(1))
                .findUserSkill(proposedSkill.getId(), testUser.getId());
        verify(skillOfferRepository, times(1))
                .findAllOffersOfSkill(proposedSkill.getId(), testUser.getId());
    }

    @Test
    public void testAcquireOfferedSkill() {
        long userId = 10L;

        Skill proposedSkill = new Skill();
        proposedSkill.setId(1L);
        proposedSkill.setTitle("Java");

        User guarantor = new User();

        Recommendation recommendation = new Recommendation();
        recommendation.setAuthor(guarantor);

        SkillOffer offerSkill = new SkillOffer();
        offerSkill.setSkill(proposedSkill);
        offerSkill.setRecommendation(recommendation);

        List<SkillOffer> skillOffers = List.of(offerSkill, offerSkill, offerSkill);

        SkillDto dto = new SkillDto();
        dto.setId(100L);

        when(skillRepository.findUserSkill(proposedSkill.getId(), userId))
                .thenReturn(Optional.empty());
        when(skillOfferRepository.findAllOffersOfSkill(proposedSkill.getId(), userId))
                .thenReturn(skillOffers);
        when(skillMapper.toDto(any())).thenReturn(dto);
        when(skillRepository.findById(proposedSkill.getId())).thenReturn(Optional.of(proposedSkill));

        SkillDto result = skillService.acquireSkillFromOffer(proposedSkill.getId(), userId);

        verify(skillRepository, times(1))
                .assignSkillToUser(proposedSkill.getId(), userId);
        verify(userSkillGuaranteeRepository, times(3))
                .save(argThat(userSkillGuarantee -> userSkillGuarantee.getGuarantor() != null));
        verify(skillRepository, times(1)).findById(proposedSkill.getId());

        assertNotNull(result);
        assertEquals(dto.getId(), result.getId());
    }
}
