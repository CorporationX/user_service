package school.faang.user_service.service.score;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.aspect.score.ScoreActionType;
import school.faang.user_service.entity.Role;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.model.user.RoleThesaurus;
import school.faang.user_service.service.role.RoleService;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class ScoreTrackingServiceTest {

    @Mock
    private UserScoreService userScoreService;

    @Mock
    private ScoreRuleService scoreRuleService;

    @Mock
    private LeaderboardService leaderboardService;

    @Mock
    private RoleService roleService;

    @InjectMocks
    private ScoreTrackingService scoreTrackingService;

    @Test
    void trackAfterCompleteGoal_shouldUpdateAllUsers() {
        int delta = 50;
        User user1 = new User(); user1.setId(1L);
        User user2 = new User(); user2.setId(2L);
        Goal goal = new Goal();
        goal.setUsers(List.of(user1, user2));

        Mockito.when(scoreRuleService.getScoreByTypeOrThrow(ScoreActionType.COMPLETE_GOAL))
                .thenReturn(delta);
        Mockito.when(userScoreService.incrementScore(Mockito.eq(1L), Mockito.eq(delta)))
                .thenReturn(100);
        Mockito.when(userScoreService.incrementScore(Mockito.eq(2L), Mockito.eq(delta)))
                .thenReturn(200);

        scoreTrackingService.trackAfterCompleteGoal(goal);

        Mockito.verify(userScoreService).incrementScore(1L, delta);
        Mockito.verify(userScoreService).incrementScore(2L, delta);
        Mockito.verify(leaderboardService).updateLeaderboard(1L, 100);
        Mockito.verify(leaderboardService).updateLeaderboard(2L, 200);
    }

    @Test
    void trackAfterCompleteEvent_shouldSkipIfNotCompleted() {
        Event event = new Event();
        event.setStatus(EventStatus.CANCELED);

        scoreTrackingService.trackAfterCompleteEvent(event);

        Mockito.verifyNoInteractions(roleService, scoreRuleService, userScoreService, leaderboardService);
    }

    @Test
    void trackAfterCompleteEvent_shouldUpdateOwnerAndAttendees() {
        Event event = new Event();
        event.setStatus(EventStatus.COMPLETED);

        User owner = new User(); owner.setId(1L);
        User attendee1 = new User(); attendee1.setId(2L);
        User attendee2 = new User(); attendee2.setId(3L);

        event.setOwner(owner);
        event.setAttendees(List.of(attendee1, attendee2));

        Role attendeeRole = new Role(); attendeeRole.setName(RoleThesaurus.ATTENDEE);
        Role ownerRole = new Role(); ownerRole.setName(RoleThesaurus.OWNER);

        Mockito.when(roleService.getByNameOrThrow(RoleThesaurus.ATTENDEE))
                .thenReturn(attendeeRole);
        Mockito.when(roleService.getByNameOrThrow(RoleThesaurus.OWNER))
                .thenReturn(ownerRole);

        Mockito.when(scoreRuleService.getScoreByRoleOrThrow(ScoreActionType.COMPLETE_EVENT, "ATTENDEE"))
                .thenReturn(10);
        Mockito.when(scoreRuleService.getScoreByRoleOrThrow(ScoreActionType.COMPLETE_EVENT, "OWNER"))
                .thenReturn(20);

        Mockito.when(userScoreService.incrementScore(2L, 10)).thenReturn(110);
        Mockito.when(userScoreService.incrementScore(3L, 10)).thenReturn(120);
        Mockito.when(userScoreService.incrementScore(1L, 20)).thenReturn(200);

        scoreTrackingService.trackAfterCompleteEvent(event);

        Mockito.verify(userScoreService).incrementScore(2L, 10);
        Mockito.verify(userScoreService).incrementScore(3L, 10);
        Mockito.verify(userScoreService).incrementScore(1L, 20);

        Mockito.verify(leaderboardService).updateLeaderboard(2L, 110);
        Mockito.verify(leaderboardService).updateLeaderboard(3L, 120);
        Mockito.verify(leaderboardService).updateLeaderboard(1L, 200);
    }
}

