package school.faang.user_service.mapper;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.contact.ContactPreference;
import school.faang.user_service.entity.contact.PreferredContact;
import school.faang.user_service.mapper.user.UserMapperImpl;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class UserMapperTest {
    @Spy
    private UserMapperImpl userMapper;
    private User user;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .username("username")
                .email("user@email")
                .phone("12345678")
                .aboutMe("aboutUser")
                .country(Country.builder().title("country").build())
                .city("city")
                .experience(10)
                .contactPreference(ContactPreference.builder().preference(PreferredContact.EMAIL).build())
                .build();

        userDto = UserDto.builder()
                .id(1L)
                .username("username")
                .email("user@email")
                .phone("12345678")
                .aboutMe("aboutUser")
                .city("city")
                .experience(10)
                .preference(PreferredContact.EMAIL)
                .build();
    }

    @Test
    void userToUserDto_shouldMatchAllFields() {
        UserDto actual = userMapper.toDto(user);
        assertAll(() -> {
            assertEquals(1L, actual.getId());
            assertEquals("username", actual.getUsername());
            assertEquals("user@email", actual.getEmail());
            assertEquals("12345678", actual.getPhone());
            assertEquals("aboutUser", actual.getAboutMe());
            assertEquals("country", actual.getCountry());
            assertEquals("city", actual.getCity());
            assertEquals(10, actual.getExperience());
            assertEquals(PreferredContact.EMAIL, actual.getPreference());
        });
    }

    @Test
    void userDtoToUser_shouldMatchAllFields() {
        User actual = userMapper.toEntity(userDto);
        assertAll(() -> {
            assertEquals(1L, actual.getId());
            assertEquals("username", actual.getUsername());
            assertEquals("user@email", actual.getEmail());
            assertEquals("12345678", actual.getPhone());
            assertEquals("aboutUser", actual.getAboutMe());
            assertEquals("city", actual.getCity());
            assertEquals(10, actual.getExperience());
            assertEquals(PreferredContact.EMAIL, actual.getContactPreference().getPreference());
        });
    }

    @Test
    void test_user_mapper_user_to_user_dto() {
        UserDto result = userMapper.toDto(user);
        Assertions.assertAll(
                () -> assertEquals(result.getId(), userDto.getId()),
                () -> assertEquals(result.getUsername(), userDto.getUsername()),
                () -> assertEquals(result.getEmail(), userDto.getEmail())
        );
    }

    @Test
    void test_user_mapper_user_dto_to_user() {
        User result = userMapper.toEntity(userDto);
        Assertions.assertAll(
                () -> assertEquals(result.getId(), user.getId()),
                () -> assertEquals(result.getUsername(), user.getUsername()),
                () -> assertEquals(result.getEmail(), user.getEmail())
        );
    }
}