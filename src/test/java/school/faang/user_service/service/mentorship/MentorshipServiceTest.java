package school.faang.user_service.service.mentorship;

import jakarta.persistence.EntityNotFoundException;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MentorshipServiceTest {

    @Nested
    @DisplayName("Тесты для получения списка менти")
    class MenteesServiceTests {
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

        @Test
        @DisplayName("Должен вернуть список менти для ментора")
        void shouldReturnListOfMenteesForMentor() {
            when(mentorshipRepository.findById(mentor.getId())).thenReturn(Optional.of(mentor));
            when(userMapper.toDto(mentee))
                    .thenReturn(UserDto.builder()
                            .id(mentee.getId())
                            .username("Mentee Name")
                            .email("mentee@example.com")
                            .build());

            List<UserDto> mentees = mentorshipService.getMentees(mentor.getId());

            assertEquals(1, mentees.size());
            assertEquals(mentee.getId(), mentees.get(0).getId());
            verify(mentorshipRepository).findById(mentor.getId());
            verify(userMapper).toDto(mentee);
        }


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

        @Test
        @DisplayName("Должен вернуть список менторов для менти")
        void shouldReturnListOfMentorsForMentee() {
            when(mentorshipRepository.findById(mentee.getId())).thenReturn(Optional.of(mentee));
            mentee.setMentors(new ArrayList<>(List.of(mentor)));
            when(userMapper.toDto(mentor))
                    .thenReturn(UserDto.builder()
                            .id(mentor.getId())
                            .username("Mentee Name")
                            .email("mentee@example.com")
                            .build());

            List<UserDto> mentors = mentorshipService.getMentors(mentee.getId());

            assertEquals(1, mentors.size());
            assertEquals(mentor.getId(), mentors.get(0).getId());
            verify(mentorshipRepository).findById(mentee.getId());
            verify(userMapper).toDto(mentor);
        }


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

    @Nested
    @ExtendWith(MockitoExtension.class)
    class RemoveUserMenteesTest {
        private static final int SIZE_OF_MENTORS_IS_ONE = 1;
        private static final int SIZE_OF_MENTORS_IS_EMPTY = 0;

        private static final long USER_ID_IS_ONE = 1L;
        private static final long USER_ID_IS_TWO = 2L;
        private static final long USER_ID_IS_THREE = 3L;

        @InjectMocks
        private MentorshipService mentorshipService;

        @Test
        @DisplayName("Mentors size decrease from 1 to 0")
        void whenUsersMenteesContainsUserThenSizeOfUserMenteesDecreesOnOne() {
            User user = User.builder()
                    .id(USER_ID_IS_THREE)
                    .build();

            List<User> mentors = new ArrayList<>();
            mentors.add(user);

            User user1 = User.builder()
                    .id(USER_ID_IS_ONE)
                    .mentors(mentors)
                    .build();
            User user2 = User.builder()
                    .id(USER_ID_IS_TWO)
                    .mentors(mentors)
                    .build();

            user.setMentees(List.of(user1, user2));

            assertEquals(SIZE_OF_MENTORS_IS_ONE, user1.getMentors().size());
            assertEquals(SIZE_OF_MENTORS_IS_ONE, user2.getMentors().size());

            mentorshipService.removeUserFromListHisMentees(user);

            assertEquals(SIZE_OF_MENTORS_IS_EMPTY, user1.getMentors().size());
            assertEquals(SIZE_OF_MENTORS_IS_EMPTY, user2.getMentors().size());
        }
    }
}