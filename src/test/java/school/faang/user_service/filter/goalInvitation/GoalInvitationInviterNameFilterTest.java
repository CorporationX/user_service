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
class GoalInvitationInviterNameFilterTest {

    private final static String INVITER_NAME_PATTERN = "inviterName";

    @InjectMocks
    private GoalInvitationInviterNameFilter goalInvitationInviterNameFilter;

    private GoalInvitationFilterDto goalInvitationFilterDto;

    @Nested
    class PositiveTests {

        @Test
        @DisplayName("If GoalInvitationFilterDto inviterNamePattern not null and not blank than return true")
        void whenGoalInvitationFilterDtoSpecifiedInviterNamePatternISNotNullAndMoreThanZeroThenReturnTrue() {
            goalInvitationFilterDto = GoalInvitationFilterDto.builder()
                    .inviterNamePattern(INVITER_NAME_PATTERN)
                    .build();

            assertTrue(goalInvitationInviterNameFilter.isApplicable(goalInvitationFilterDto));
        }

        @Test
        @DisplayName("If GoalInvitationFilterDto inviterNamePattern not null and not blank than return sorted list")
        void whenGoalInvitationFilterDtoSpecifiedInviterNamePatternThenReturnFilteredList() {
            Stream<GoalInvitation> goalInvitations = Stream.of(
                    GoalInvitation.builder()
                            .inviter(User.builder()
                                    .username(INVITER_NAME_PATTERN)
                                    .build())
                            .build(),
                    GoalInvitation.builder()
                            .inviter(User.builder()
                                    .username("false")
                                    .build())
                            .build());

            goalInvitationFilterDto = GoalInvitationFilterDto.builder()
                    .inviterNamePattern(INVITER_NAME_PATTERN)
                    .build();

            List<GoalInvitation> goalInvitationsAfterFilter = List.of(
                    GoalInvitation.builder()
                            .inviter(User.builder()
                                    .username(INVITER_NAME_PATTERN)
                                    .build())
                            .build());

            assertEquals(goalInvitationsAfterFilter, goalInvitationInviterNameFilter.apply(goalInvitations,
                    goalInvitationFilterDto).toList());
        }
    }

    @Nested
    class NegativeTests {

        @Test
        @DisplayName("If GoalInvitationFilterDto inviterNamePattern is null than return false")
        void whenGoalInvitationFilterDtoSpecifiedInvitedIdIsNullThenReturnFalse() {
            goalInvitationFilterDto = GoalInvitationFilterDto.builder()
                    .inviterNamePattern(null)
                    .build();

            assertFalse(goalInvitationInviterNameFilter.isApplicable(goalInvitationFilterDto));
        }

        @Test
        @DisplayName("If GoalInvitationFilterDto inviterNamePattern is blank than return false")
        void whenGoalInvitationFilterDtoSpecifiedInvitedIdIsLessThanZeroThenReturnFalse() {
            goalInvitationFilterDto = GoalInvitationFilterDto.builder()
                    .inviterNamePattern("   ")
                    .build();

            assertFalse(goalInvitationInviterNameFilter.isApplicable(goalInvitationFilterDto));
        }
    }
}