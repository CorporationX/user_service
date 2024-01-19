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
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.skill.SkillCandidateMapper;
import school.faang.user_service.mapper.skill.SkillMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.validate.skill.SkillValidation;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SkillServiceTest {

    @Mock
    private SkillRepository skillRepository;
    @Mock
    private SkillMapper skillMapper;
    @Mock
    private SkillCandidateMapper skillCandidateMapper;
    @Mock
    private UserSkillGuaranteeRepository userSkillGuaranteeRepository;
    @Mock
    private SkillOfferRepository skillOfferRepository;
    @Mock
    private SkillValidation skillValidation;
    @Mock
    private UserService userService;

    @InjectMocks
    private SkillService skillService;

    SkillDto inputSkillDto = new SkillDto(null, "Навык");
    SkillDto skillDto = new SkillDto(1L, "Навык");
    long userId = 1L;
    long skillId = 1L;

    @Test
    public void create_whenSkillFromSkillDtoIsNew_thenSaveToDb() {
        // Arrange

        Skill skill = new Skill();
        skill.setTitle(inputSkillDto.getTitle());

        when(skillRepository.existsByTitle(inputSkillDto.getTitle())).thenReturn(false);
        when(skillMapper.toEntity(inputSkillDto)).thenReturn(skill);
        when(skillRepository.save(skill)).thenReturn(skill);
        when(skillMapper.toDto(skill)).thenReturn(skillDto);

        // Act
        SkillDto result = skillService.create(inputSkillDto);

        // Assert
        assertAll(
                () -> verify(skillRepository, times(1)).existsByTitle(inputSkillDto.getTitle()),
                () -> verify(skillMapper, times(1)).toEntity(inputSkillDto),
                () -> verify(skillRepository, times(1)).save(skill),
                () -> verify(skillMapper, times(1)).toDto(skill),
                () -> assertEquals(inputSkillDto.getTitle(), result.getTitle())
        );
    }

    @Test
    public void create_whenTitleOfSkillDtoIsExist_thenThrowDataValidationException() {
        // Arrange
        when(skillRepository.existsByTitle(inputSkillDto.getTitle())).thenReturn(true);

        // Act & Assert
        assertThrows(DataValidationException.class, () -> skillService.create(inputSkillDto));
    }

    @Test
    public void getUserSkills_whenUserIdIsExist_thenGetUserSkills() {
        // Arrange
        List<Skill> skillList = List.of(Skill.builder().build(), Skill.builder().build());
        when(skillRepository.findAllByUserId(userId)).thenReturn(skillList);

        // Act
        skillService.getUserSkills(userId);

        // Assert
        assertAll(
                () -> verify(skillRepository, times(1)).findAllByUserId(userId),
                () -> verify(skillMapper, times(skillList.size())).toDto(any())
        );
    }

    @Test
    public void getOfferedSkills_whenUserIdIsExist_thenReturnListSkillCandidateDto() {
        // Arrange
        List<Skill> skillsOfferedToUser = List.of(
                Skill.builder().id(1).title("java").build(),
                Skill.builder().id(2).title("kotlin").build(),
                Skill.builder().id(1).title("java").build(),
                Skill.builder().id(1).title("java").build()
        );
        int uniqueSKillsCount = (int) skillsOfferedToUser.stream().distinct().count();
        SkillCandidateDto skillCandidateDto = new SkillCandidateDto(skillDto, 1);

        when(skillRepository.findSkillsOfferedToUser(userId)).thenReturn(skillsOfferedToUser);
        when(skillCandidateMapper.toDto(any())).thenReturn(skillCandidateDto);

        // Act
        skillService.getOfferedSkills(userId);

        // Assert
        assertAll(
                () -> verify(skillRepository, times(1)).findSkillsOfferedToUser(userId),
                () -> verify(skillCandidateMapper, times(uniqueSKillsCount)).toDto(any())
        );
    }

    @Test
    public void acquireSkillFromOffers_whenUserIdAndSkillIdIsExist_thenReturnSkillDto() {
        // Arrange
        Skill skill = new Skill();
        User user = User.builder().id(userId).build();
        List<SkillOffer> skillOffers = List.of(
                new SkillOffer(1, skill, Recommendation.builder().author(User.builder().id(3).build()).build()),
                new SkillOffer(2, skill, Recommendation.builder().author(User.builder().id(4).build()).build()),
                new SkillOffer(3, skill, Recommendation.builder().author(User.builder().id(5).build()).build()));
        when(skillRepository.findUserSkill(skillId, userId)).thenReturn(Optional.empty());
        when(skillOfferRepository.findAllOffersOfSkill(skillId, userId)).thenReturn(skillOffers);
        when(userService.getUserById(userId)).thenReturn(user);
        when(skillRepository.findById(skillId)).thenReturn(Optional.of(skill));

        // Act
        skillService.acquireSkillFromOffers(skillId, userId);

        // Assert
        assertAll(
                () -> verify(skillMapper, times(1)).toDto(any()),
                () -> verify(userService, times(1)).getUserById(userId),
                () -> verify(skillOfferRepository, times(1)).findAllOffersOfSkill(skillId, userId),
                () -> verify(skillRepository, times(1)).assignSkillToUser(skillId, userId),
                () -> verify(userSkillGuaranteeRepository, times(skillOffers.size())).save(any())
        );
    }
}