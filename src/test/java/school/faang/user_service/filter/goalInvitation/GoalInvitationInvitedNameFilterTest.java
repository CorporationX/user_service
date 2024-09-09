package school.faang.user_service.filter.goalInvitation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.goal.GoalInvitationFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.GoalInvitation;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class GoalInvitationInvitedNameFilterTest {

    @Spy
    private GoalInvitationInvitedNameFilter goalInvitationInvitedNameFilter;

    private GoalInvitationFilterDto goalInvitationFilterDto;

    private final String INVENTED_NAME_PATTERN = "inventedName";


    @Nested
    class PositiveTests {

        @Nested
        class IsApplicable {

            @Test
            @DisplayName("Если у GoalInvitationFilterDto заполнено поле invitedNamePattern не null и оно больше нуля, " +
                    "тогда возвращаем true")
            void whenGoalInvitationFilterDtoSpecifiedInvitedNamePatternISNotNullAndMoreThanZeroThenReturnTrue() {
                goalInvitationFilterDto = GoalInvitationFilterDto.builder()
                        .invitedNamePattern(INVENTED_NAME_PATTERN)
                        .build();

                assertTrue(goalInvitationInvitedNameFilter.isApplicable(goalInvitationFilterDto));
            }
        }

        @Nested
        class Apply {

            @Test
            @DisplayName("Если у GoalInvitationFilterDto корректно заполнено поле invitedNamePattern, " +
                    "тогда возвращаем отфильтрованный список")
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
    }

    @Nested
    class NegativeTests {

        @Nested
        class IsApplicable {

            @Test
            @DisplayName("Если у GoalInvitationFilterDto поле invitedNamePattern null тогда возвращаем false")
            void whenGoalInvitationFilterDtoSpecifiedInvitedIdIsNullThenReturnFalse() {
                goalInvitationFilterDto = GoalInvitationFilterDto.builder()
                        .invitedNamePattern(null)
                        .build();

                assertFalse(goalInvitationInvitedNameFilter.isApplicable(goalInvitationFilterDto));
            }

            @Test
            @DisplayName("Если у GoalInvitationFilterDto поле invitedNamePattern пустое тогда возвращаем false")
            void whenGoalInvitationFilterDtoSpecifiedInvitedIdIsLessThanZeroThenReturnFalse() {
                goalInvitationFilterDto = GoalInvitationFilterDto.builder()
                        .invitedNamePattern("   ")
                        .build();

                assertFalse(goalInvitationInvitedNameFilter.isApplicable(goalInvitationFilterDto));
            }
        }
    }
}