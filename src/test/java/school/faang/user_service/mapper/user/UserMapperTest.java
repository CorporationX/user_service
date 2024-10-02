package school.faang.user_service.mapper.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import school.faang.user_service.dto.promotion.UserResponseDto;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserResponseShortDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.promotion.PromotionTariff;
import school.faang.user_service.entity.promotion.UserPromotion;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static school.faang.user_service.util.promotion.PromotionFabric.getUser;
import static school.faang.user_service.util.promotion.PromotionFabric.getUserPromotion;

public class UserMapperTest {
    private static final long USER_ID = 1;
    private static final String USERNAME = "username";
    private static final PromotionTariff TARIFF = PromotionTariff.STANDARD;
    private static final LocalDateTime LOCAL_DATE_TIME = LocalDateTime.of(2000, 1, 1, 1,1);

    private final UserMapper responseUserMapper = Mappers.getMapper(UserMapper.class);

    private UserMapper userMapper;
    private User user1;
    private User user2;

    @BeforeEach
    void setUp() {
        userMapper = Mappers.getMapper(UserMapper.class);
        user1 = new User();
        user1.setId(1L);
        user1.setUsername("John");
        user1.setEmail("john@test.com");

        user2 = new User();
        user2.setId(2L);
        user2.setUsername("Dan");
        user2.setEmail("dan@test.com");
    }

    @Test
    void testToUserDto() {
        UserResponseShortDto userDto = userMapper.toUserResponseShortDto(user1);

        assertEquals(user1.getId(), userDto.getId());
        assertEquals(user1.getUsername(), userDto.getUsername());
        assertEquals(user1.getEmail(), userDto.getEmail());
    }

    @Test
    void testToListUserDtos() {
        List<User> users = List.of(user1, user2);

        List<UserResponseShortDto> userDtos = userMapper.toUserResponseShortDtos(users);

        assertEquals(users.size(), userDtos.size());
        assertEquals(user1.getUsername(), userDtos.get(0).getUsername());
        assertEquals(user2.getUsername(), userDtos.get(1).getUsername());

    }

    @Test
    @DisplayName("Test convert event to response event")
    void testToDto() {
        UserPromotion userPromotion = getUserPromotion(TARIFF, TARIFF.getNumberOfViews());
        User user = getUser(USER_ID, USERNAME, List.of(userPromotion), LOCAL_DATE_TIME);
        var responseDto = new UserResponseDto(USER_ID, USERNAME, TARIFF.toString(), TARIFF.getNumberOfViews(), LOCAL_DATE_TIME);

        assertThat(responseUserMapper.toUserResponseDto(user)).isEqualTo(responseDto);
    }
}
