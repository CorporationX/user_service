package school.faang.user_service.mapper;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.contact.ContactPreference;
import school.faang.user_service.entity.contact.PreferredContact;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserMapperTest {

    private UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    @Test
    void toDto_returnDTO() {
        User user = new User();
        user.setId(1L);
        user.setUsername("User");
        user.setEmail("test@example.com");
        user.setPhone("123456789");
        user.setCountry(Country.builder().id(4L).build());
        ContactPreference contactPreference = new ContactPreference();
        contactPreference.setPreference(PreferredContact.EMAIL);
        user.setContactPreference(contactPreference);

        UserDto userDto = userMapper.toDto(user);

        assertEquals(user.getId(), userDto.getId());
        assertEquals(user.getUsername(), userDto.getUsername());
        assertEquals(user.getEmail(), userDto.getEmail());
        assertEquals(user.getPhone(), userDto.getPhone());
        assertEquals(user.getCountry().getId(), userDto.getCountryId());
        assertEquals(user.getContactPreference().getPreference(), userDto.getPreference());
    }

    @Test
    void toEntity_returnEntity() {
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setUsername("User");
        userDto.setEmail("test@example.com");
        userDto.setPhone("123456789");
        userDto.setCountryId(4L);
        userDto.setPreference(PreferredContact.EMAIL);

        User user = userMapper.toEntity(userDto);

        assertEquals(userDto.getId(), user.getId());
        assertEquals(userDto.getUsername(), user.getUsername());
        assertEquals(userDto.getEmail(), user.getEmail());
        assertEquals(userDto.getPhone(), user.getPhone());
        assertEquals(userDto.getCountryId(), user.getCountry().getId());
        assertEquals(userDto.getPreference(), user.getContactPreference().getPreference());
    }
}