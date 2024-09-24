package school.faang.user_service.mapper.goal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalInvitation;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
class GoalInvitationMapperTest {

    private static final long USER_ID_ONE = 1L;
    private static final long USER_ID_TWO = 2L;
    private static final long GOAL_ID_ONE = 1L;
    private static final long GOAL_INVITATION_ID_ONE = 1L;
    private static final long GOAL_INVITATION_ID_TWO = 2L;

    private static final int SIZE_OF_GOAL_INVITATION_DTOS = 2;

    private static final RequestStatus REQUEST_STATUS_ACCEPTED = RequestStatus.ACCEPTED;

    @InjectMocks
    private GoalInvitationMapperImpl mapper;


    private GoalInvitation goalInvitation;
    private GoalInvitationDto goalInvitationDto;

    @BeforeEach
    void init() {
        goalInvitation = GoalInvitation.builder()
                .id(GOAL_INVITATION_ID_ONE)
                .inviter(User.builder()
                        .id(USER_ID_ONE)
                        .build())
                .invited(User.builder()
                        .id(USER_ID_TWO)
                        .build())
                .goal(Goal.builder()
                        .id(GOAL_ID_ONE)
                        .build())
                .status(REQUEST_STATUS_ACCEPTED)
                .build();
    }

    @Nested
    class ToEntity {

        @Test
        @DisplayName("If get null than return null")
        void whenDtoIsNullThenReturnNull() {
            assertNull(mapper.toEntity(null));
        }

        @Test
        @DisplayName("Convert GoalInvitationDto dto to GoalInvitation entity")
        void whenDtoIsNotNullThenReturnEntity() {
            goalInvitationDto = GoalInvitationDto.builder()
                    .id(GOAL_INVITATION_ID_ONE)
                    .inviterUserId(USER_ID_ONE)
                    .invitedUserId(USER_ID_TWO)
                    .goalId(GOAL_ID_ONE)
                    .status(REQUEST_STATUS_ACCEPTED)
                    .build();

            assertEquals(goalInvitationDto.getId(), goalInvitation.getId());
            assertEquals(goalInvitationDto.getInviterUserId(), goalInvitation.getInviter().getId());
            assertEquals(goalInvitationDto.getInvitedUserId(), goalInvitation.getInvited().getId());
            assertEquals(goalInvitationDto.getGoalId(), goalInvitation.getGoal().getId());
            assertEquals(goalInvitationDto.getStatus(), goalInvitation.getStatus());
        }
    }

    @Nested
    class ToDtos {

        @Test
        @DisplayName("If gets null than return null")
        void whenListUsersIsNullThenGetNull() {
            assertNull(mapper.toDtos(null));
        }

        @Test
        @DisplayName("If List<GoalInvitation> size is 2 than List<GoalInvitationDto> size is 2")
        void whenListOfGoalInvitationIsNotNullThenReturnListOfGoalInvitationDtos() {
            List<GoalInvitation> goalInvitations = List.of(
                    goalInvitation,
                    GoalInvitation.builder()
                            .id(GOAL_INVITATION_ID_TWO)
                            .build());

            List<GoalInvitationDto> goalInvitationsDtos = mapper.toDtos(goalInvitations);

            assertEquals(SIZE_OF_GOAL_INVITATION_DTOS, goalInvitationsDtos.size());
        }
    }
}