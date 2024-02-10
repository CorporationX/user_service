package school.faang.user_service.mapper.mentorship;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapperImpl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
public class UserMapperTest {
    @InjectMocks
    UserMapperImpl userMapper;
    private User userEntity;
    private UserDto userDto;
    private List<User> userEntities;
    private List<UserDto> userDtos;

    @BeforeEach
    public void init() {
        long userId = 1L;
        String firstUsername = "test name";
        String secondUsername = "test name";
        String firstEmail = "test email";
        String secondEmail = "test email";
        String firstPhone = "test phone";
        String secondPhone = "test phone";
        LocalDateTime createdAt = LocalDateTime.MIN;
        LocalDateTime updatedAt = LocalDateTime.MAX;
        userEntity = User.builder()
                .id(userId).username(firstUsername).email(firstEmail).phone(firstPhone)
                .createdAt(createdAt).updatedAt(updatedAt)
                .build();
        userDto = UserDto.builder()
                .id(userId).username(firstUsername).email(firstEmail).phone(firstPhone)
                .createdAt(createdAt).updatedAt(updatedAt)
                .build();

        User secondUserEntity = User.builder()
                .id(userId).username(secondUsername).email(secondEmail).phone(secondPhone)
                .createdAt(createdAt).updatedAt(updatedAt)
                .build();
        UserDto secondUserDto = UserDto.builder()
                .id(userId).username(secondUsername).email(secondEmail).phone(secondPhone)
                .createdAt(createdAt).updatedAt(updatedAt)
                .build();
        userEntities = new ArrayList<>(List.of(userEntity, secondUserEntity));
        userDtos = new ArrayList<>(List.of(userDto, secondUserDto));
    }

    @Test
    public void testToDto_userNotNull_returnsUserDto() {
        UserDto dto = userMapper.toDto(userEntity);
        assertEquals(userDto, dto);
    }

    @Test
    public void testToEntity_userDtoNotNull_returnsUser() {
        User entity = userMapper.toEntity(userDto);
        assertEquals(userEntity, entity);
    }

    @Test
    public void testToDto_userNull_returnsNull() {
        UserDto dto = userMapper.toDto(null);
        assertNull(dto);
    }

    @Test
    public void testToEntity_userDtoNull_returnsNull() {
        User entity = userMapper.toEntity(null);
        assertNull(entity);
    }

    @Test
    public void testToDtoList_usersNotNull_returnsDtos() {
        List<UserDto> dtos = userMapper.toDtoList(userEntities);
        assertEquals(userDtos, dtos);
    }

    @Test
    public void testToEntityList_userDtosNotNull_returnsUsers() {
        List<User> entities = userMapper.toEntityList(userDtos);
        assertEquals(userEntities, entities);
    }

    @Test
    public void testToDtoList_usersNull_returnsNull () {
        List<UserDto> dtos = userMapper.toDtoList(null);
        assertNull(dtos);
    }

    @Test
    public void testToEntityList_userDtosNull_returnsNull () {
        List<User> entities = userMapper.toEntityList(null);
        assertNull(entities);
    }
}