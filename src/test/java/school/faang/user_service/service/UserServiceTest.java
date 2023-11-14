package school.faang.user_service.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.contact.ContactDto;
import school.faang.user_service.dto.contact.ContactPreferenceDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.contact.ContactType;
import school.faang.user_service.entity.contact.PreferredContact;
import school.faang.user_service.mapper.UserMapperImpl;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.contact.ContactPreferenceService;
import school.faang.user_service.service.contact.ContactService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private ContactService contactService;
    @Mock
    private ContactPreferenceService contactPreferenceService;
    @InjectMocks
    private UserService userService;
    @Spy
    private UserMapperImpl userMapper;

    private final List<Long> USERS_IDS = List.of(1L, 2L, 3L);
    private long USER_ID = 1L;
    private User user;
    private UserDto userDto;

    @Test
    void testGetUsersByIds() {
        when(userRepository.findAllById(USERS_IDS)).thenReturn(getListOfUser());
        List<UserDto> actualLust = userService.getUsersByIds(USERS_IDS);
        List<UserDto> expectedList = getCorrectListOfUserDto();

        assertEquals(expectedList, actualLust);
    }

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(USER_ID)
                .username("Nikita")
                .build();
        userDto = UserDto.builder()
                .id(USER_ID)
                .username("Nikita")
                .followeeIds(new ArrayList<>())
                .followerIds(new ArrayList<>())
                .build();
    }

    @Test
    void getUserWithoutUserInDataBase() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> userService.getUser(USER_ID));
    }

    private List<User> getListOfUser() {
        return List.of(User.builder().id(1L).build(),
                User.builder().id(2L).build(),
                User.builder().id(3L).build());
    }

    private List<UserDto> getCorrectListOfUserDto() {
        return List.of(UserDto.builder()
                        .id(1L)
                        .followeeIds(new ArrayList<>())
                        .followerIds(new ArrayList<>())
                        .build(),
                UserDto.builder()
                        .id(2L)
                        .followeeIds(new ArrayList<>())
                        .followerIds(new ArrayList<>())
                        .build(),
                UserDto.builder()
                        .id(3L)
                        .followeeIds(new ArrayList<>())
                        .followerIds(new ArrayList<>())
                        .build());
    }

    @Test
    void getUser() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.ofNullable(user));
        UserDto actualUser = userService.getUser(USER_ID);
        assertEquals(userDto, actualUser);
    }

    @Test
    void testSaveTelegramChatId() {
        long chatId = 1L;
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        ContactPreferenceDto contactPreference = ContactPreferenceDto.builder()
                .user(user)
                .preference(PreferredContact.TELEGRAM)
                .build();
        ContactDto contact = ContactDto.builder()
                .user(user)
                .contact(String.valueOf(chatId))
                .type(ContactType.TELEGRAM)
                .build();

        userService.saveTelegramChatId(USER_ID, chatId);
        verify(contactService).save(contact);
        verify(contactPreferenceService).save(contactPreference);
    }
}