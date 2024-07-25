package school.faang.user_service.mapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.userDto.UserDto;
import school.faang.user_service.entity.User;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class UserMapperTest {

    private final UserMapper userMapper = Mappers.getMapper(UserMapper.class);

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
}