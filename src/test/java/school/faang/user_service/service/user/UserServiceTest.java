package school.faang.user_service.service.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.dto.user_jira.UserJiraCreateUpdateDto;
import school.faang.user_service.dto.user_jira.UserJiraDto;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.entity.userJira.UserJira;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.exception.ErrorMessage;
import school.faang.user_service.filter.user.UserEmailFilter;
import school.faang.user_service.filter.user.UserFilter;
import school.faang.user_service.filter.user.UserNameFilter;
import school.faang.user_service.mapper.user.UserMapper;
import school.faang.user_service.mapper.user.UserMapperImpl;
import school.faang.user_service.mapper.user_jira.UserJiraMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.user_jira.UserJiraService;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class})
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Spy
    private UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    @Spy
    private UserJiraMapper userJiraMapper = Mappers.getMapper(UserJiraMapper.class);

    @Mock
    private UserJiraService userJiraService;

    @Mock
    private CountryService countryService;

    @Mock
    private UserService userService;

    private List<UserFilter> userFilters;

    @Captor
    ArgumentCaptor<User> userCaptor;

    @Test
    void getUserTest() {
        long userId = 1L;
        User user = User.builder()
                .id(userId)
                .username("username")
                .build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        UserDto result = userService.getUser(userId);

        assertNotNull(result);
        assertEquals(user.getUsername(), result.getUsername());
    }

    @BeforeEach
    void setUp() {
        UserFilter userEmailFilter = mock(UserEmailFilter.class);
        UserFilter userNameFilter = mock(UserNameFilter.class);
        userFilters = new ArrayList<>(List.of(userEmailFilter, userNameFilter));
        userMapper = new UserMapperImpl();

        userService = new UserService(userRepository, userFilters, userMapper, userJiraMapper, userJiraService, countryService);
    }

    @Test
    void getUserNotFoundTest() {
        long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class, () -> userService.getUser(userId)
        );
        assertEquals(String.format(ErrorMessage.USER_NOT_FOUND, userId), exception.getMessage());
    }

    @Test
    void getUserEntityTest() {
        long userId = 1L;
        User user = User.builder()
                .id(userId)
                .username("username")
                .build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        User result = userService.getUserEntity(userId);

        assertNotNull(result);
        assertEquals(user.getUsername(), result.getUsername());
    }

    @Test
    void getUserEntityNotFoundTest() {
        long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class, () -> userService.getUserEntity(userId)
        );
        assertEquals(String.format(ErrorMessage.USER_NOT_FOUND, userId), exception.getMessage());
    }

    @Test
    void getUsersIdsTest() {
        List<Long> userIds = List.of(1L, 2L);
        User user1 = User.builder().id(1L).build();
        User user2 = User.builder().id(2L).build();
        when(userRepository.findAllById(userIds)).thenReturn(List.of(user1, user2));

        List<UserDto> result = userService.getUsers(userIds);

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void getUsersNotFoundTest() {
        List<Long> userIds = List.of(1L, 2L);
        when(userRepository.findAllById(userIds)).thenReturn(Collections.emptyList());

        List<UserDto> result = userService.getUsers(userIds);

        assertTrue(result.isEmpty());
    }

    @Test
    void existsByIdTrueTest() {
        long userId = 1L;
        when(userRepository.existsById(userId)).thenReturn(true);

        assertTrue(userService.existsById(userId));
        verify(userRepository, times(1)).existsById(userId);
    }

    @Test
    void existsByIdFalseTest() {
        long userId = 1L;
        when(userRepository.existsById(userId)).thenReturn(false);

        assertFalse(userService.existsById(userId));
        verify(userRepository, times(1)).existsById(userId);
    }

    @Test
    void saveUserTest() {
        User user = new User();
        userService.saveUser(user);

        verify(userRepository, times(1)).save(user);
    }

    @Test
    void getNotExistingUsersEmptyListTest() {
        List<Long> userIds = Collections.emptyList();
        List<Long> notExistingUserIds = userService.getNotExistingUserIds(userIds);

        assertTrue(notExistingUserIds.isEmpty());
    }

    @Test
    void getNotExistingUsersValidListTest() {
        List<Long> userIds = List.of(1L, 2L, 3L);
        when(userRepository.findNotExistingUserIds(userIds)).thenReturn(List.of(1L));

        List<Long> notExistingUserIds = userService.getNotExistingUserIds(userIds);

        assertEquals(1, notExistingUserIds.size());
        assertTrue(notExistingUserIds.contains(1L));
    }

    @Test
    void saveOrUpdateUserJiraInfoTest() {
        long userId = 1L;
        User user = new User();
        user.setId(userId);
        String jiraDomain = "itmad1x";
        String jiraEmail = "mad1x@example.com";
        String jiraAccountId = "oiu1293oi5yuh1i2u5yh1i92u5h1";
        String jiraToken = "921804vk019248v10928v40129v8412908v412980v4218-90v41204";
        UserJiraCreateUpdateDto createUpdateDto = UserJiraCreateUpdateDto.builder()
                .jiraEmail(jiraEmail)
                .jiraAccountId(jiraAccountId)
                .jiraToken(jiraToken)
                .build();
        UserJira userJira = userJiraMapper.toEntity(createUpdateDto);
        userJira.setJiraDomain(jiraDomain);
        userJira.setUser(user);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userJiraService.saveOrUpdate(userJira)).thenReturn(userJira);

        UserJiraDto responseDto = assertDoesNotThrow(() -> userService.saveOrUpdateUserJiraInfo(userId, jiraDomain, createUpdateDto));

        verify(userRepository, times(1)).findById(userId);
        verify(userJiraService, times(1)).saveOrUpdate(userJira);

        assertEquals(userId, responseDto.getUserId());
        assertEquals(jiraDomain, responseDto.getJiraDomain());
        assertEquals(jiraEmail, responseDto.getJiraEmail());
        assertEquals(jiraAccountId, responseDto.getJiraAccountId());
        assertEquals(jiraToken, responseDto.getJiraToken());
    }

    @Test
    void getUserJiraInfoTest() {
        long userId = 1L;
        String jiraDomain = "itmad1x";
        when(userJiraService.getByUserIdAndJiraDomain(userId, jiraDomain)).thenReturn(new UserJira());

        assertDoesNotThrow(() -> userService.getUserJiraInfo(userId, jiraDomain));

        verify(userJiraService, times(1)).getByUserIdAndJiraDomain(userId, jiraDomain);
    }

    @Test
    void getPremiumUsersTest() {
        long firstUserId = 1L;
        long secondUserId = 2L;

        User firstUser = User.builder()
                .id(firstUserId)
                .username("firstUser")
                .email("first@email.com")
                .telegramChatId(1242142141241L)
                .build();

        User secondUser = User.builder()
                .id(secondUserId)
                .username("secondUser")
                .email("second@email.com")
                .telegramChatId(90218421908421L)
                .build();

        UserDto firstUserDto = new UserDto(firstUserId, "firstUser", "first@email.com", 1242142141241L, false);
        UserDto secondUserDto = new UserDto(secondUserId, "secondUser", "second@email.com", 90218421908421L, false);

        Stream<User> users = Stream.of(firstUser, secondUser);
        List<UserDto> expectedUsersDto = List.of(firstUserDto, secondUserDto);
        UserFilterDto filterDto = new UserFilterDto();

        when(userRepository.findPremiumUsers()).thenReturn(users);
        when(userFilters.get(0).isApplicable(filterDto)).thenReturn(true);
        when(userFilters.get(0).apply(users, filterDto)).thenReturn(users);
        when(userFilters.get(1).isApplicable(filterDto)).thenReturn(false);

        List<UserDto> actualUsersDto = userService.getPremiumUsers(filterDto);

        verify(userRepository).findPremiumUsers();
        verify(userFilters.get(0)).isApplicable(filterDto);
        verify(userFilters.get(1)).isApplicable(filterDto);
        verify(userFilters.get(0)).apply(users, filterDto);

        assertEquals(expectedUsersDto, actualUsersDto);
    }

    @Test
    void getNotPremiumUsersTest() {
        long firstUserId = 1L;
        long secondUserId = 2L;

        Premium expiredPremium = new Premium();
        expiredPremium.setEndDate(LocalDateTime.now().minusDays(1));

        User firstUser = User.builder()
                .id(firstUserId)
                .username("firstUser")
                .email("first@email.com")
                .premium(expiredPremium)
                .telegramChatId(90182590L)
                .build();

        User secondUser = User.builder()
                .id(secondUserId)
                .username("secondUser")
                .email("second@email.com")
                .telegramChatId(893248953L)
                .build();

        UserDto firstUserDto = new UserDto(firstUserId, "firstUser", "first@email.com", 90182590L, false);
        UserDto secondUserDto = new UserDto(secondUserId, "secondUser", "second@email.com", 893248953L, false);

        List<UserDto> expectedUsersDto = List.of(firstUserDto, secondUserDto);
        List<User> usersList = List.of(firstUser, secondUser);
        UserFilterDto filterDto = new UserFilterDto();

        when(userRepository.findAll()).thenReturn(usersList);
        when(userFilters.get(0).isApplicable(filterDto)).thenReturn(true);
        when(userFilters.get(0).apply(any(), eq(filterDto)))
                .thenAnswer(invocation -> invocation.<Stream<User>>getArgument(0));
        when(userFilters.get(1).isApplicable(filterDto)).thenReturn(false);

        List<UserDto> actualUsersDto = userService.getNotPremiumUsers(filterDto);

        verify(userFilters.get(0), times(1)).isApplicable(filterDto);
        verify(userFilters.get(1), times(1)).isApplicable(filterDto);
        verify(userFilters.get(0), times(1)).apply(any(), eq(filterDto));

        assertEquals(expectedUsersDto, actualUsersDto);
    }

    @Test
    public void usersArePremiumShouldReturnEmptyListTest() {
        Premium premium = new Premium();
        premium.setEndDate(LocalDateTime.now().plusDays(30));

        User user = new User();
        user.setPremium(premium);
        User user2 = new User();
        user2.setPremium(premium);

        List<User> users = new ArrayList<>(List.of(user, user2));
        UserFilterDto filterDto = new UserFilterDto();

        when(userRepository.findAll()).thenReturn(users);

        List<UserDto> actualUsers = userService.getNotPremiumUsers(filterDto);

        assertEquals(new ArrayList<>(), actualUsers);
    }

    @Test
    public void banUserTest() {
        User user = new User();
        user.setId(1L);
        user.setBanned(false);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        userService.banUser(user.getId());

        verify(userRepository, times(1)).save(userCaptor.capture());
        assertTrue(userCaptor.getValue().isBanned());
    }

    @Test
    void parsePersonDataIntoUserDto() throws IOException {
        String csvData = """
            firstName,lastName,yearOfBirth,group,studentID,email,phone,street,city,state,country,postalCode,faculty,yearOfStudy,major,GPA,status,admissionDate,graduationDate,degree,institution,completionYear,scholarship,employer
            John,Doe,1998,A,123456,johndoe@example.com,+1-123-456-7890,123 Main Street,New York,NY,USA,10001,Computer Science,3,Software Engineering,3.8,Active,2016-09-01,2020-05-30,High School Diploma,XYZ High School,2016,true,XYZ Technologies
            """;

        MultipartFile csvFile = mock(MultipartFile.class);
        when(csvFile.getInputStream()).thenReturn(new ByteArrayInputStream(csvData.getBytes()));
        when(csvFile.getContentType()).thenReturn("text/csv");
        when(csvFile.isEmpty()).thenReturn(false);

        User user1 = new User();
        user1.setEmail("johndoe@example.com");
        user1.setPhone("+1-123-456-7890");
        List<User> savedUsers = List.of(user1);

        Country country = new Country();
        country.setTitle("USA");

        when(userRepository.saveAll(Mockito.anyList())).thenReturn(savedUsers);
        when(countryService.getOrCreateCountry("USA")).thenReturn(country);

        List<UserDto> result = userService.parsePersonDataIntoUserDto(csvFile);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("johndoe@example.com", result.get(0).getEmail());

        verify(countryService, times(1)).getOrCreateCountry("USA");
        verify(userRepository, times(1)).saveAll(Mockito.anyList());
    }

    @Test
    void parsePersonDataIntoUserDtoEmptyFile()  {
        MockMultipartFile emptyFile = new MockMultipartFile("file", "file.csv",
                "text/csv", new byte[0]);
        assertThrows(IllegalArgumentException.class, () -> userService.parsePersonDataIntoUserDto(emptyFile));
    }

    @Test
    void parsePersonDataIntoUserDtoInvalidFileType()  {
        MockMultipartFile invalidTypeFile = new MockMultipartFile("file", "file.txt",
                "text/plain", "Some content".getBytes());
        assertThrows(IllegalArgumentException.class, () -> userService.parsePersonDataIntoUserDto(invalidTypeFile));
    }


}