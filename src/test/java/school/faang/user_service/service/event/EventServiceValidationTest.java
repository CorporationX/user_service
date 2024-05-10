package school.faang.user_service.service.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static school.faang.user_service.exception.ExceptionMessage.INAPPROPRIATE_OWNER_SKILLS_EXCEPTION;
import static school.faang.user_service.exception.ExceptionMessage.NO_SUCH_EVENT_EXCEPTION;
import static school.faang.user_service.exception.ExceptionMessage.NO_SUCH_USER_EXCEPTION;

@ExtendWith(MockitoExtension.class)
class EventServiceValidationTest {
    @Spy
    @InjectMocks
    private EventServiceValidation eventServiceValidation;
    @Mock
    private EventRepository eventRepository;
    @Mock
    private SkillRepository skillRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private SkillMapper skillMapper;
    private EventDto eventDto;


    @BeforeEach
    void setUp() {
        eventDto = new EventDto();
        eventDto.setTitle("Title");
        eventDto.setStartDate(LocalDateTime.of(2024, 6, 12, 12, 12));
        eventDto.setOwnerId(1L);
        eventDto.setDescription("Description");

        var skillADto = new SkillDto();
        skillADto.setTitle("SQL");
        var skillBDto = new SkillDto();
        skillBDto.setTitle("Java");
        eventDto.setRelatedSkills(List.of(skillADto, skillBDto));
        eventDto.setLocation("Location");
        eventDto.setMaxAttendees(10);
    }

    @Nested
    class PositiveTests {
        @DisplayName("shouldn't throw exception when owner has required skills to create/update event")
        @Test
        void shouldNotThrowExceptionWhenOwnerHasRequiredSkills() {
            eventDto.setRelatedSkills(List.of());
            Long ownerId = eventDto.getOwnerId();

            doNothing().when(eventServiceValidation).checkOwnerPresence(ownerId);
            doReturn(List.of()).when(skillRepository).findAllByUserId(ownerId);
            doReturn(List.of()).when(skillMapper).toDto(List.of());

            assertDoesNotThrow(() -> eventServiceValidation.checkOwnerSkills(eventDto));
        }

        @DisplayName("shouldn't throw exception when owner exists in system")
        @Test
        void shouldNotThrowExceptionWhenOwnerExists() {
            Long ownerId = eventDto.getOwnerId();

            doReturn(Optional.of(new User())).when(userRepository).findById(ownerId);

            assertDoesNotThrow(() -> eventServiceValidation.checkOwnerPresence(ownerId));
        }

        @DisplayName("shouldn't throw exception when owner has required skills to create/update existing event")
        @Test
        void shouldNotThrowExceptionWhenEventDtoIsValid() {
            doNothing().when(eventServiceValidation).checkEventPresence(eventDto.getId());
            doNothing().when(eventServiceValidation).checkOwnerSkills(eventDto);

            assertDoesNotThrow(() -> eventServiceValidation.eventUpdateValidation(eventDto));
        }
    }

    @Nested
    class NegativeTests {
        @Test
        void shouldThrowExceptionWhenOwnerDoesNotHaveRequiredSkills() {
            Long ownerId = eventDto.getOwnerId();

            doNothing().when(eventServiceValidation).checkOwnerPresence(ownerId);
            doReturn(List.of()).when(skillRepository).findAllByUserId(ownerId);
            doReturn(List.of()).when(skillMapper).toDto(List.of());

            DataValidationException exception = assertThrows(DataValidationException.class,
                    () -> eventServiceValidation.checkOwnerSkills(eventDto));

            assertEquals(INAPPROPRIATE_OWNER_SKILLS_EXCEPTION.getMessage(), exception.getMessage());
        }

        @DisplayName("should throw exception when owner doesn't exist in system")
        @Test
        void shouldThrowExceptionWhenOwnerDoesNotExists() {
            Long ownerId = eventDto.getOwnerId();

            doReturn(Optional.empty()).when(userRepository).findById(ownerId);

            DataValidationException exception = assertThrows(DataValidationException.class,
                    () -> eventServiceValidation.checkOwnerPresence(ownerId));

            assertEquals(NO_SUCH_USER_EXCEPTION.getMessage(), exception.getMessage());
        }

        @DisplayName("should throw exception when event to be updated doesn't exist in system")
        @Test
        void shouldThrowExceptionWhenEventDoesNotExists() {
            Long id = eventDto.getId();

            doReturn(false).when(eventRepository).existsById(id);

            DataValidationException exception = assertThrows(DataValidationException.class,
                    () -> eventServiceValidation.checkEventPresence(id));

            assertEquals(NO_SUCH_EVENT_EXCEPTION.getMessage(), exception.getMessage());
        }
    }
}