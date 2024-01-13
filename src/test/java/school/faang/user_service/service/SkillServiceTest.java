package school.faang.user_service.service;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.skill.SkillCandidateMapper;
import school.faang.user_service.mapper.skill.SkillMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;

@ExtendWith(MockitoExtension.class)
class SkillServiceTest {

    @Mock
    private SkillRepository skillRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private SkillMapper skillMapper;
    @Mock
    private SkillCandidateMapper skillCandidateMapper;

    @InjectMocks
    private SkillService skillService;

    SkillDto inputSkillDto = new SkillDto("Навык");
    long userId = 1l;

    @Test
    public void create_whenSkillFromSkillDtoIsNew_thenSaveToDb() {
        // Arrange

        Skill skill = new Skill();
        skill.setTitle(inputSkillDto.getTitle());

        Mockito.when(skillRepository.existsByTitle(inputSkillDto.getTitle())).thenReturn(false);
        Mockito.when(skillMapper.toEntity(inputSkillDto)).thenReturn(skill);
        Mockito.when(skillRepository.save(skill)).thenReturn(skill);
        Mockito.when(skillMapper.toDto(skill)).thenReturn(new SkillDto("Навык"));

        // Act
        skillService.create(inputSkillDto);

        // Assert
        Mockito.verify(skillRepository, Mockito.times(1)).existsByTitle(inputSkillDto.getTitle());
        Mockito.verify(skillMapper, Mockito.times(1)).toEntity(inputSkillDto);
        Mockito.verify(skillRepository, Mockito.times(1)).save(skill);
        Mockito.verify(skillMapper, Mockito.times(1)).toEntity(inputSkillDto);
    }

    @Test
    public void create_whenTitleOfSkillDtoIsExist_thenThrowRuntimeException() {
        // Arrange
        Mockito.when(skillRepository.existsByTitle(inputSkillDto.getTitle())).thenReturn(true);

        // Act & Assert
        Assert.assertThrows(RuntimeException.class, () -> skillService.create(inputSkillDto));
    }

    @Test
    public void getUserSkills_whenUserIdIsExist_thenGetUserSkills() {
        // Arrange
        List<Skill> skillList = List.of(Skill.builder().build(), Skill.builder().build());
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(User.builder().id(userId).build()));
        Mockito.when(skillRepository.findAllByUserId(userId)).thenReturn(skillList);

        // Act
        skillService.getUserSkills(userId);

        // Assert
        Mockito.verify(userRepository, Mockito.times(1)).findById(userId);
        Mockito.verify(skillRepository, Mockito.times(1)).findAllByUserId(userId);
        Mockito.verify(skillMapper, Mockito.times(skillList.size())).toDto(Mockito.any());
    }

    @Test
    public void getUserSkills_whenUserIdIsNotExist_thenThrowRuntimeException() {
        // Arrange
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        Assert.assertThrows(RuntimeException.class, () -> skillService.getUserSkills(userId));
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
        SkillCandidateDto skillCandidateDto = new SkillCandidateDto(new SkillDto(1L, "Skill"), 1);

        Mockito.when(skillRepository.findSkillsOfferedToUser(userId)).thenReturn(skillsOfferedToUser);
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(User.builder().id(userId).build()));
        Mockito.when(skillCandidateMapper.toDto(Mockito.any())).thenReturn(skillCandidateDto);

        // Act
        skillService.getOfferedSkills(userId);

        // Assert
        assertAll(
                () -> Mockito.verify(skillRepository, Mockito.times(1)).findSkillsOfferedToUser(userId),
                () -> Mockito.verify(userRepository, Mockito.times(1)).findById(userId),
                () -> Mockito.verify(skillCandidateMapper, Mockito.times(uniqueSKillsCount)).toDto(Mockito.any())
        );
    }

    @Test
    public void getOfferedSkills_whenUserIdIsNotExist_thenThrowRuntimeException() {
        // Arrange
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        Assert.assertThrows(RuntimeException.class, () -> skillService.getOfferedSkills(userId));
    }
}