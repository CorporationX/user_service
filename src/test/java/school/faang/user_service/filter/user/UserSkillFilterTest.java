package school.faang.user_service.filter.user;

import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.filters.user.UserSkillFilter;
import school.faang.user_service.util.TestDataFactory;

import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class UserSkillFilterTest {

    private UserSkillFilter skillFilter = new UserSkillFilter();

    @Test
    public void givenValidUserWhenApplyThenReturnUser() {
        // Given
        var user = TestDataFactory.createUser();
        var filter = TestDataFactory.filterDto();
        filter.setSkillPattern("Java");

        // When
        var result = skillFilter.apply(Stream.of(user), filter);

        // Then
        assertNotNull(result);
        assertEquals(result.findFirst().get(), user);
    }

    @Test
    public void givenValidUserWhenApplyThenReturnEmptyStream() {
        // Given
        var user = TestDataFactory.createUser();
        UserFilterDto filter = new UserFilterDto();
        filter.setSkillPattern("Run");

        // When
        var result = skillFilter.apply(Stream.of(user), filter);

        // Then
        assertNotNull(result);
        assertFalse(result.findAny().isPresent());
    }
}
