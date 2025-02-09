package school.faang.user_service.service.impl;


import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private GoalRepository goalRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .active(true)
                .mentees(new ArrayList<>())
                .mentors(new ArrayList<>())
                .build();
    }

    @Test
    void test_deactivateUser_userNotFound_throwsException() {
        long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.deactivateUser(userId));
        verify(userRepository, times(1)).findById(userId);
        verifyNoInteractions(eventRepository, goalRepository);
    }

    @Test
    void test_deactivateUser_userFound_deactivatesUserAndStopsActivities() {
        long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(eventRepository.findAllByUserId(userId)).thenReturn(Collections.emptyList());
        when(eventRepository.findParticipatedEventsByUserId(userId)).thenReturn(Collections.emptyList());
        when(goalRepository.findGoalsByUserId(userId)).thenReturn(Stream.empty());

        userService.deactivateUser(userId);

        assertFalse(user.isActive());
        verify(eventRepository, times(1)).findAllByUserId(userId);
        verify(eventRepository, times(1)).findParticipatedEventsByUserId(userId);
        verify(goalRepository, times(1)).findGoalsByUserId(userId);
    }

    @Test
    void test_deactivateUser_userHasEvents_deletesOwnedEventsAndRemovesFromParticipated() {
        long userId = 1L;
        long eventId = 2L;
        long secondEventId = 3L;
        Event ownedEvent = Event.builder().id(eventId).build();
        Event participatedEvent = Event.builder().id(secondEventId).attendees(new ArrayList<>(List.of(user))).build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(eventRepository.findAllByUserId(userId)).thenReturn(List.of(ownedEvent));
        when(eventRepository.findParticipatedEventsByUserId(userId)).thenReturn(List.of(participatedEvent));

        userService.deactivateUser(userId);

        verify(eventRepository).deleteById(eventId);
        assertFalse(participatedEvent.getAttendees().contains(user));
    }

    @Test
    void test_deactivateUser_userHasGoals_removesUserFromGoals() {
        long userId = 1L;
        long goalId = 4L;
        Goal goal = Goal.builder()
                .id(goalId)
                .users(new HashSet<>(Set.of(user)))
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(goalRepository.findGoalsByUserId(userId)).thenReturn(Stream.of(goal));
        when(goalRepository.findUsersByGoalId(goalId)).thenReturn(List.of(user));

        userService.deactivateUser(userId);

        verify(goalRepository).deleteById(goalId);
    }

    @Test
    void test_deactivateUser_userHasGoalsWithMultipleUsers_removesUserFromGoal() {
        long userId = 1L;
        long secondUserId = 2L;
        long goalId = 4L;
        User userToDeactivate = User.builder()
                .id(userId)
                .active(true)
                .mentees(Collections.emptyList())
                .mentors(Collections.emptyList())
                .build();

        User anotherUser = User.builder()
                .id(secondUserId)
                .active(true)
                .mentees(Collections.emptyList())
                .mentors(Collections.emptyList())
                .build();

        Goal goal = new Goal();
        goal.setId(goalId);
        goal.setUsers(new HashSet<>(Set.of(userToDeactivate, anotherUser)));

        when(userRepository.findById(userId)).thenReturn(Optional.of(userToDeactivate));
        when(eventRepository.findAllByUserId(userId)).thenReturn(Collections.emptyList());
        when(eventRepository.findParticipatedEventsByUserId(userId)).thenReturn(Collections.emptyList());
        when(goalRepository.findGoalsByUserId(userId)).thenReturn(Stream.of(goal));
        when(goalRepository.findUsersByGoalId(goalId)).thenReturn(new ArrayList<>(goal.getUsers()));

        userService.deactivateUser(userId);

        assertFalse(goal.getUsers().contains(userToDeactivate));

        verify(goalRepository, never()).deleteById(goalId);
    }


    @Test
    void test_deactivateUser_userHasMenteesAndMentors_removesMentorship() {
        long userId = 1L;
        long menteeId = 2L;
        long mentorId = 3L;
        User mentee = User.builder().id(menteeId).mentors(new ArrayList<>(List.of(user))).build();
        User mentor = User.builder().id(mentorId).mentees(new ArrayList<>(List.of(user))).build();

        user.setMentees(new ArrayList<>(List.of(mentee)));
        user.setMentors(new ArrayList<>(List.of(mentor)));

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        userService.deactivateUser(userId);

        assertFalse(mentee.getMentors().contains(user));
        assertFalse(mentor.getMentees().contains(user));
    }

    @Test
    void test_deactivateUser_userIsMentorForGoals_removesMentorshipFromGoals() {
        long userId = 1L;
        long goalId = 5L;
        Goal goal = Goal.builder().id(goalId).mentor(user).build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(goalRepository.findAllByMentorId(userId)).thenReturn(List.of(goal));

        userService.deactivateUser(userId);
        assertNull(goal.getMentor());
    }
}