package school.faang.user_service.mapper;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import school.faang.user_service.util.TestDataFactory;

import static org.assertj.core.api.Assertions.*;

class UserMapperTest {

    private final UserMapper mapper = Mappers.getMapper(UserMapper.class);

    @Test
    void testToEntity() {
        // given - precondition
        var userDto = TestDataFactory.createUserDto();

        // when - action
        var actualResult = mapper.toEntity(userDto);

        // then - verify the output
        assertThat(actualResult).isNotNull();
        assertThat(actualResult.getId()).isEqualTo(userDto.getId());
        assertThat(actualResult.getUsername()).isEqualTo(userDto.getUsername());
        assertThat(actualResult.getEmail()).isEqualTo(userDto.getEmail());
    }

    @Test
    void testToDto() {
        // given - precondition
        var user = TestDataFactory.createUser();

        // when - action
        var actualResult = mapper.toDto(user);

        // then - verify the output
        assertThat(actualResult).isNotNull();
        assertThat(actualResult.getId()).isEqualTo(user.getId());
        assertThat(actualResult.getUsername()).isEqualTo(user.getUsername());
        assertThat(actualResult.getEmail()).isEqualTo(user.getEmail());
    }
}