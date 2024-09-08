package school.faang.user_service.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalInvitation;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


class GoalInvitationMapperTest {

    private final GoalInvitationMapper mapper = new GoalInvitationMapperImpl();
    private Goal goal;
    private User inviter;
    private User invited;

    @BeforeEach
    public void setUp() {
        goal = Goal.builder().id(1L).title("Test Goal").build();
        inviter = User.builder().id(2L).username("inviter").build();
        invited = User.builder().id(3L).username("invited").build();
    }

    @Test
    void testToEntity() {
        GoalInvitationDto dto = new GoalInvitationDto(invited.getId(), goal.getId());
        GoalInvitation entity = mapper.toEntity(dto, goal, inviter, invited);

        equalsDtoAndEntity(dto, entity);
        assertEquals(inviter, entity.getInviter());
        assertEquals(invited, entity.getInvited());
        assertEquals(goal, entity.getGoal());
    }

    @Test
    void testToDto() {
        GoalInvitation entity = GoalInvitation.builder()
                .id(4L)
                .goal(goal)
                .inviter(inviter)
                .invited(invited)
                .status(RequestStatus.ACCEPTED)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        GoalInvitationDto dto = mapper.toDto(entity);

        equalsDtoAndEntity(dto, entity);
    }

    @Test
    void testToDtos() {
        List<GoalInvitation> entities = List.of(
                GoalInvitation.builder()
                        .id(4L)
                        .goal(goal)
                        .inviter(inviter)
                        .invited(invited)
                        .status(RequestStatus.ACCEPTED)
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build(),
                GoalInvitation.builder()
                        .id(5L)
                        .goal(goal)
                        .inviter(inviter)
                        .invited(invited)
                        .status(RequestStatus.PENDING)
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build()
        );

        List<GoalInvitationDto> dtos = mapper.toDtos(entities);

        assertEquals(2, dtos.size());
        equalsDtoAndEntity(dtos.get(0), entities.get(0));
        equalsDtoAndEntity(dtos.get(0), entities.get(0));
    }

    private void equalsDtoAndEntity(GoalInvitationDto dto, GoalInvitation entity) {
        assertEquals(dto.getInvitedUserId(), entity.getInvited().getId());
        assertEquals(dto.getGoalId(), entity.getGoal().getId());
        assertEquals(dto.getStatus(), entity.getStatus());
        assertEquals(dto.getCreatedAt(), entity.getCreatedAt());
        assertEquals(dto.getUpdatedAt(), entity.getUpdatedAt());
    }
}