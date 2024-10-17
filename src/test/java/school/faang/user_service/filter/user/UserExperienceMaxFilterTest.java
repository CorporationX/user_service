package school.faang.user_service.filter.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.model.dto.user.UserFilterDto;
import school.faang.user_service.model.entity.User;
import school.faang.user_service.util.TestDataFactory;

import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class UserExperienceMaxFilterTest {
    private UserExperienceMaxFilter experienceMaxFilter = new UserExperienceMaxFilter();
    private User user;

    @BeforeEach
    public void setUp() {
        user = TestDataFactory.createUser();
    }

    @Test
    public void givenValidUserWhenApplyThenReturnUser() {
        // Given
        var filter = TestDataFactory.createFilterDto();
        filter.setExperienceMax(12);

        // When
        var result = experienceMaxFilter.apply(Stream.of(user), filter);

        // Then
        assertNotNull(result);
        assertEquals(result.findFirst().get(), user);
    }

    @Test
    public void givenValidUserWhenApplyThenReturnEmptyStream() {
        // Given
        var user = TestDataFactory.createUser();
        UserFilterDto filter = new UserFilterDto();
        filter.setExperienceMax(12);

        // When
        var result = experienceMaxFilter.apply(Stream.of(user), filter);

        // Then
        assertNotNull(result);
        assertTrue(result.findAny().isPresent());
    }


}
