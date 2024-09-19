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
class GoalInvitationInvitedIdFilterTest {

    private final static Long INVITED_ID_NEGATIVE_PATTERN = -1L;
    private final static Long INVITED_ID_PATTERN = 1L;
    private final static Long INVITED_USER_ID_ONE = 1L;
    private final static Long INVITED_USER_ID_TWO = 2L;

    @InjectMocks
    private GoalInvitationInvitedIdFilter goalInvitationInvitedIdFilter;

    private GoalInvitationFilterDto goalInvitationFilterDto;

    @Nested
    class PositiveTests {

        @Test
        @DisplayName("If GoalInvitationFilterDto invitedId not null and positive than return true")
        void whenGoalInvitationFilterDtoSpecifiedInvitedIdISNotNullAndMoreThanZeroThenReturnTrue() {
            goalInvitationFilterDto = GoalInvitationFilterDto.builder()
                    .invitedId(INVITED_ID_PATTERN)
                    .build();

            assertTrue(goalInvitationInvitedIdFilter.isApplicable(goalInvitationFilterDto));
        }

        @Test
        @DisplayName("If GoalInvitationFilterDto invitedId not null and positive than return sorted list")
        void whenGoalInvitationFilterDtoSpecifiedInvitedIdThenReturnFilteredList() {
            Stream<GoalInvitation> goalInvitations = Stream.of(
                    GoalInvitation.builder()
                            .invited(User.builder()
                                    .id(INVITED_USER_ID_ONE)
                                    .build())
                            .build(),
                    GoalInvitation.builder()
                            .invited(User.builder()
                                    .id(INVITED_USER_ID_TWO)
                                    .build())
                            .build());

            goalInvitationFilterDto = GoalInvitationFilterDto.builder()
                    .invitedId(INVITED_ID_PATTERN)
                    .build();

            List<GoalInvitation> goalInvitationsAfterFilter = List.of(
                    GoalInvitation.builder()
                            .invited(User.builder()
                                    .id(INVITED_USER_ID_ONE)
                                    .build())
                            .build());

            assertEquals(goalInvitationsAfterFilter, goalInvitationInvitedIdFilter.apply(goalInvitations,
                    goalInvitationFilterDto).toList());
        }
    }


    @Nested
    class NegativeTests {

        @Test
        @DisplayName("If GoalInvitationFilterDto invitedId is null than return false")
        void whenGoalInvitationFilterDtoSpecifiedInvitedIdIsNullThenReturnFalse() {
            goalInvitationFilterDto = GoalInvitationFilterDto.builder()
                    .invitedId(null)
                    .build();

            assertFalse(goalInvitationInvitedIdFilter.isApplicable(goalInvitationFilterDto));
        }

        @Test
        @DisplayName("If GoalInvitationFilterDto invitedId is less than zero than return false")
        void whenGoalInvitationFilterDtoSpecifiedInvitedIdIsLessThanZeroThenReturnFalse() {
            goalInvitationFilterDto = GoalInvitationFilterDto.builder()
                    .invitedId(INVITED_ID_NEGATIVE_PATTERN)
                    .build();

            assertFalse(goalInvitationInvitedIdFilter.isApplicable(goalInvitationFilterDto));
        }
    }
}