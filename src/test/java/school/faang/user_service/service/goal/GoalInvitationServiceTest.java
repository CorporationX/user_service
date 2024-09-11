package school.faang.user_service.service.goal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.repository.goal.GoalInvitationRepository;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class GoalInvitationServiceTest {

    @InjectMocks
    private GoalInvitationService goalInvitationService;
    @Mock
    private GoalInvitationRepository goalInvitationRepository;

    private final static int TWO_TIMES_USES_REPOSITORY = 2;

    private final static long USER_ID_IS_ONE = 1L;
    private final static long USER_ID_IS_TWO = 2L;

    @Nested
    class PositiveTests {

        private User user;
        private List<GoalInvitation> goalInvitations;

        @BeforeEach
        void init() {
            user = User.builder()
                    .id(USER_ID_IS_ONE)
                    .build();
            User userWithIdTwo = User.builder()
                    .id(USER_ID_IS_TWO)
                    .build();

            goalInvitations = List.of(
                    GoalInvitation.builder()
                            .invited(user)
                            .inviter(user)
                            .build(),
                    GoalInvitation.builder()
                            .invited(userWithIdTwo)
                            .inviter(userWithIdTwo)
                            .build());
        }

        @Test
        @DisplayName("Если передали лист из 2х приглашений к цели, то метод deleteById должен вызваться 2 раза")
        void whenGoalInvitationsSizeIsTwoThenTwoTimesUsesRepository() {
            goalInvitationService.deleteGoalInvitations(goalInvitations);

            verify(goalInvitationRepository, times(TWO_TIMES_USES_REPOSITORY))
                    .deleteById(anyLong());
        }

        @Test
        @DisplayName("Если в фильтре подходит одно из значений входного листа то deleteById вызывается один раз")
        void whenUserExistsThenSuccess() {
            goalInvitationService.deleteGoalInvitationForUser(goalInvitations, user);

            verify(goalInvitationRepository)
                    .deleteById(anyLong());
        }
    }
}