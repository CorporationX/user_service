package school.faang.user_service.mapper;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import school.faang.user_service.util.TestDataFactory;

import static java.lang.String.*;
import static org.assertj.core.api.Assertions.*;

class PersonToUserMapperTest {

    private final PersonToUserMapper mapper = Mappers.getMapper(PersonToUserMapper.class);

    @Test
    void testMapToUser() {
        // given - precondition
        var person = TestDataFactory.createPerson();

        // when - action
        var actualResult = mapper.mapToUser(person);

        // then - verify the output
        assertThat(actualResult).isNotNull();
        assertThat(actualResult.getUsername())
                .isEqualTo(join("_", person.getFirstName(), person.getLastName()));
        assertThat(actualResult.getCity())
                .isEqualTo(person.getContactInfo().getAddress().getCity());
        assertThat(actualResult.getCountry().getTitle())
                .isEqualTo(person.getContactInfo().getAddress().getCountry());
    }
}