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
class GoalInvitationInvitedNameFilterTest {

    private final static String INVENTED_NAME_PATTERN = "inventedName";

    @InjectMocks
    private GoalInvitationInvitedNameFilter goalInvitationInvitedNameFilter;

    private GoalInvitationFilterDto goalInvitationFilterDto;

    @Nested
    class PositiveTests {

        @Test
        @DisplayName("If GoalInvitationFilterDto invitedNamePattern not null and not blank than return true")
        void whenGoalInvitationFilterDtoSpecifiedInvitedNamePatternISNotNullAndMoreThanZeroThenReturnTrue() {
            goalInvitationFilterDto = GoalInvitationFilterDto.builder()
                    .invitedNamePattern(INVENTED_NAME_PATTERN)
                    .build();

            assertTrue(goalInvitationInvitedNameFilter.isApplicable(goalInvitationFilterDto));
        }

        @Test
        @DisplayName("If GoalInvitationFilterDto invitedNamePattern not null and not blank than return sorted list")
        void whenGoalInvitationFilterDtoSpecifiedInvitedNamePatternThenReturnFilteredList() {
            Stream<GoalInvitation> goalInvitations = Stream.of(
                    GoalInvitation.builder()
                            .invited(User.builder()
                                    .username(INVENTED_NAME_PATTERN)
                                    .build())
                            .build(),
                    GoalInvitation.builder()
                            .invited(User.builder()
                                    .username("false")
                                    .build())
                            .build());

            goalInvitationFilterDto = GoalInvitationFilterDto.builder()
                    .invitedNamePattern(INVENTED_NAME_PATTERN)
                    .build();

            List<GoalInvitation> goalInvitationsAfterFilter = List.of(
                    GoalInvitation.builder()
                            .invited(User.builder()
                                    .username(INVENTED_NAME_PATTERN)
                                    .build())
                            .build());

            assertEquals(goalInvitationsAfterFilter, goalInvitationInvitedNameFilter.apply(goalInvitations,
                    goalInvitationFilterDto).toList());
        }
    }

    @Nested
    class NegativeTests {

        @Test
        @DisplayName("If GoalInvitationFilterDto invitedNamePattern is null than return false")
        void whenGoalInvitationFilterDtoSpecifiedInvitedIdIsNullThenReturnFalse() {
            goalInvitationFilterDto = GoalInvitationFilterDto.builder()
                    .invitedNamePattern(null)
                    .build();

            assertFalse(goalInvitationInvitedNameFilter.isApplicable(goalInvitationFilterDto));
        }

        @Test
        @DisplayName("If GoalInvitationFilterDto invitedNamePattern is blank than return false")
        void whenGoalInvitationFilterDtoSpecifiedInvitedIdIsLessThanZeroThenReturnFalse() {
            goalInvitationFilterDto = GoalInvitationFilterDto.builder()
                    .invitedNamePattern("   ")
                    .build();

            assertFalse(goalInvitationInvitedNameFilter.isApplicable(goalInvitationFilterDto));
        }
    }
}