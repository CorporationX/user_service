package school.faang.user_service.service.Filter;

import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.service.subscription.filter.NameFilter;
import java.util.List;
import java.util.stream.Stream;
import static org.junit.jupiter.api.Assertions.*;

public class NameFilterTest {
    private final NameFilter nameFilter = new NameFilter();
    private final static String NAME_ALEX = "Alex";
    private final static String NAME_FRED = "Fred";
    private final static String NAME_JOHN = "John";

    @Test
    public void testIsApplicableTrue() {
        UserFilterDto userFilterDto = createFilter(NAME_ALEX);
        boolean result = nameFilter.isApplicable(userFilterDto);

        assertTrue(result);
    }

    @Test
    public void testIsApplicableFalse() {
        UserFilterDto userFilterDto = createFilter(null);
        boolean result = nameFilter.isApplicable(userFilterDto);

        assertFalse(result);
    }

    @Test
    public void testIsApplicableWhenNameIsEmpty() {
        UserFilterDto userFilterDto = createFilter("");
        boolean result = nameFilter.isApplicable(userFilterDto);

        assertFalse(result);
    }

    @Test
    public void testIsApplicableWhenNameIsBlank() {
        UserFilterDto userFilterDto = createFilter("    ");
        boolean result = nameFilter.isApplicable(userFilterDto);

        assertFalse(result);
    }

    @Test
    public void testApply() {
        Stream<User> users = createUsers(NAME_ALEX, NAME_FRED);
        UserFilterDto filter = createFilter(NAME_ALEX);

        List<User> result = nameFilter.apply(users, filter).toList();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(NAME_ALEX, result.get(0).getUsername());
    }

    @Test
    public void testApplyNotSuitableUsers() {
        Stream<User> users = createUsers(NAME_ALEX, NAME_FRED);
        UserFilterDto filter = createFilter(NAME_JOHN);

        List<User> result = nameFilter.apply(users, filter).toList();

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    private Stream<User> createUsers(String nameOfUser1, String nameOfUser2) {
        return Stream.of(
                User.builder().username(nameOfUser1).build(),
                User.builder().username(nameOfUser2).build()
        );
    }

    private UserFilterDto createFilter(String name) {
        return new UserFilterDto(name, null, null, null);
    }
}
