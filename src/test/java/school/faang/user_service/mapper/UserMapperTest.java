package school.faang.user_service.mapper;

import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserMapperTest {
    @Test
    public void testToDto() {
        UserDto userDto = UserDto.builder()
                .city("City")
                .experience(300).build();

        User user = User.builder()
                .city("City")
                .experience(300)
                .build();

        UserMapperImpl userMapperImpl = new UserMapperImpl();

        assertEquals(userDto, userMapperImpl.toDto(user));
    }

    @Test
    public void testToDtoList() {
        UserDto userDto = UserDto.builder()
                .city("Moscow")
                .build();
        UserDto userDto1 = UserDto.builder()
                .city("Tula")
                .build();
        List<UserDto> dtos = List.of(userDto, userDto1);

        User user = User.builder()
                .city("Moscow")
                .build();
        User user1 = User.builder()
                .city("Tula")
                .build();
        List<User> users = List.of(user, user1);

        UserMapperImpl userMapperImpl = new UserMapperImpl();

        assertEquals(dtos, userMapperImpl.toDtoList(users));
    }
}
