package school.faang.user_service.filter.goalInvitation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.goal.GoalInvitationFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.GoalInvitation;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class GoalInvitationInviterIdFilterTest {

    private final static Long INVITER_ID_NEGATIVE_PATTERN = -1L;
    private final static Long INVITER_ID_PATTERN = 1L;
    private final static Long INVITER_USER_ID_ONE = 1L;
    private final static Long INVITER_USER_ID_TWO = 2L;

    @InjectMocks
    private GoalInvitationInviterIdFilter goalInvitationInviterIdFilter;

    private GoalInvitationFilterDto goalInvitationFilterDto;

    @Nested
    class PositiveTests {

        @Test
        @DisplayName("If GoalInvitationFilterDto inviterId not null and positive than return true")
        void whenGoalInvitationFilterDtoSpecifiedInviterIdISNotNullAndMoreThanZeroThenReturnTrue() {
            goalInvitationFilterDto = GoalInvitationFilterDto.builder()
                    .inviterId(INVITER_ID_PATTERN)
                    .build();

            assertTrue(goalInvitationInviterIdFilter.isApplicable(goalInvitationFilterDto));
        }

        @Test
        @DisplayName("If GoalInvitationFilterDto inviterId not null and positive than return sorted list")
        void whenGoalInvitationFilterDtoSpecifiedInviterIdThenReturnFilteredList() {
            Stream<GoalInvitation> goalInvitations = Stream.of(
                    GoalInvitation.builder()
                            .inviter(User.builder()
                                    .id(INVITER_USER_ID_ONE)
                                    .build())
                            .build(),
                    GoalInvitation.builder()
                            .inviter(User.builder()
                                    .id(INVITER_USER_ID_TWO)
                                    .build())
                            .build());

            goalInvitationFilterDto = GoalInvitationFilterDto.builder()
                    .inviterId(INVITER_ID_PATTERN)
                    .build();

            List<GoalInvitation> goalInvitationsAfterFilter = List.of(
                    GoalInvitation.builder()
                            .inviter(User.builder()
                                    .id(INVITER_USER_ID_ONE)
                                    .build())
                            .build());

            assertEquals(goalInvitationsAfterFilter, goalInvitationInviterIdFilter.apply(goalInvitations,
                    goalInvitationFilterDto).toList());
        }
    }

    @Nested
    class NegativeTests {

        @Test
        @DisplayName("If GoalInvitationFilterDto inviterId is null than return false")
        void whenGoalInvitationFilterDtoSpecifiedInviterIdIsNullThenReturnFalse() {
            goalInvitationFilterDto = GoalInvitationFilterDto.builder()
                    .inviterId(null)
                    .build();

            assertFalse(goalInvitationInviterIdFilter.isApplicable(goalInvitationFilterDto));
        }

        @Test
        @DisplayName("If GoalInvitationFilterDto inviterId is less than zero than return false")
        void whenGoalInvitationFilterDtoSpecifiedInviterIdIsLessThanZeroThenReturnFalse() {
            goalInvitationFilterDto = GoalInvitationFilterDto.builder()
                    .inviterId(INVITER_ID_NEGATIVE_PATTERN)
                    .build();

            assertFalse(goalInvitationInviterIdFilter.isApplicable(goalInvitationFilterDto));
        }
    }
}