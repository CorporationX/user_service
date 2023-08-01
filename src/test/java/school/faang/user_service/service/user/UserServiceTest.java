package school.faang.user_service.service.user;

import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.MentorshipService;
import school.faang.user_service.service.event.EventService;
import school.faang.user_service.service.goal.GoalService;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    GoalService goalService;

    @Mock
    EventService eventService;

    @Mock
    MentorshipService mentorshipService;

    @InjectMocks
    private UserService userService;
    @Mock
    private UserRepository userRepository;

    @Test
    @DisplayName("Test: User exists")
    public void testFindUserByIdPositive() {
        Long userId = 1L;
        when(userService.isUserExist(userId)).thenReturn(true);
        assertTrue(userService.isUserExist(userId));
    }

    @Test
    @DisplayName("Test: User does not exists")
    public void testFindUserByIdNegative() {
        Long userId = 1L;
        when(userService.isUserExist(userId)).thenReturn(false);
        assertFalse(userService.isUserExist(userId));
    }

    @Test
    @DisplayName("Test: Should delete goals only with 1 user")
    public void testDeletingGoalsWithOneUser() {
        GoalDto runningDto = new GoalDto();
        runningDto.setUserIds(List.of(1L, 2L));

        GoalDto swimmingDto = new GoalDto();
        swimmingDto.setId(1L);
        swimmingDto.setUserIds(List.of(1L));

        GoalDto codingDto = new GoalDto();
        codingDto.setUserIds(List.of(1L, 2L));

        GoalDto paintingDto = new GoalDto();
        paintingDto.setId(2L);
        paintingDto.setUserIds(List.of(1L));


        List<GoalDto> goalDtos = List.of(runningDto, swimmingDto, codingDto, paintingDto);
        when(goalService.getGoalsByUser(1L)).thenReturn(goalDtos);
        when(eventService.getParticipatedEvents(1L)).thenReturn(List.of());

        userService.deactivateUser(1L);

        Mockito.verify(goalService, Mockito.times(1)).deleteAllByIds(List.of(1L, 2L));
    }

    @Test
    @DisplayName("Test: Should remove user from goal if users > 1")
    public void testRemovingUserFromGoal() {
        GoalDto runningDto = new GoalDto();
        runningDto.setId(1L);
        runningDto.setUserIds(List.of(1L, 2L));

        GoalDto swimmingDto = new GoalDto();
        swimmingDto.setUserIds(List.of(1L));

        GoalDto codingDto = new GoalDto();
        codingDto.setId(2L);
        codingDto.setUserIds(List.of(1L, 2L));

        GoalDto paintingDto = new GoalDto();
        paintingDto.setUserIds(List.of(1L));


        List<GoalDto> goalDtos = List.of(runningDto, swimmingDto, codingDto, paintingDto);
        when(goalService.getGoalsByUser(1L)).thenReturn(goalDtos);
        when(eventService.getParticipatedEvents(1L)).thenReturn(List.of());

        userService.deactivateUser(1L);

        Mockito.verify(goalService, Mockito.times(1)).removeUserFromGoals(List.of(1L, 2L), 1L);
    }

    @Test
    @DisplayName("Test: Should delete events only with 1 user")
    public void testDeletingEventsWithOneUser() {
        EventDto runningDto = new EventDto();
        runningDto.setAttendeesIds(List.of(1L, 2L));

        EventDto swimmingDto = new EventDto();
        swimmingDto.setId(1L);
        swimmingDto.setOwnerId(1L);
        swimmingDto.setAttendeesIds(List.of(1L));

        EventDto codingDto = new EventDto();
        codingDto.setAttendeesIds(List.of(1L, 2L));

        EventDto paintingDto = new EventDto();
        paintingDto.setId(2L);
        paintingDto.setOwnerId(1L);
        paintingDto.setAttendeesIds(List.of(1L));


        List<EventDto> eventDtos = List.of(runningDto, swimmingDto, codingDto, paintingDto);
        when(eventService.getParticipatedEvents(1L)).thenReturn(eventDtos);
        when(goalService.getGoalsByUser(1L)).thenReturn(List.of());

        userService.deactivateUser(1L);

        Mockito.verify(eventService, Mockito.times(1)).deleteAllByIds(List.of(1L, 2L));
    }

    @Test
    @DisplayName("Test: Should NOT delete events of owner is different user")
    public void testDeletingEventsWithDifferentUser() {
        EventDto runningDto = new EventDto();
        runningDto.setAttendeesIds(List.of(1L, 2L));

        EventDto swimmingDto = new EventDto();
        swimmingDto.setId(1L);
        swimmingDto.setOwnerId(1L);
        swimmingDto.setAttendeesIds(List.of(1L));

        EventDto codingDto = new EventDto();
        codingDto.setAttendeesIds(List.of(1L, 2L));

        EventDto paintingDto = new EventDto();
        paintingDto.setId(2L);
        paintingDto.setOwnerId(2L);
        paintingDto.setAttendeesIds(List.of(1L));


        List<EventDto> eventDtos = List.of(runningDto, swimmingDto, codingDto, paintingDto);
        when(eventService.getParticipatedEvents(1L)).thenReturn(eventDtos);
        when(goalService.getGoalsByUser(1L)).thenReturn(List.of());

        userService.deactivateUser(1L);

        Mockito.verify(eventService, Mockito.times(1)).deleteAllByIds(List.of(1L));
    }

    @Test
    @DisplayName("Test: Should remove user from events if users > 1")
    public void testRemovingUserFromEvent() {
        EventDto runningDto = new EventDto();
        runningDto.setId(1L);
        runningDto.setAttendeesIds(List.of(1L, 2L));

        EventDto swimmingDto = new EventDto();
        swimmingDto.setId(2L);
        swimmingDto.setAttendeesIds(List.of(1L));

        EventDto codingDto = new EventDto();
        codingDto.setId(3L);
        codingDto.setAttendeesIds(List.of(1L, 2L));

        EventDto paintingDto = new EventDto();
        paintingDto.setId(4L);
        paintingDto.setAttendeesIds(List.of(1L));


        List<EventDto> eventsDtos = List.of(runningDto, swimmingDto, codingDto, paintingDto);
        when(eventService.getParticipatedEvents(1L)).thenReturn(eventsDtos);
        when(goalService.getGoalsByUser(1L)).thenReturn(List.of());

        userService.deactivateUser(1L);

        Mockito.verify(eventService, Mockito.times(1)).removeUserFromEvents(List.of(1L, 2L, 3L, 4L), 1L);
    }

    @Test
    public void testCancelMentoring() {
        userService.deactivateUser(1L);
        Mockito.verify(mentorshipService, Mockito.times(1)).cancelMentoring(1L);
    }
}