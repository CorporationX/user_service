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

    @InjectMocks
    private GoalInvitationInvitedIdFilter goalInvitationInvitedIdFilter;

    private GoalInvitationFilterDto goalInvitationFilterDto;

    private final static Long INVITED_ID_NEGATIVE_PATTERN = -1L;
    private final static Long INVITED_ID_PATTERN = 1L;
    private final static Long INVITED_USER_ID_ONE = 1L;
    private final static Long INVITED_USER_ID_TWO = 2L;

    @Nested
    class PositiveTests {

        @Test
        @DisplayName("Если у GoalInvitationFilterDto заполнено поле invitedId не null и оно больше нуля, " +
                "тогда возвращаем true")
        void whenGoalInvitationFilterDtoSpecifiedInvitedIdISNotNullAndMoreThanZeroThenReturnTrue() {
            goalInvitationFilterDto = GoalInvitationFilterDto.builder()
                    .invitedId(INVITED_ID_PATTERN)
                    .build();

            assertTrue(goalInvitationInvitedIdFilter.isApplicable(goalInvitationFilterDto));
        }

        @Test
        @DisplayName("Если у GoalInvitationFilterDto корректно заполнено поле invitedId, " +
                "тогда возвращаем отфильтрованный список")
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
        @DisplayName("Если у GoalInvitationFilterDto поле invitedId null тогда возвращаем false")
        void whenGoalInvitationFilterDtoSpecifiedInvitedIdIsNullThenReturnFalse() {
            goalInvitationFilterDto = GoalInvitationFilterDto.builder()
                    .invitedId(null)
                    .build();

            assertFalse(goalInvitationInvitedIdFilter.isApplicable(goalInvitationFilterDto));
        }

        @Test
        @DisplayName("Если у GoalInvitationFilterDto поле invitedId меньше нуля тогда возвращаем false")
        void whenGoalInvitationFilterDtoSpecifiedInvitedIdIsLessThanZeroThenReturnFalse() {
            goalInvitationFilterDto = GoalInvitationFilterDto.builder()
                    .invitedId(INVITED_ID_NEGATIVE_PATTERN)
                    .build();

            assertFalse(goalInvitationInvitedIdFilter.isApplicable(goalInvitationFilterDto));
        }
    }
}