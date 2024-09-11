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

    @InjectMocks
    private GoalInvitationInviterNameFilter goalInvitationInviterNameFilter;

    private GoalInvitationFilterDto goalInvitationFilterDto;

    private final static String INVITER_NAME_PATTERN = "inviterName";


    @Nested
    class PositiveTests {

        @Test
        @DisplayName("Если у GoalInvitationFilterDto заполнено поле inviterNamePattern не null и оно больше нуля, " +
                "тогда возвращаем true")
        void whenGoalInvitationFilterDtoSpecifiedInviterNamePatternISNotNullAndMoreThanZeroThenReturnTrue() {
            goalInvitationFilterDto = GoalInvitationFilterDto.builder()
                    .inviterNamePattern(INVITER_NAME_PATTERN)
                    .build();

            assertTrue(goalInvitationInviterNameFilter.isApplicable(goalInvitationFilterDto));
        }

        @Test
        @DisplayName("Если у GoalInvitationFilterDto корректно заполнено поле inviterNamePattern, " +
                "тогда возвращаем отфильтрованный список")
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

        @Nested
        class IsApplicable {

            @Test
            @DisplayName("Если у GoalInvitationFilterDto поле inviterNamePattern null тогда возвращаем false")
            void whenGoalInvitationFilterDtoSpecifiedInvitedIdIsNullThenReturnFalse() {
                goalInvitationFilterDto = GoalInvitationFilterDto.builder()
                        .inviterNamePattern(null)
                        .build();

                assertFalse(goalInvitationInviterNameFilter.isApplicable(goalInvitationFilterDto));
            }

            @Test
            @DisplayName("Если у GoalInvitationFilterDto поле inviterNamePattern пустое тогда возвращаем false")
            void whenGoalInvitationFilterDtoSpecifiedInvitedIdIsLessThanZeroThenReturnFalse() {
                goalInvitationFilterDto = GoalInvitationFilterDto.builder()
                        .inviterNamePattern("   ")
                        .build();

                assertFalse(goalInvitationInviterNameFilter.isApplicable(goalInvitationFilterDto));
            }
        }
    }
}