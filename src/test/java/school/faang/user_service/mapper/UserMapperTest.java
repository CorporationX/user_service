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

    @Test
    @DisplayName("Если передали null, на выходе получим null")
    void WhenListUsersIsNullThenGetNull() {
        assertNull(userMapper.toDtos(null));
    }

    @Test
    @DisplayName("Проверка маппера")
    void WhenListUsersIsNotNullThenGetListOfUserDtos() {
        long ID = 1L;
        List<User> users = List.of(
                User.builder()
                        .id(ID)
                        .username("User")
                        .email("Email")
                        .build(),
                User.builder()
                        .id(ID)
                        .username("User1")
                        .email("Email1")
                        .build());
        List<UserDto> userDtos = userMapper.toDtos(users);
        assertEquals(2L, userDtos.size());
    }
}