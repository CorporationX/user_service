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
import school.faang.user_service.mapper.skill.SkillMapperImpl;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.validation.skill.SkillValidator;
import school.faang.user_service.validation.user.UserValidator;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
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
    private UserValidator userValidator;

    @Mock
    SkillOfferRepository skillOfferRepository;

    @Mock
    SkillValidator skillValidator;
    @Spy
    private SkillMapperImpl skillMapper;

    @Captor
    private ArgumentCaptor<Skill> captorSkill;

    UserSkillGuarantee userSkillGuarantee;
    List<Skill> skills;
    List<SkillDto> skillDtoList;
    List<SkillOffer> skillOffers;

    @BeforeEach
    public void setUp() {
        userSkillGuarantee = userSkillGuarantee(1L, userData(1L), skillData(1L, "test"), userData(2L));
        skills = List.of(
                skillData(1L, "test"),
                skillData(2L, "test"),
                skillData(3L, "test")
        );
        skillDtoList = List.of(
                new SkillDto(1L, "test"),
                new SkillDto(2L, "test"),
                new SkillDto(3L, "test")
        );
        skillOffers = List.of(
                skillOfferData(1l, skillData(1L, "test"), recommendationData(1L)),
                skillOfferData(2l, skillData(1L, "test"), recommendationData(2L)),
                skillOfferData(3l, skillData(1L, "test"), recommendationData(3L))
        );
    }

    @Test
    public void testCreateSaveSkill() {
        SkillDto skillDto = prepareData(1L, "test");

        skillService.create(skillDto);
        skillValidator.validateSkill(skillDto);

        verify(skillRepository, times(1)).save(captorSkill.capture());
        Skill skill = captorSkill.getValue();
        assertEquals(skillDto.getTitle(), skill.getTitle());
    }

    @Test
    public void testGetUserSkills() {
        when(skillRepository.findAllByUserId(1L)).thenReturn(skills);
        List<SkillDto> result = skillService.getUserSkills(1L);

        assertEquals(skillDtoList.size(), result.size());
        verify(skillRepository).findAllByUserId(1L);
    }

    @Test
    public void testGetOfferedSkills() {
        skillService.getUserSkills(1L);
        List<Skill> listSkill = List.of(
                createDataSkill(1L, "test")
        );

        when(skillRepository.findSkillsOfferedToUser(1L)).thenReturn(listSkill);
        List<SkillCandidateDto> skillCandidateDto = skillService.getOfferedSkills(1L);

        verify(skillRepository, times(1)).findSkillsOfferedToUser(1L);
        verify(skillMapper, times(listSkill.size())).toDto(any(Skill.class));
        assertEquals(skillCandidateDto, listSkillCandidateDto(1L, 1L, "test"));
        assertNotNull(skillCandidateDto);
    }

    @Test
    public void test() {
        Long skillId = 1L;
        Long userId = 1L;
        skillService.acquireSkillFromOffers(skillId, userId);

        verify(skillRepository, times(1)).assignSkillToUser(skillId, userId);
    }

    private SkillDto prepareData(long id, String title) {
        return SkillDto
                .builder()
                .id(id)
                .title(title)
                .build();
    }

    private Skill createDataSkill(Long id, String title) {
        return Skill.builder()
                .id(id)
                .title(title)
                .build();
    }

    private SkillOffer skillOfferData(Long id, Skill skill, Recommendation recommendation) {
        return SkillOffer
                .builder()
                .id(id)
                .skill(skill)
                .recommendation(recommendation)
                .build();
    }

    private List<SkillCandidateDto> listSkillCandidateDto(long offersAmount, long id, String title) {
        SkillCandidateDto skillCandidateDto = SkillCandidateDto
                .builder()
                .skillDto(prepareData(id, title))
                .offersAmount(offersAmount)
                .build();

        return List.of(skillCandidateDto);
    }

    private Recommendation recommendationData(Long id) {
        return Recommendation
                .builder()
                .id(id)
                .build();
    }

    private UserSkillGuarantee userSkillGuarantee(Long id, User user, Skill skill, User user2) {
        return UserSkillGuarantee
                .builder()
                .id(id)
                .user(user)
                .skill(skill)
                .guarantor(user2)
                .build();
    }

    private User userData(Long id) {
        return User
                .builder()
                .id(id)
                .build();
    }

    private Skill skillData(Long id, String title) {
        return Skill
                .builder()
                .id(id)
                .title(title)
                .build();
    }
}