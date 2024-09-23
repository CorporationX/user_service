package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.user.UserMapper;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
                when(mentorshipRepository.findById(1L)).thenReturn(Optional.of(mentor));
                when(userMapper.toDto(mentee)).thenReturn(new UserDto(2L, "Mentee Name", "mentee@example.com"));

                List<UserDto> mentees = mentorshipService.getMentees(1L);

                assertEquals(1, mentees.size());
                assertEquals(2L, mentees.get(0).getId());
                verify(mentorshipRepository).findById(1L);
                verify(userMapper).toDto(mentee);
            }
        }

        @Nested
        @DisplayName("Негативные тесты")
        class NegativeTests {

            @Test
            @DisplayName("Должен вернуть пустой список, если ментор не найден")
            void shouldReturnEmptyListWhenMentorNotFound() {
                when(mentorshipRepository.findById(1L)).thenReturn(Optional.empty());

                List<UserDto> mentees = mentorshipService.getMentees(1L);

                assertTrue(mentees.isEmpty());
                verify(mentorshipRepository).findById(1L);
                verify(mentorshipRepository, never()).save(any());
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
                when(mentorshipRepository.findById(1L)).thenReturn(Optional.of(mentor));
                when(mentorshipRepository.findById(2L)).thenReturn(Optional.of(mentee));
                when(userMapper.toDto(mentee)).thenReturn(new UserDto(2L, "Mentee Name", "mentee@example.com"));

                assertTrue(mentor.getMentees().contains(mentee));

                Optional<UserDto> deletedMentee = mentorshipService.deleteMentee(1L, 2L);

                assertEquals(2L, deletedMentee.get().getId());
                verify(mentorshipRepository).save(mentor);
            }
        }

        @Nested
        @DisplayName("Негативные тесты")
        class NegativeTests {

            @Test
            @DisplayName("Должен вернуть пустой Optional, если менти не найден")
            void shouldReturnEmptyOptionalWhenMenteeNotFound() {
                when(mentorshipRepository.findById(1L)).thenReturn(Optional.of(mentor));
                when(mentorshipRepository.findById(2L)).thenReturn(Optional.empty());

                Optional<UserDto> result = mentorshipService.deleteMentee(1L, 2L);

                assertTrue(result.isEmpty());
                verify(mentorshipRepository).findById(1L);
                verify(mentorshipRepository).findById(2L);
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
                when(mentorshipRepository.findById(2L)).thenReturn(Optional.of(mentee));
                mentee.setMentors(new ArrayList<>(List.of(mentor)));
                when(userMapper.toDto(mentor)).thenReturn(new UserDto(1L, "Mentor Name", "mentor@example.com"));

                List<UserDto> mentors = mentorshipService.getMentors(2L);

                assertEquals(1, mentors.size());
                assertEquals(1L, mentors.get(0).getId());
                verify(mentorshipRepository).findById(2L);
                verify(userMapper).toDto(mentor);
            }
        }

        @Nested
        @DisplayName("Негативные тесты")
        class NegativeTests {

            @Test
            @DisplayName("Должен вернуть пустой список, если менти не найден")
            void shouldReturnEmptyListWhenMenteeNotFound() {
                when(mentorshipRepository.findById(2L)).thenReturn(Optional.empty());

                List<UserDto> mentors = mentorshipService.getMentors(2L);

                assertTrue(mentors.isEmpty());
                verify(mentorshipRepository).findById(2L);
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
                when(mentorshipRepository.findById(2L)).thenReturn(Optional.of(mentee));
                when(mentorshipRepository.findById(1L)).thenReturn(Optional.of(mentor));
                when(userMapper.toDto(mentor)).thenReturn(new UserDto(1L, "Mentor Name", "mentor@example.com"));

                assertTrue(mentee.getMentors().contains(mentor));

                Optional<UserDto> deletedMentor = mentorshipService.deleteMentor(2L, 1L);

                assertTrue(deletedMentor.isPresent());
                assertEquals(1L, deletedMentor.get().getId());
                verify(mentorshipRepository).save(mentee);
            }
        }

        @Nested
        @DisplayName("Негативные тесты")
        class NegativeTests {

            @Test
            @DisplayName("Должен вернуть пустой Optional, если ментор не найден")
            void shouldReturnEmptyOptionalWhenMentorNotFound() {
                when(mentorshipRepository.findById(2L)).thenReturn(Optional.of(mentee));
                when(mentorshipRepository.findById(1L)).thenReturn(Optional.empty());

                Optional<UserDto> result = mentorshipService.deleteMentor(2L, 1L);

                assertTrue(result.isEmpty());
                verify(mentorshipRepository).findById(2L);
                verify(mentorshipRepository).findById(1L);
                verify(mentorshipRepository, never()).save(mentee);
            }
        }
    }
}
