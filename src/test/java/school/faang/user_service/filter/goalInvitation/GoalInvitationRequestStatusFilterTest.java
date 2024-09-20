package school.faang.user_service.filter.goalInvitation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.goal.GoalInvitationFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.goal.GoalInvitation;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class GoalInvitationRequestStatusFilterTest {

    private final static RequestStatus REQUEST_PATTERN = RequestStatus.ACCEPTED;

    @InjectMocks
    private GoalInvitationRequestStatusFilter goalInvitationRequestStatusFilter;

    private GoalInvitationFilterDto goalInvitationFilterDto;

    @Nested
    class PositiveTests {

        @Test
        @DisplayName("If GoalInvitationFilterDto status not null and not blank than return true")
        void whenGoalInvitationFilterDtoSpecifiedStatusIsNotNullThenReturnTrue() {
            goalInvitationFilterDto = GoalInvitationFilterDto.builder()
                    .status(REQUEST_PATTERN)
                    .build();

            assertTrue(goalInvitationRequestStatusFilter.isApplicable(goalInvitationFilterDto));
        }

        @Test
        @DisplayName("If GoalInvitationFilterDto status not null and not blank than return sorted list")
        void whenGoalInvitationFilterDtoSpecifiedStatusThenReturnFilteredList() {
            Stream<GoalInvitation> goalInvitations = Stream.of(
                    GoalInvitation.builder()
                            .status(REQUEST_PATTERN)
                            .build(),
                    GoalInvitation.builder()
                            .status(RequestStatus.REJECTED)
                            .build());

            goalInvitationFilterDto = GoalInvitationFilterDto.builder()
                    .status(REQUEST_PATTERN)
                    .build();

            List<GoalInvitation> goalInvitationsAfterFilter = List.of(
                    GoalInvitation.builder()
                            .status(REQUEST_PATTERN)
                            .build());

            assertEquals(goalInvitationsAfterFilter, goalInvitationRequestStatusFilter.apply(goalInvitations,
                    goalInvitationFilterDto).toList());
        }
    }

    @Nested
    class NegativeTests {

        @Test
        @DisplayName("If GoalInvitationFilterDto status is null than return false")
        void whenGoalInvitationFilterDtoSpecifiedStatusIsNullThenReturnFalse() {
            goalInvitationFilterDto = GoalInvitationFilterDto.builder()
                    .status(null)
                    .build();

            assertFalse(goalInvitationRequestStatusFilter.isApplicable(goalInvitationFilterDto));
        }
    }
}