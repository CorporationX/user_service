package school.faang.user_service.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.contact.ContactPreference;
import school.faang.user_service.entity.contact.PreferredContact;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class UserMapperTest {

    private final UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    private User user;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1)
                .username("Name")
                .email("Email")
                .contactPreference(ContactPreference
                        .builder()
                        .preference(PreferredContact.EMAIL)
                        .build())
                .build();

        userDto = UserDto.builder()
                .id(1L)
                .username("Name")
                .email("Email")
                .preference(PreferredContact.EMAIL)
                .build();
    }

    @Test
    @DisplayName("convetListUserforListUserDto")
    public void convertListUserForListUserDto() {
        User firstUser = new User();
        firstUser.setId(1L);
        firstUser.setUsername("Alex");
        User secondUser = new User();
        secondUser.setId(2L);
        secondUser.setUsername("Andry");
        List<User> userList = List.of(firstUser, secondUser);

        UserDto firstUserDto = new UserDto();
        firstUserDto.setId(1L);
        firstUserDto.setUsername("Alex");
        UserDto secondUserDto = new UserDto();
        secondUserDto.setId(2L);
        secondUserDto.setUsername("Andry");
        List<UserDto> userListDto = List.of(firstUserDto, secondUserDto);

        List<UserDto> actualResult = userMapper.toDtoList(userList);

        assertThat(actualResult).isEqualTo(userListDto);
    }

    @Test
    public void testToDto() {
        UserDto result = userMapper.toUserDto(user);

        assertEquals(userDto.getId(), result.getId());
        assertEquals(userDto.getUsername(), result.getUsername());
        assertEquals(userDto.getEmail(), result.getEmail());
        assertEquals(userDto.getPreference(), userDto.getPreference());
    }

    @Test
    public void testToEntity() {
        User result = userMapper.toEntity(userDto);

        assertEquals(user.getId(), result.getId());
        assertEquals(user.getUsername(), result.getUsername());
        assertEquals(user.getEmail(), result.getEmail());
        assertEquals(user.getContactPreference(), result.getContactPreference());
    }
}