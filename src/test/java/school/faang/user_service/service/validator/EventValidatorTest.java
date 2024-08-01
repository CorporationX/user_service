package school.faang.user_service.service.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.exceptions.DataValidationException;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.validator.EventValidator;

import java.util.ArrayList;
import java.util.Optional;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(MockitoExtension.class)
public class EventValidatorTest {
    @Mock
    private SkillMapper skillMapper;
    @Mock
    private SkillRepository skillRepository;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private EventValidator eventValidator;
    private EventDto eventDto = new EventDto();
    private User owner = new User();
    private Skill firstSkill = new Skill();
    private Skill secondSkill = new Skill();
    private SkillDto firstSkillDto = new SkillDto();
    private SkillDto secondSkillDto = new SkillDto();
    private List<SkillDto> skillDtoList = new ArrayList<>();
    private List<Skill> skillList = new ArrayList<>();


    @BeforeEach
    public void setUp() {
        eventDto.setOwnerId(1L);
        eventDto.setRelatedSkills(skillDtoList);

        owner.setId(1L);
        owner.setSkills(List.of(firstSkill));

        firstSkill.setId(1L);
        firstSkill.setUsers(List.of(owner));

        secondSkill.setId(2L);
        secondSkill.setUsers(List.of(owner));

        firstSkillDto.setId(1L);
        firstSkillDto.setUserIds(List.of(owner.getId()));

        secondSkillDto.setId(2L);
        secondSkillDto.setUserIds(List.of(owner.getId()));

        skillDtoList = List.of(firstSkillDto, secondSkillDto);
        skillList = List.of(firstSkill, secondSkill);
    }

    @Test
    public void testOwnerValidation_UserFound() {
        setUp();
        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
        User result = eventValidator.ownerValidation(eventDto);
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    public void testOwnerValidation_UserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        DataValidationException exception = assertThrows(DataValidationException.class, () -> {
            eventValidator.ownerValidation(eventDto);
        });
        assertEquals("Ошибка: пользователь не найден", exception.getMessage());
    }

    @Test
    public void testSkillValidation_allSkillValidated() {
        when(skillMapper.toEntityList(skillDtoList)).thenReturn(skillList);
        when(skillRepository.findById(1L)).thenReturn(Optional.of(firstSkill));
        when(skillRepository.findById(2L)).thenReturn(Optional.of(secondSkill));
        EventDto eventDto = new EventDto();
        eventDto.setRelatedSkills(skillDtoList);
        List result = eventValidator.skillValidation(eventDto);
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(skillRepository, times(2)).findById(anyLong());
    }

    @Test
    public void testSkillValidation_someSkillInvalidated() {
        when(skillMapper.toEntityList(eventDto.getRelatedSkills())).thenReturn(skillList);
        when(skillRepository.findById(1L)).thenReturn(Optional.of(firstSkill));
        when(skillRepository.findById(2L)).thenReturn(Optional.empty());
        DataValidationException exception = assertThrows(DataValidationException.class, () -> {
            eventValidator.skillValidation(eventDto);
        });
        assertEquals("Ошибка: навык с ID " + secondSkill.getId() + " не найден", exception.getMessage());
    }

    @Test
    public void testInputDataValidation_AllSkillsValidated() {
        List result = eventValidator.skillValidation(eventDto);
        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(skillMapper.toEntityList(eventDto.getRelatedSkills())).thenReturn(skillList);
        when(skillRepository.findById(1L)).thenReturn(Optional.of(firstSkill));
        when(skillRepository.findById(2L)).thenReturn(Optional.of(secondSkill));
        assertDoesNotThrow(() -> eventValidator.inputDataValidation(eventDto));
    }

    @Test
    public void testInputDataValidation_SomeSkillsNotValidated() {
        secondSkill.setUsers(List.of(new User()));
        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(skillMapper.toEntityList(eventDto.getRelatedSkills())).thenReturn(skillList);
        when(skillRepository.findById(1L)).thenReturn(Optional.of(firstSkill));
        when(skillRepository.findById(2L)).thenReturn(Optional.of(secondSkill));
        DataValidationException exception = assertThrows(DataValidationException.class, () -> {
            eventValidator.inputDataValidation(eventDto);
        });
        assertEquals("Ошибка: пользователь не обладает всеми необходимыми навыками", exception.getMessage());
    }
}
