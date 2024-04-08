package school.faang.user_service.service.user;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.jira.JiraAccountDto;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.entity.jira.JiraAccount;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.mapper.jira.JiraAccountMapper;
import school.faang.user_service.mapper.jira.JiraAccountMapperImpl;
import school.faang.user_service.mapper.user.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.jira.JiraAccountRepository;
import school.faang.user_service.service.user.filter.UserFilter;
import school.faang.user_service.validation.user.UserValidator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    private UserService userService;
    private UserRepository userRepository;
    private UserMapper userMapper;
    private UserFilter userFilter;
    private JiraAccountMapper jiraAccountMapper;

    private User user;
    private User mentee;
    private Goal goal;
    private Event event;
    private User premiumUser;
    private UserDto premiumUserDto;
    private UserValidator userValidator;
    private UserDto userDto;
    private JiraAccount jiraAccount;
    private JiraAccountDto jiraAccountDto;
    private JiraAccountRepository jiraAccountRepository;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .username("Valid username")
                .email("valid@email.com")
                .phone("+71234567890")
                .active(true)
                .build();
        userDto = UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .phone(user.getPhone())
                .active(user.isActive())
                .build();
        mentee = User.builder()
                .id(2L)
                .mentors(new ArrayList<>(List.of(user)))
                .build();
        goal = Goal.builder()
                .id(3L)
                .mentor(user)
                .status(GoalStatus.ACTIVE)
                .users(new ArrayList<>(List.of(user)))
                .build();
        event = Event.builder()
                .id(4L)
                .owner(user)
                .status(EventStatus.PLANNED)
                .build();
        user.setMentees(new ArrayList<>(List.of(mentee)));
        user.setGoals(new ArrayList<>(List.of(goal)));
        user.setOwnedEvents(new ArrayList<>(List.of(event)));
        premiumUser = User.builder()
                .id(1L)
                .username("Valid username")
                .email("valid@email.com")
                .phone("+71234567890")
                .premium(new Premium())
                .build();
        premiumUserDto = UserDto.builder()
                .id(premiumUser.getId())
                .username(premiumUser.getUsername())
                .email(premiumUser.getEmail())
                .phone(premiumUser.getPhone())
                .isPremium(true)
                .build();
        jiraAccount = JiraAccount.builder()
                .user(user)
                .username("Valid username")
                .password("v@l1dpAssw0rd")
                .projectUrl("https://faang-school.atlassian.net/")
                .build();
        jiraAccountDto = JiraAccountDto.builder()
                .userId(jiraAccount.getUser().getId())
                .username(jiraAccount.getUsername())
                .password(jiraAccount.getPassword())
                .projectUrl(jiraAccount.getProjectUrl())
                .build();
        user.setJiraAccount(jiraAccount);
        userRepository = mock(UserRepository.class);
        userMapper = mock(UserMapper.class);
        userFilter = mock(UserFilter.class);
        jiraAccountMapper = spy(JiraAccountMapperImpl.class);
        jiraAccountRepository = mock(JiraAccountRepository.class);
        userService = new UserService(userRepository, userMapper, List.of(userFilter), userValidator,
                jiraAccountMapper, jiraAccountRepository);
    }

    @Test
    void getPremiumUsers_UsersFoundAndFiltered_ThenReturnedAsDto() {
        when(userRepository.findPremiumUsers()).thenReturn(Stream.of(premiumUser));
        when(userFilter.isApplicable(any(UserFilterDto.class))).thenReturn(true);
        doNothing().when(userFilter).apply(anyList(), any(UserFilterDto.class));
        when(userMapper.toDto(List.of(premiumUser))).thenReturn(List.of(premiumUserDto));

        userService.getPremiumUsers(new UserFilterDto());

        verify(userRepository, times(1)).findPremiumUsers();
        verify(userMapper, times(1)).toDto(List.of(premiumUser));
    }

    @Test
    void getUser_UserFound_ThenReturnedAsDto() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(userMapper.toDto(user)).thenReturn(userDto);

        userService.getUser(user.getId());

        assertAll(
                () -> verify(userRepository, times(1)).findById(user.getId()),
                () -> verify(userMapper, times(1)).toDto(user)
        );
    }

    @Test
    void getUser_UserNotFound_ShouldThrowEntityNotFoundException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
                userService.getUser(5L));
    }

    @Test
    void getJiraAccountInfo_UserFound_JiraAccountReturnedAsDto() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));

        JiraAccountDto returned = userService.getJiraAccountInfo(user.getId());

        assertAll(
                () -> verify(userRepository, times(1)).findById(user.getId()),
                () -> verify(jiraAccountMapper, times(1)).toDto(user.getJiraAccount()),
                () -> assertEquals(jiraAccountDto, returned)
        );
    }

    @Test
    void getUsersByIds_UsersFound_ThenReturnedAsDto() {
        when(userRepository.findAllById(List.of(user.getId()))).thenReturn((List.of(user)));
        when(userMapper.toDto(List.of(user))).thenReturn(List.of(userDto));

        userService.getUsersByIds(List.of(user.getId()));

        assertAll(
                () -> verify(userRepository, times(1)).findAllById(List.of(user.getId())),
                () -> verify(userMapper, times(1)).toDto(List.of(user))
        );
    }

    @Test
    void getUsersByIds_UsersNotFound_ShouldThrowEntityNotFoundException() {
        when(userRepository.findAllById(List.of(589123098L))).thenReturn(Collections.emptyList());
        when(userMapper.toDto(Collections.emptyList())).thenReturn(Collections.emptyList());

        userService.getUsersByIds(List.of(589123098L));

        assertAll(
                () -> verify(userRepository, times(1)).findAllById(anyList()),
                () -> verify(userMapper, times(1)).toDto(anyList()),
                () -> assertEquals(Collections.emptyList(), userService.getUsersByIds(List.of(589123098L)))
        );
    }

    @Test
    void getSubscribers_UserFound_SubscribersListReturnedAsDto() {
        User follower = User.builder()
                .id(156L)
                .build();
        UserDto followerDto = UserDto.builder()
                .id(follower.getId())
                .build();
        user.setFollowers(List.of(follower));
        when(userRepository.findById(user.getId())).thenReturn(Optional.ofNullable(user));
        when(userMapper.toDto(List.of(follower))).thenReturn(List.of(followerDto));

        List<UserDto> returned = userService.getFollowers(user.getId());

        assertAll(
                () -> verify(userRepository, times(1)).findById(user.getId()),
                () -> verify(userMapper, times(1)).toDto(List.of(follower)),
                () -> assertEquals(List.of(followerDto), returned)
        );
    }

    @Test
    void getSubscribers_UserNotFound_ShouldThrowEntityNotFoundException() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
                userService.getFollowers(user.getId()));
    }

    @Test
    void shouldgetUserById() {
        User user = new User();
        long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        User foundUser = userService.getUserById(userId);

        assertThat(foundUser).isEqualTo(user);
    }

    @Test
    void shouldThrowExceptionForInvalidId() {
        long userId = 0L;
        assertThrows(EntityNotFoundException.class, () -> userService.getUserById(userId));
    }

    @Test
    void shouldThrowExceptionIfUserNotFound() {
        long userId = 2L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.getUserById(userId));
    }

    @Test
    void banUser_ValidArgs() {
        Long userId = 1L;
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));

        userService.banUser(userId);

        assertTrue(user.isBanned());
        verify(userRepository, times(1)).findById(anyLong());
    }

    @Test
    void saveJiraAccountInfo_JiraAccountInfoSaved_UserReturnedAsDto() {
        user.setJiraAccount(null);
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(jiraAccountRepository.save(any(JiraAccount.class))).thenReturn(jiraAccount);
        when(userMapper.toDto(user)).thenReturn(userDto);

        UserDto returned = userService.saveJiraAccountInfo(user.getId(), jiraAccountDto);

        assertAll(
                () -> verify(userRepository, times(1)).findById(user.getId()),
                () -> verify(jiraAccountMapper, times(1)).toEntity(jiraAccountDto),
                () -> assertNotNull(user.getJiraAccount()),
                () -> assertEquals(jiraAccount.getUsername(), user.getJiraAccount().getUsername()),
                () -> assertEquals(userDto, returned)
        );
    }
}