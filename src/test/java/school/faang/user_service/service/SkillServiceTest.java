package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.skill.SkillMapperImpl;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.service.skill.SkillService;
import school.faang.user_service.service.skill.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SkillServiceTest {
    @InjectMocks
    private SkillService skillService;

    @Spy
    private SkillMapperImpl skillMapper;
    @Mock
    private SkillRepository skillRepository;

    @Mock
    private UserService userService;

    @Captor
    ArgumentCaptor<Skill> skillCaptor;

    @Test
    public void shouldThrowExceptionForExistingSkill () {
        SkillDto dto = setSkillDto(true);

        assertThrows(
                DataValidationException.class,
                () -> skillService.create(dto)
        );
    }

    @Test
    public void shouldCreateSkill () {
        SkillDto dto = setSkillDto(false);
        User firstUser = User.builder().id(1L).username("David").build();
        User secondUser = User.builder().id(2L).username("Jason").build();
        List<User> users = List.of(firstUser, secondUser);
        List<Long> userIds = users.stream().map(User::getId).toList();

        dto.setUserIds(userIds);

        when(userService.getUsersByIds(dto.getUserIds())).thenReturn(users);

        SkillDto result = skillService.create(dto);

        verify(skillRepository, times(1))
                .save(skillCaptor.capture());

        Skill skill = skillCaptor.getValue();
        List<Long> skillUserIds = skill.getUsers().stream().map(User::getId).toList();

        assertEquals(dto.getTitle(), skill.getTitle());
        assertEquals(dto.getUserIds(), skillUserIds);
        assertEquals(dto.getTitle(), result.getTitle());
    }

    @Test
    public void shouldCreateSkillWithoutUsers () {
        SkillDto dto = setSkillDto(false);
        SkillDto result = skillService.create(dto);

        verify(skillRepository, times(1))
                .save(skillCaptor.capture());

        Skill skill = skillCaptor.getValue();
        List<Long> skillUserIds = skill.getUsers().stream().map(User::getId).toList();

        assertEquals(dto.getTitle(), skill.getTitle());
        assertEquals(dto.getUserIds(), skillUserIds);
        assertEquals(dto.getTitle(), result.getTitle());

    }

    private SkillDto setSkillDto (boolean existsByTitle) {
        SkillDto dto = new SkillDto();
        dto.setTitle("java");
        when(skillRepository.existsByTitle(dto.getTitle())).thenReturn(existsByTitle);

        return dto;
    }
}
