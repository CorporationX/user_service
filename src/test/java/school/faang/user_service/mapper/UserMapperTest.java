package school.faang.user_service.mapper;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
class UserMapperTest {

    @InjectMocks
    private UserMapperImpl userMapper;

    private static final long ID = 1L;
    private static final String STRING = "Smth";

    @Test
    @DisplayName("Если передали null, на выходе получим null")
    void whenListUsersIsNullThenGetNull() {
        assertNull(userMapper.toDtos(null));
    }

    @Test
    @DisplayName("Если передать два листа, получим двойной размер")
    void whenListUsersIsNotNullThenGetListOfUserDtos() {
        List<User> users = List.of(
                User.builder()
                        .id(ID)
                        .username(STRING)
                        .email(STRING)
                        .build(),
                User.builder()
                        .id(ID)
                        .username(STRING)
                        .email(STRING)
                        .build());
        List<UserDto> userDtos = userMapper.toDtos(users);
        assertEquals(2L, userDtos.size());
    }
}