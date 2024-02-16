package school.faang.user_service.mapper;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import school.faang.user_service.dto.user.UserCreateDto;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserMapperTest {

    private UserMapper userMapper = Mappers.getMapper(UserMapper.class);
    private CreateUserMapper createUserMapper = Mappers.getMapper(CreateUserMapper.class);

    @Test
    void toDto_returnDTO() {
        User user = new User();
        user.setId(1L);
        user.setUsername("User");
        user.setEmail("test@example.com");
        user.setPhone("123456789");
        user.setCountry(Country.builder().id(4L).build());

        UserCreateDto userDto = createUserMapper.toDto(user);

        assertEquals(user.getId(), userDto.getId());
        assertEquals(user.getUsername(), userDto.getUsername());
        assertEquals(user.getEmail(), userDto.getEmail());
        assertEquals(user.getPhone(), userDto.getPhone());
        assertEquals(user.getCountry().getId(), userDto.getCountryId());
    }

    @Test
    void toEntity_returnEntity() {
        UserCreateDto userDto = new UserCreateDto();
        userDto.setId(1L);
        userDto.setUsername("User");
        userDto.setEmail("test@example.com");
        userDto.setPhone("123456789");
        userDto.setCountryId(4L);

        User user = createUserMapper.toEntity(userDto);

        assertEquals(userDto.getId(), user.getId());
        assertEquals(userDto.getUsername(), user.getUsername());
        assertEquals(userDto.getEmail(), user.getEmail());
        assertEquals(userDto.getPhone(), user.getPhone());
        assertEquals(userDto.getCountryId(), user.getCountry().getId());
    }
}