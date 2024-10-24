package school.faang.user_service.service.impl;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.filter.user.UserFilter;
import school.faang.user_service.model.dto.UserDto;
import school.faang.user_service.model.event.ProfileViewEvent;
import school.faang.user_service.model.entity.TelegramContact;
import school.faang.user_service.model.filter_dto.user.UserFilterDto;
import school.faang.user_service.model.entity.Country;
import school.faang.user_service.model.entity.User;
import school.faang.user_service.model.entity.UserProfilePic;
import school.faang.user_service.model.entity.Event;
import school.faang.user_service.model.entity.Goal;
import school.faang.user_service.model.entity.Promotion;
import school.faang.user_service.filter.user.UserCreatedAfterFilter;
import school.faang.user_service.filter.user.UserCreatedBeforeFilter;
import school.faang.user_service.filter.user.UserNameFilter;
import school.faang.user_service.filter.user.UserPhoneFilter;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.publisher.ProfileViewEventPublisher;
import school.faang.user_service.publisher.SearchAppearanceEventPublisher;
import school.faang.user_service.repository.PromotionRepository;
import school.faang.user_service.repository.TelegramContactRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.EventRepository;
import school.faang.user_service.repository.GoalRepository;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
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
    @Mock
    private MentorshipServiceImpl mentorshipService;
    @Mock
    PromotionRepository promotionRepository;
    @Mock
    private S3serviceImpl s3service;
    @Spy
    private UserMapper userMapper = Mappers.getMapper(UserMapper.class);
    @Mock
    private List<UserFilter> userFilters;
    @Mock
    private TelegramContactRepository telegramContactRepository;
    @Mock
    private SearchAppearanceEventPublisher searchAppearanceEventPublisher;
    @Mock
    private ProfileViewEventPublisher profileViewEventPublisher;
    @Mock
    private UserContext userContext;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private UserDto userDto;
    private Goal firstGoal;
    private Goal secondGoal;
    private Event firstEvent;
    private Event secondEvent;
    long viewerId;
    long profileOwnerId;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setActive(true);
        user.setGoals(new ArrayList<>());
        user.setOwnedEvents(new ArrayList<>());
        user.setParticipatedEvents(new ArrayList<>());

        userDto = new UserDto();
        userDto.setId(user.getId());

        firstGoal = new Goal();
        firstGoal.setId(1L);
        firstGoal.setUsers(new ArrayList<>(Arrays.asList(user)));

        secondGoal = new Goal();
        secondGoal.setId(2L);
        secondGoal.setUsers(new ArrayList<>(Arrays.asList(new User())));

        firstEvent = new Event();
        firstEvent.setId(1L);
        firstEvent.setAttendees(List.of(user));

        secondEvent = new Event();
        secondEvent.setId(2L);
        secondEvent.setAttendees(List.of(user));
    }

    @Test
    public void testDeactivateUser_UserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        NoSuchElementException exception = assertThrows(NoSuchElementException.class,
                () -> userService.deactivateUser(userDto));
        assertEquals("Пользователь с ID 1 не найден", exception.getMessage());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    public void testDeactivateUser_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(userDto);
        UserDto result = userService.deactivateUser(userDto);
        assertNotNull(result);
        assertFalse(user.isActive());
        verify(mentorshipService, times(1)).stopMentorship(user);
        verify(userRepository, times(1)).save(user);
        verify(userMapper, times(1)).toDto(user);
    }

    @Test
    public void testStopUserActivities() {
        user.setGoals(List.of(firstGoal, secondGoal));
        user.setOwnedEvents(List.of(firstEvent));
        user.setParticipatedEvents(List.of(secondEvent));
        userService.stopUserActivities(user);
        verify(eventRepository, times(1)).deleteAllById(any());
    }

    @Test
    public void testStopGoals_OnlyUserInGoal() {
        Goal goal = new Goal();
        goal.setId(1L);
        goal.setUsers(Collections.singletonList(user));
        user.setGoals(new ArrayList<>(Collections.singletonList(goal)));
        userService.stopGoals(user);
        verify(goalRepository, times(1)).deleteById(1L);
        assertTrue(user.getGoals().isEmpty());
    }

    @Test
    public void testStopGoals_OtherUsersInGoal() {
        firstGoal.getUsers().add(new User());
        user.setGoals(List.of(firstGoal));
        userService.stopGoals(user);
        verify(goalRepository, never()).deleteById(anyLong());
        assertTrue(user.getGoals().isEmpty());
    }

    @Test
    public void testStopEvents() {
        user.setOwnedEvents(List.of(firstEvent));
        user.setParticipatedEvents(List.of(secondEvent));
        userService.stopEvents(user);
        assertTrue(user.getOwnedEvents().isEmpty());
        assertTrue(user.getParticipatedEvents().isEmpty());
        verify(eventRepository, times(1)).deleteAllById(any());
    }

    @Test
    void testGetPremiumUsers_positiveWithoutFilters() {
        User user1 = new User();
        User user2 = new User();
        User user3 = new User();
        Mockito.when(userRepository.findPremiumUsers()).thenReturn(Stream.of(user1, user2, user3));

        UserFilterDto filterDto = new UserFilterDto();
        List<UserDto> users = userService.getPremiumUsers(filterDto);

        assertEquals(3, users.size());
    }

    @Test
    void testGetPremiumUsers_positiveWithFilters() {
        User user1 = new User();
        user1.setUsername("user1");
        user1.setPhone("123123");
        user1.setCreatedAt(LocalDateTime.now());
        User user2 = new User();
        user2.setUsername("user2");
        user2.setPhone("111111");
        user2.setCreatedAt(LocalDateTime.now());
        User user3 = new User();
        user3.setUsername("user3");
        user3.setPhone("222222");
        user3.setCreatedAt(LocalDateTime.now().minusMonths(2));
        Mockito.when(userRepository.findPremiumUsers()).thenReturn(Stream.of(user1, user2, user3));

        List<UserFilter> filters = new ArrayList<>();
        filters.add(new UserNameFilter());
        filters.add(new UserPhoneFilter());
        filters.add(new UserCreatedAfterFilter());
        filters.add(new UserCreatedBeforeFilter());
        Mockito.when(userFilters.stream()).thenReturn(filters.stream());

        UserFilterDto filterDto = new UserFilterDto();
        filterDto.setNamePattern("user");
        filterDto.setPhone("123123");
        filterDto.setCreatedAfter(LocalDateTime.now().minusMonths(1));
        filterDto.setCreatedBefore(LocalDateTime.now().plusMonths(1));
        List<UserDto> users = userService.getPremiumUsers(filterDto);

        assertEquals(1, users.size());
    }

    @Test
    @DisplayName("Should return a certain user when user exists by id")
    public void testGetUser_Success() {
        userDto.setActive(true);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(userDto);

        UserDto resultDto = userService.getUser(user.getId());

        assertAll(
                () -> assertNotNull(resultDto),
                () -> assertEquals(1L, resultDto.getId()),
                () -> assertTrue(resultDto.isActive())
        );
        verify(userRepository, times(1)).findById(anyLong());
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when user is not found by id")
    public void testGetUser_UserNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.getUser(42L));

        verify(userRepository, times(1)).findById(anyLong());
        verifyNoInteractions(userMapper);
    }

    @Test
    @DisplayName("Should return list of certain users when users exist by id")
    public void testGetUsersByIds_Success() {
        List<Long> ids = Arrays.asList(1L, 2L);
        User anotherUser = new User();
        UserDto anotherUserDto = new UserDto();

        List<UserDto> dtos = Arrays.asList(userDto, anotherUserDto);
        List<User> users = Arrays.asList(user, anotherUser);

        when(userRepository.findAllById(anyList())).thenReturn(users);
        when(userMapper.toListUserDto(users)).thenReturn(dtos);

        List<UserDto> resultDtoList = userService.getUsersByIds(ids);

        assertAll(
                () -> assertEquals(2, resultDtoList.size()),
                () -> assertTrue(resultDtoList.contains(userDto)),
                () -> assertTrue(resultDtoList.contains(anotherUserDto))
        );
        verify(userRepository, times(1)).findAllById(anyList());
    }

    @Test
    @DisplayName("Should return an empty list when ids list is empty")
    public void testGetUsersByIds_EmptyIdsList() {
        List<UserDto> result = userService.getUsersByIds(Collections.emptyList());

        assertAll(
                () -> assertNotNull(result),
                () -> assertTrue(result.isEmpty())
        );
        verify(userRepository, times(1)).findAllById(anyList());
    }

    @Test
    @DisplayName("Should return users in correct order when users are found and filtered")
    public void testGetFilteredUsers_FoundAndFiltered() {
        Country usa = new Country(1, "USA", List.of());
        Country uk = new Country(2, "UK", List.of());

        User callingUser = new User();
        callingUser.setId(1L);
        callingUser.setUsername("John Doe");
        callingUser.setCountry(usa);

        User promoted1 = new User();
        promoted1.setId(2L);
        promoted1.setUsername("John Smith");
        promoted1.setCountry(uk);

        Promotion promotion1_1 = new Promotion();
        promotion1_1.setPromotionTarget("profile");
        promotion1_1.setRemainingShows(5);
        promotion1_1.setPriorityLevel(3);

        Promotion promotion1_2 = new Promotion();
        promotion1_2.setPromotionTarget("event");
        promotion1_2.setRemainingShows(2);
        promotion1_2.setPriorityLevel(1);

        promoted1.setPromotions(new ArrayList<>(List.of(promotion1_1, promotion1_2)));

        User promoted2 = new User();
        promoted2.setId(3L);
        promoted2.setUsername("John Smith");
        promoted2.setCountry(usa);
        promoted2.setPromotions(new ArrayList<>());

        Promotion promotion2_1 = new Promotion();
        promotion2_1.setPromotionTarget("profile");
        promotion2_1.setRemainingShows(5);
        promotion2_1.setPriorityLevel(3);

        Promotion promotion2_2 = new Promotion();
        promotion2_2.setPromotionTarget("event");
        promotion2_2.setRemainingShows(3);
        promotion2_2.setPriorityLevel(2);

        promoted2.setPromotions(new ArrayList<>(List.of(promotion2_1, promotion2_2)));

        List<UserFilter> filters = new ArrayList<>();
        filters.add(new UserNameFilter());

        UserFilterDto filterDto = new UserFilterDto();
        filterDto.setNamePattern("John");

        List<User> filteredUsers = List.of(callingUser, promoted1, promoted2);

        when(userFilters.stream()).thenReturn(filters.stream());
        when(userRepository.findById(callingUser.getId())).thenReturn(Optional.of(callingUser));
        when(userRepository.findAll(ArgumentMatchers.<Specification<User>>any())).thenReturn(filteredUsers);

        List<UserDto> result = userService.getFilteredUsers(filterDto, callingUser.getId());

        verify(userMapper, times(1)).toDto(callingUser);
        verify(userMapper, times(2)).toDto(promoted1);
        verify(userMapper, times(2)).toDto(promoted2);
        verify(searchAppearanceEventPublisher, times(3)).publish(any());

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(3, result.size()),
                () -> assertEquals(3L, result.get(0).getId()),
                () -> assertEquals(2L, result.get(1).getId()),
                () -> assertEquals(1L, result.get(2).getId())
        );
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when calling user is not found")
    public void testGetFilteredUsers_CallingUserNotFound() {
        UserFilterDto filterDto = new UserFilterDto();

        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> userService.getFilteredUsers(filterDto, 1L));
    }

    @Test
    void testSaveAvatar_whenUserProfilePicExists_shouldReplaceAvatar() {
        long userId = 1L;
        MultipartFile file = mock(MultipartFile.class);
        UserProfilePic existingProfilePic = new UserProfilePic("existing-large.jpg", "existing-small.jpg");

        when(userRepository.findUserProfilePicByUserId(userId)).thenReturn(existingProfilePic);
        UserProfilePic newProfilePic = new UserProfilePic("new-large.jpg", "new-small.jpg");
        when(s3service.uploadFile(file, userId)).thenReturn(newProfilePic);

        userService.saveAvatar(userId, file);

        verify(s3service).deleteFile("existing-large.jpg");
        verify(s3service).deleteFile("existing-small.jpg");
        verify(userRepository).deleteUserProfilePicByUserId(userId);
        verify(s3service).uploadFile(file, userId);
        verify(userRepository).saveUserProfilePic(userId, newProfilePic);
    }

    @Test
    void testSaveAvatar_whenUserProfilePicDoesNotExist_shouldUploadNewAvatar() {
        long userId = 1L;
        MultipartFile file = mock(MultipartFile.class);

        when(userRepository.findUserProfilePicByUserId(userId)).thenReturn(null);
        UserProfilePic newProfilePic = new UserProfilePic("new-large.jpg", "new-small.jpg");
        when(s3service.uploadFile(file, userId)).thenReturn(newProfilePic);

        userService.saveAvatar(userId, file);

        verify(s3service, never()).deleteFile(anyString());
        verify(userRepository, never()).deleteUserProfilePicByUserId(anyLong());
        verify(s3service).uploadFile(file, userId);
        verify(userRepository).saveUserProfilePic(userId, newProfilePic);
    }

    @Test
    void testGetAvatar_whenAvatarExists_shouldReturnAvatarBytes() throws Exception {
        long userId = 1L;
        UserProfilePic profilePic = new UserProfilePic("large.jpg", "small.jpg");
        when(userRepository.findUserProfilePicByUserId(userId)).thenReturn(profilePic);

        byte[] avatarBytes = "avatar".getBytes();
        InputStream avatarStream = new ByteArrayInputStream(avatarBytes);
        when(s3service.downloadFile("large.jpg")).thenReturn(avatarStream);

        byte[] result = userService.getAvatar(userId);

        verify(s3service).downloadFile("large.jpg");
        Assertions.assertArrayEquals(avatarBytes, result);
    }

    @Test
    void testGetAvatar_whenAvatarDoesNotExist_shouldThrowException() {
        long userId = 1L;
        when(userRepository.findUserProfilePicByUserId(userId)).thenReturn(null);

        assertThrows(EntityNotFoundException.class, () -> userService.getAvatar(userId));

        verify(s3service, never()).downloadFile(anyString());
    }

    @Test
    void testDeleteAvatar_whenAvatarExists_shouldDeleteAvatar() {
        long userId = 1L;
        UserProfilePic profilePic = new UserProfilePic("large.jpg", "small.jpg");
        when(userRepository.findUserProfilePicByUserId(userId)).thenReturn(profilePic);

        userService.deleteAvatar(userId);

        verify(s3service).deleteFile("large.jpg");
        verify(s3service).deleteFile("small.jpg");
        verify(userRepository).deleteUserProfilePicByUserId(userId);
    }

    @Test
    void testDeleteAvatar_whenAvatarDoesNotExist_shouldThrowException() {
        long userId = 1L;
        when(userRepository.findUserProfilePicByUserId(userId)).thenReturn(null);

        assertThrows(EntityNotFoundException.class, () -> userService.deleteAvatar(userId));

        verify(s3service, never()).deleteFile(anyString());
        verify(userRepository, never()).deleteUserProfilePicByUserId(anyLong());
    }

    @Test
    public void testUpdateTelegramUserId_Success() {
        String telegramUserName = "testTelegramUserName";
        String telegramUserId = "123456789";

        TelegramContact telegramContact = new TelegramContact();
        telegramContact.setTelegramUserName(telegramUserName);
        telegramContact.setTelegramUserId(null); // Предположим, что поле ещё не заполнено

        when(telegramContactRepository.findByTelegramUserName(telegramUserName)).thenReturn(Optional.of(telegramContact));

        userService.updateTelegramUserId(telegramUserName, telegramUserId);

        verify(telegramContactRepository, times(1)).save(telegramContact);
        verify(telegramContactRepository).save(telegramContact);
        verify(telegramContactRepository, times(1)).findByTelegramUserName(telegramUserName);
    }

    @Test
    public void testUpdateTelegramUserId_UserNotFound() {
        String telegramUserName = "nonExistingTelegramUserName";
        String telegramUserId = "123456789";

        when(telegramContactRepository.findByTelegramUserName(telegramUserName)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            userService.updateTelegramUserId(telegramUserName, telegramUserId);
        });

        verify(telegramContactRepository, times(1)).findByTelegramUserName(telegramUserName);
        verify(telegramContactRepository, never()).save(any(TelegramContact.class));
    }

    @Test
    public void testUpdateTelegramUserId_IdAlreadyExists() {
        String telegramUserName = "testTelegramUserName";
        String existingTelegramUserId = "123456789";
        String newTelegramUserId = "987654321";

        TelegramContact telegramContact = new TelegramContact();
        telegramContact.setTelegramUserName(telegramUserName);
        telegramContact.setTelegramUserId(existingTelegramUserId);

        when(telegramContactRepository.findByTelegramUserName(telegramUserName)).thenReturn(Optional.of(telegramContact));

        userService.updateTelegramUserId(telegramUserName, newTelegramUserId);

        verify(telegramContactRepository, never()).save(telegramContact);
        verify(telegramContactRepository, times(1)).findByTelegramUserName(telegramUserName);
    }
    @Test
    @DisplayName("Should publish ProfileViewEvent when viewer id is different from profile owner id")
    public void testPublishProfileViewEvent_Success() {
        viewerId = 1L;
        profileOwnerId = 2L;

        userService.publishProfileViewEvent(viewerId, profileOwnerId);

        verify(profileViewEventPublisher, times(1)).publish(any(ProfileViewEvent.class));
    }

    @Test
    @DisplayName("Should not publish ProfileViewEvent when viewer id is same as profile owner id")
    public void testPublishProfileViewEvent_SameViewerAndProfileOwnerIds() {
        long viewerId = 1L;
        long profileOwnerId = 1L;

        userService.publishProfileViewEvent(viewerId, profileOwnerId);

        verify(profileViewEventPublisher, never()).publish(any(ProfileViewEvent.class));
    }
}
