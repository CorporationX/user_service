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

    @InjectMocks
    private MentorshipService mentorshipService;

    private final static int SIZE_OF_MENTORS_IS_ONE = 1;
    private final static int SIZE_OF_MENTORS_IS_EMPTY = 0;

    private final static long USER_ID_IS_ONE = 1L;
    private final static long USER_ID_IS_TWO = 2L;
    private final static long USER_ID_IS_THREE = 3L;

    @Nested
    class PositiveTests {

        @Test
        @DisplayName("После выполнения метода, список менторов у пользователей сократился с 1 до 0")
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