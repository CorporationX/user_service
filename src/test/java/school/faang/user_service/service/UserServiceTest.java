package school.faang.user_service.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.util.TestDataFactory;

import java.util.List;
import java.util.Optional;

import static java.lang.Long.*;
import static java.util.List.of;
import static java.util.Optional.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @InjectMocks
    private UserService userService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private MentorshipService mentorshipService;
    @Mock
    private GoalRepository goalRepository;
    @Mock
    private UserMapper userMapper;
    @Mock
    private EventRepository eventRepository;

    private static final Long USER_ID = 1L;
    private static final Long INVALID_USER_ID = MAX_VALUE;

    @Test
    void givenUserIdWhenFindUserByIdThenReturnUser() {
        // given - precondition
        var user = TestDataFactory.createUser();
        var userDto = TestDataFactory.createUserDto();

        when(userRepository.findById(USER_ID))
                .thenReturn(Optional.of(user));
        when(userMapper.toDto(user))
                .thenReturn(userDto);

            // when - action
        var actualResult = userService.findUserById(USER_ID);

        // then - verify the output
        assertThat(actualResult).isNotNull();
        assertThat(actualResult.getId()).isEqualTo(userDto.getId());

        verify(userRepository).findById(USER_ID);
        verify(userMapper).toDto(user);
    }

    @Test
    void givenInvalidUserIdWhenFindUserByIdThenThrowException() {
        // given - precondition
        when(userRepository.findById(INVALID_USER_ID))
                .thenReturn(empty());

        // when - action and
        // then - verify the output
        assertThatThrownBy(() -> userService.findUserById(INVALID_USER_ID))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("User with id " + INVALID_USER_ID + " not found");
    }

    @Test
    void givenUsersIdsWhenFindAllUsersByIdsThenReturnUsers() {
        // given - precondition
        List<Long> userIds = of(1L, 2L, 3L);
        var usersList = TestDataFactory.createUsersList();
        var userDtosList = TestDataFactory.createUserDtosList();

        when(userRepository.findAllById(userIds))
                .thenReturn(usersList);
        for (int i = 0; i < usersList.size(); i++) {
            when(userMapper.toDto(usersList.get(i))).thenReturn(userDtosList.get(i));
        }

        // when - action
        var actualResult = userService.findUsersByIds(userIds);

        // then - verify the output
        assertThat(actualResult).isNotNull();
        assertThat(actualResult.size()).isEqualTo(userIds.size());
        assertThat(actualResult).extracting(UserDto::getId)
                .containsExactlyInAnyOrderElementsOf(userIds);

        verify(userRepository).findAllById(userIds);
        usersList.forEach(user -> verify(userMapper).toDto(user));
    }

    @Test
    void givenUserIdWhenDeactivateUserByIdThenUserIsDeactivated() {
        // given - precondition
        var user = TestDataFactory.createUser();
        var goal = TestDataFactory.createNewGoal();
        user.getGoals().add(goal);

        var userDto = TestDataFactory.createUserDto();

        userDto.setActive(false);

        when(userRepository.findById(USER_ID)).
                thenReturn(Optional.of(user));
        when(mentorshipService.stopUserMentorship(USER_ID))
                .thenReturn(userDto);
        when(userMapper.toDto(user))
                .thenReturn(userDto);
        when(goalRepository.save(any(Goal.class)))
                .thenReturn(goal);
        doNothing().when(goalRepository).delete(any(Goal.class));
        doNothing().when(eventRepository).deleteAll(anyList());

        // when - action
        var actualResult = userService.deactivateUserById(USER_ID);

        // then - verify the output
        assertThat(actualResult).isNotNull();
        assertThat(actualResult.getActive()).isFalse();

        verify(userRepository).findById(USER_ID);
        verify(goalRepository, times(1)).delete(any(Goal.class));
        verify(eventRepository).deleteAll(anyList());
        verify(mentorshipService).stopUserMentorship(USER_ID);
        verify(userRepository, times(1)).save(user);
        verify(userMapper).toDto(user);
    }

    @Test
    void givenInvalidUserIdWhenDeactivateUserByIdThenThrowException() {
        // given - precondition
        when(userRepository.findById(INVALID_USER_ID))
                .thenReturn(empty());

        // when - action and
        // then - verify the output
        assertThatThrownBy(() -> userService.deactivateUserById(INVALID_USER_ID))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("User with id " + INVALID_USER_ID + " not found");
    }
}