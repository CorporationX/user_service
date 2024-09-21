package school.faang.user_service.mapper.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserRegistrationDto;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
class UserMapperTest {

    private static final long USER_ID_ONE = 1L;
    private static final long USER_ID_TWO = 2L;
    private static final long COUNTRY_ID_ONE = 1L;

    private static final int SIZE_USER_DTO_LIST = 2;

    private static final String PICTURE_ID = "1";

    @InjectMocks
    private UserMapperImpl userMapper;

    @Test
    @DisplayName("Convert userRegistration dto should be correctly converted to User entity")
    void whenUserRegistrationDtoConvertedToEntityThenReturnCorrectEntity() {
        UserRegistrationDto userRegistrationDto = UserRegistrationDto.builder()
                .username("test")
                .email("test@test.test")
                .phone("123")
                .password("test")
                .aboutMe("test")
                .countryId(COUNTRY_ID_ONE)
                .city("test")
                .build();

        User expectedUser = User.builder()
                .username("test")
                .email("test@test.test")
                .phone("123")
                .password("test")
                .aboutMe("test")
                .country(Country.builder()
                        .id(COUNTRY_ID_ONE)
                        .build())
                .city("test")
                .build();

        User user = userMapper.toEntity(userRegistrationDto);

        assertEquals(expectedUser.getUsername(), user.getUsername());
        assertEquals(expectedUser.getEmail(), user.getEmail());
        assertEquals(expectedUser.getPhone(), user.getPhone());
        assertEquals(expectedUser.getPassword(), user.getPassword());
        assertEquals(expectedUser.getAboutMe(), user.getAboutMe());
        assertEquals(expectedUser.getCity(), user.getCity());
        assertEquals(expectedUser.getCountry().getId(), user.getCountry().getId());
    }

    @Test
    @DisplayName("Convert User entity should be correctly converted to User dto")
    void whenUserEntityConvertedToUserDtoThenReturnCorrectUserDto() {
        User user = User.builder()
                .username("test")
                .email("test@test.test")
                .phone("123")
                .password("test")
                .aboutMe("test")
                .country(Country.builder()
                        .id(COUNTRY_ID_ONE)
                        .build())
                .city("test")
                .userProfilePic(UserProfilePic.builder()
                        .fileId(PICTURE_ID)
                        .build())
                .build();

        UserDto expectedUserDto = UserDto.builder()
                .username("test")
                .email("test@test.test")
                .phone("123")
                .aboutMe("test")
                .countryId(COUNTRY_ID_ONE)
                .city("test")
                .userProfilePicId(PICTURE_ID)
                .build();

        UserDto userDto = userMapper.toDto(user);

        assertEquals(expectedUserDto.getUsername(), userDto.getUsername());
        assertEquals(expectedUserDto.getEmail(), userDto.getEmail());
        assertEquals(expectedUserDto.getPhone(), userDto.getPhone());
        assertEquals(expectedUserDto.getAboutMe(), userDto.getAboutMe());
        assertEquals(expectedUserDto.getCity(), userDto.getCity());
        assertEquals(expectedUserDto.getCountryId(), userDto.getCountryId());
        assertEquals(expectedUserDto.getUserProfilePicId(), userDto.getUserProfilePicId());
    }

    @Test
    @DisplayName("If gets null than return null")
    void whenListUsersIsNullThenGetNull() {
        assertNull(userMapper.toDtos(null));
    }

    @Test
    @DisplayName("When gets List<User> with size 2 than return List<UserDto> with size 2")
    void whenListOfUsersIsNotNullThenGetListOfUserDtos() {
        List<User> users = List.of(
                User.builder()
                        .id(USER_ID_ONE)
                        .username("User")
                        .email("Email")
                        .build(),
                User.builder()
                        .id(USER_ID_TWO)
                        .username("User1")
                        .email("Email1")
                        .active(true)
                        .build());

        List<UserDto> userDtos = userMapper.toDtos(users);

        assertEquals(SIZE_USER_DTO_LIST, userDtos.size());
    }
}