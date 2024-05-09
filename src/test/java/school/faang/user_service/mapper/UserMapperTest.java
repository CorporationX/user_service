package school.faang.user_service.mapper;

import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.User;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserMapperTest {

    @Test
    public void testToDto() {
        UserDto userDto = new UserDto();
        userDto.setCity("City");
        userDto.setExperience(300);

        User user = new User();
        user.setCity("City");
        user.setExperience(300);

        UserMapperImpl userMapperImpl = new UserMapperImpl();

        assertEquals(userDto, userMapperImpl.toDto(user));
    }

    @Test
    public void testToDtoList() {
        List<UserDto> dtos = new ArrayList<>();
        UserDto userDto = new UserDto();
        UserDto userDto1 = new UserDto();
        userDto1.setCity("Tula");
        userDto.setCity("Moscow");

        dtos.add(userDto);
        dtos.add(userDto1);

        List<User> users = new ArrayList<>();
        User user = new User();
        User user1 = new User();
        user1.setCity("Tula");
        user.setCity("Moscow");
        users.add(user);
        users.add(user1);

        UserMapperImpl userMapperImpl = new UserMapperImpl();

        assertEquals(dtos, userMapperImpl.toDtoList(users));
    }
}
