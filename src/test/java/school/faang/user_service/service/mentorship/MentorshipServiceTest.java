package school.faang.user_service.service.mentorship;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.User;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class MentorshipServiceTest {

    private static final int SIZE_OF_MENTORS_IS_ONE = 1;
    private static final int SIZE_OF_MENTORS_IS_EMPTY = 0;

    private static final long USER_ID_IS_ONE = 1L;
    private static final long USER_ID_IS_TWO = 2L;
    private static final long USER_ID_IS_THREE = 3L;

    @InjectMocks
    private MentorshipService mentorshipService;

    @Nested
    class PositiveTests {

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