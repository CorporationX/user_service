package school.faang.user_service.service;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.json.student.Person;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.dto.UserProfilePicDto;
import school.faang.user_service.dto.event.ProfileViewEventDto;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.mapper.PersonToUserMapper;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.mapper.image.ImageMapper;
import school.faang.user_service.publisher.ProfileViewMessagePublisher;
import school.faang.user_service.repository.CountryRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.s3.S3ServiceImpl;
import school.faang.user_service.util.TestDataFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import static java.lang.Long.MAX_VALUE;
import static java.util.List.of;
import static java.util.Optional.empty;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyInt;
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
    private CsvMapper csvMapper;
    @Mock
    private CountryRepository countryRepository;
    @Mock
    private PersonToUserMapper personToUserMapper;
    @Mock
    private ObjectReader objectReader;
    @Mock
    private MappingIterator<Person> mappingIterator;
    @Mock
    private EventRepository eventRepository;
    @Mock
    private S3ServiceImpl s3Service;
    @Mock
    private ImageMapper imageMapper;
    @Mock
    private ProfileViewMessagePublisher profileViewMessagePublisher;

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
    void givenPersonsInputStreamWhenSavePersonsThenReturnUsers() throws IOException, ExecutionException, InterruptedException {
        // given - precondition
        String csvData = "firstName,lastName,email,country\nJohn,Smith,incognito1@gmail.com,USA";
        InputStream inputStream = new ByteArrayInputStream(csvData.getBytes(StandardCharsets.UTF_8));
        User user = TestDataFactory.createUser();
        UserDto userDto = TestDataFactory.createUserDto();
        List<Country> countryList = of(TestDataFactory.createCounty());
        List<User> userList = of(user);
        List<UserDto> expectedResult = of(userDto);

        when(countryRepository.findAll()).thenReturn(Collections.emptyList());
        when(countryRepository.saveAll(anyList())).thenReturn(countryList);
        when(userRepository.saveAll(anyList())).thenReturn(userList);
        when(personToUserMapper.mapToUser(any(Person.class))).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(userDto);

        // when - action
        var actualResult = userService.saveStudents(inputStream).get();

        // then - verify the output
        assertThat(actualResult).isNotNull();
        assertThat(actualResult.size()).isEqualTo(expectedResult.size());
        assertThat(actualResult.get(0).getEmail()).isEqualTo(expectedResult.get(0).getEmail());
        assertThat(actualResult.get(0).getUsername()).isEqualTo(expectedResult.get(0).getUsername());
    }

    @Test
    void givenEmptyInputStreamWhenSavePersonsThenThrowException() {
        // given - precondition
        var emptyInputStream = InputStream.nullInputStream();

        // when - action and
        // then - verify the output
        assertThatThrownBy(() -> userService.saveStudents(emptyInputStream))
                .isInstanceOf(IllegalArgumentException.class);
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

    @Test
    public void addUserPic_successfulTest() throws IOException {
        // Arrange
        var user = TestDataFactory.createUser();
        MultipartFile mockMultipartFile = mock(MultipartFile.class);

        // When
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(imageMapper.convertFilePermissions(any(), anyInt())).thenReturn(mockMultipartFile);


        // Act
        UserProfilePicDto userProfilePicDto = userService.addUserPic(user.getId(), mockMultipartFile);

        // Assert
        assertNotNull(userProfilePicDto);

        // Verify
        verify(userRepository).save(user);
        verify(s3Service, times(2)).uploadFile(mockMultipartFile);
    }

    @Test
    public void getUserProfilePic_successfulTest() {
        // Arrange
        var user = TestDataFactory.createUser();
        UserProfilePic userProfilePic = new UserProfilePic();
        userProfilePic.setFileId("field");
        user.setUserProfilePic(userProfilePic);

        // When
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        // Act
        userService.getUserPic(user.getId());

        // Verify
        verify(s3Service).downloadFile(user.getUserProfilePic().getFileId());
    }

    @Test
    public void deleteUserPic_successfulTest() {
        // Arrange
        var user = TestDataFactory.createUser();
        UserProfilePic userProfilePic = new UserProfilePic();
        userProfilePic.setFileId("field");
        userProfilePic.setSmallFileId("small");
        user.setUserProfilePic(userProfilePic);

        //when
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        // Act
        userService.deleteUserPic(user.getId());

        // Verify
        verify(s3Service, times(2)).deleteFile(any());
        verify(userRepository).save(user);
    }

    @Test
    public void getUserPicById_throwTest() {
        // Arrange
        var user = TestDataFactory.createUser();

        // Assert
        assertThrows(EntityNotFoundException.class, () -> userService.getUserPic(user.getId()));
    }

    @Test
    public void deleteUserPicById_throwTest() {
        var user = TestDataFactory.createUser();
        user.setId(0L);

        // Assert
        assertThrows(EntityNotFoundException.class, () -> userService.getUserPic(user.getId()));
    }

    @Test
    public void getUserProfile_successfulTest() {
        // Arrange
        var user = TestDataFactory.createUser();
        var userDto = TestDataFactory.createUserDto();
        var viewer = 2L;

        ProfileViewEventDto profileViewEventDto = new ProfileViewEventDto();
        profileViewEventDto.setViewerId(viewer);
        profileViewEventDto.setAuthorId(USER_ID);

        // When
        when(userRepository.findById(USER_ID))
                .thenReturn(Optional.of(user));
        when(userMapper.toDto(user))
                .thenReturn(userDto);

        // Action
        var actualResult = userService.getUserProfile(profileViewEventDto);

        // Assert
        assertThat(actualResult).isNotNull();
        assertThat(actualResult.getId()).isEqualTo(userDto.getId());

        verify(userRepository).findById(USER_ID);
        verify(userMapper).toDto(user);
        verify(profileViewMessagePublisher).publish(profileViewEventDto);
    }

    @Test
    public void getUserProfile_throwTest() {
        // Arrange
        ProfileViewEventDto profileViewEventDto = new ProfileViewEventDto();
        profileViewEventDto.setViewerId(USER_ID);
        profileViewEventDto.setAuthorId(USER_ID);

        // Assert
        assertThrows(IllegalArgumentException.class, () -> userService.getUserProfile(profileViewEventDto));

        // Verify
        verify(profileViewMessagePublisher, times(0)).publish(profileViewEventDto);
    }
}