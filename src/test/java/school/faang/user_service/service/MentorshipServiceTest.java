package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.extension.ExtendWith;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MentorshipServiceTest {

    @Mock
    private MentorshipRepository mentorshipRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private MentorshipService mentorshipService;

    private User mentor;
    private User mentee;

    @BeforeEach
    public void setup() {
        mentor = new User();
        mentor.setId(1L);
        mentee = new User();
        mentee.setId(2L);

        mentor.setMentees(new ArrayList<>(List.of(mentee)));
        mentee.setMentors(new ArrayList<>(List.of(mentor)));
    }

    @Nested
    @DisplayName("Тесты для получения списка менти")
    class GetMenteesTests {

        @Nested
        @DisplayName("Позитивные тесты")
        class PositiveTests {

            @Test
            @DisplayName("Должен вернуть список менти для ментора")
            void shouldReturnListOfMenteesForMentor() {
                when(mentorshipRepository.findById(mentor.getId())).thenReturn(Optional.of(mentor));
                when(userMapper.toDto(mentee))
                        .thenReturn(new UserDto(mentee.getId(), "Mentee Name", "mentee@example.com"));

                List<UserDto> mentees = mentorshipService.getMentees(mentor.getId());

                assertEquals(1, mentees.size());
                assertEquals(mentee.getId(), mentees.get(0).getId());
                verify(mentorshipRepository).findById(mentor.getId());
                verify(userMapper).toDto(mentee);
            }
        }

        @Nested
        @DisplayName("Негативные тесты")
        class NegativeTests {

            @Test
            @DisplayName("Должен выбросить исключение, если ментор не найден")
            void shouldThrowExceptionWhenMentorNotFound() {
                when(mentorshipRepository.findById(mentor.getId())).thenReturn(Optional.empty());

                EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
                    mentorshipService.getMentees(mentor.getId());
                });

                assertEquals("Ментор с ID 1 не найден", exception.getMessage());
                verify(mentorshipRepository).findById(mentor.getId());
            }
        }
    }

    @Nested
    @DisplayName("Тесты для удаления менти")
    class DeleteMenteeTests {

        @Nested
        @DisplayName("Позитивные тесты")
        class PositiveTests {

            @Test
            @DisplayName("Должен удалить менти для ментора")
            void shouldDeleteMenteeForMentor() {
                when(mentorshipRepository.findById(mentor.getId())).thenReturn(Optional.of(mentor));
                when(mentorshipRepository.findById(mentee.getId())).thenReturn(Optional.of(mentee));

                assertTrue(mentor.getMentees().contains(mentee));

                mentorshipService.deleteMentee(mentor.getId(), mentee.getId());

                verify(mentorshipRepository).save(mentor);
                assertFalse(mentor.getMentees().contains(mentee));
            }
        }

        @Nested
        @DisplayName("Негативные тесты")
        class NegativeTests {

            @Test
            @DisplayName("Должен выбросить исключение, если менти не найден")
            void shouldThrowExceptionWhenMenteeNotFound() {
                when(mentorshipRepository.findById(mentor.getId())).thenReturn(Optional.of(mentor));
                when(mentorshipRepository.findById(mentee.getId())).thenReturn(Optional.empty());

                EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
                    mentorshipService.deleteMentee(mentor.getId(), mentee.getId());
                });

                assertEquals("Пользователь с ID 2 не найден", exception.getMessage());
                verify(mentorshipRepository).findById(mentor.getId());
                verify(mentorshipRepository).findById(mentee.getId());
                verify(mentorshipRepository, never()).save(mentor);
            }
        }
    }

    @Nested
    @DisplayName("Тесты для получения списка менторов")
    class GetMentorsTests {

        @Nested
        @DisplayName("Позитивные тесты")
        class PositiveTests {
            @Test
            @DisplayName("Должен вернуть список менторов для менти")
            void shouldReturnListOfMentorsForMentee() {
                when(mentorshipRepository.findById(mentee.getId())).thenReturn(Optional.of(mentee));
                mentee.setMentors(new ArrayList<>(List.of(mentor)));
                when(userMapper.toDto(mentor))
                        .thenReturn(new UserDto(mentor.getId(), "Mentor Name", "mentor@example.com"));

                List<UserDto> mentors = mentorshipService.getMentors(mentee.getId());

                assertEquals(1, mentors.size());
                assertEquals(mentor.getId(), mentors.get(0).getId());
                verify(mentorshipRepository).findById(mentee.getId());
                verify(userMapper).toDto(mentor);
            }
        }

        @Nested
        @DisplayName("Негативные тесты")
        class NegativeTests {

            @Test
            @DisplayName("Должен выбросить исключение, если менти не найден")
            void shouldThrowExceptionWhenMenteeNotFound() {
                when(mentorshipRepository.findById(mentee.getId())).thenReturn(Optional.empty());

                EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
                    mentorshipService.getMentors(mentee.getId());
                });

                assertEquals("Менти с ID 2 не найден", exception.getMessage());
                verify(mentorshipRepository).findById(mentee.getId());
                verify(userMapper, never()).toDto(any());
            }
        }
    }

    @Nested
    @DisplayName("Тесты для удаления ментора")
    class DeleteMentorTests {

        @Nested
        @DisplayName("Позитивные тесты")
        class PositiveTests {

            @Test
            @DisplayName("Должен удалить ментора для менти")
            void shouldDeleteMentorForMentee() {
                when(mentorshipRepository.findById(mentee.getId())).thenReturn(Optional.of(mentee));
                when(mentorshipRepository.findById(mentor.getId())).thenReturn(Optional.of(mentor));

                assertTrue(mentee.getMentors().contains(mentor));

                mentorshipService.deleteMentor(mentee.getId(), mentor.getId());

                verify(mentorshipRepository).save(mentee);
                assertFalse(mentee.getMentors().contains(mentor));
            }
        }

        @Nested
        @DisplayName("Негативные тесты")
        class NegativeTests {

            @Test
            @DisplayName("Должен выбросить исключение, если ментор не найден")
            void shouldThrowExceptionWhenMentorNotFound() {
                when(mentorshipRepository.findById(mentee.getId())).thenReturn(Optional.of(mentee));
                when(mentorshipRepository.findById(mentor.getId())).thenReturn(Optional.empty());

                EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
                    mentorshipService.deleteMentor(mentee.getId(), mentor.getId());
                });

                assertEquals("Пользователь с ID 1 не найден", exception.getMessage());
                verify(mentorshipRepository).findById(mentee.getId());
                verify(mentorshipRepository).findById(mentor.getId());
                verify(mentorshipRepository, never()).save(mentee);
            }
        }
    }
}
