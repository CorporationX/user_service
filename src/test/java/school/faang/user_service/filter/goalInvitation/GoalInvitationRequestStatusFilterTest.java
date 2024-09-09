package school.faang.user_service.filter.goalInvitation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.goal.GoalInvitationFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.goal.GoalInvitation;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class GoalInvitationRequestStatusFilterTest {

    @Spy
    private GoalInvitationRequestStatusFilter goalInvitationRequestStatusFilter;

    private GoalInvitationFilterDto goalInvitationFilterDto;

    private final RequestStatus REQUEST_PATTERN = RequestStatus.ACCEPTED;


    @Nested
    class PositiveTests {

        @Nested
        class IsApplicable {

            @Test
            @DisplayName("Если у GoalInvitationFilterDto заполнено поле status тогда возвращаем true")
            void whenGoalInvitationFilterDtoSpecifiedStatusIsNotNullThenReturnTrue() {
                goalInvitationFilterDto = GoalInvitationFilterDto.builder()
                        .status(REQUEST_PATTERN)
                        .build();

                assertTrue(goalInvitationRequestStatusFilter.isApplicable(goalInvitationFilterDto));
            }
        }

        @Nested
        class Apply {

            @Test
            @DisplayName("Если у GoalInvitationFilterDto корректно заполнено поле status, " +
                    "тогда возвращаем отфильтрованный список")
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
    }

    @Nested
    class NegativeTests {

        @Nested
        class IsApplicable {

            @Test
            @DisplayName("Если у GoalInvitationFilterDto поле status null тогда возвращаем false")
            void whenGoalInvitationFilterDtoSpecifiedStatusIsNullThenReturnFalse() {
                goalInvitationFilterDto = GoalInvitationFilterDto.builder()
                        .status(null)
                        .build();

                assertFalse(goalInvitationRequestStatusFilter.isApplicable(goalInvitationFilterDto));
            }
        }
    }
}