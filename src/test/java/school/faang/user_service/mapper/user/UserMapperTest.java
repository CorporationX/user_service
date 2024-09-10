package school.faang.user_service.mapper.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
class UserMapperTest {

    @Spy
    private UserMapperImpl userMapper;

    private final long USER_ID_ONE = 1L;
    private final long USER_ID_TWO = 2L;

    private final int SIZE_USER_DTO_LIST = 2;

    @Test
    @DisplayName("Если передали null, на выходе получим null")
    void whenListUsersIsNullThenGetNull() {
        assertNull(userMapper.toDtos(null));
    }

    @Test
    @DisplayName("При передаче 2 элементов List<User> на выходе получим размер List<UserDto> равным 2")
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