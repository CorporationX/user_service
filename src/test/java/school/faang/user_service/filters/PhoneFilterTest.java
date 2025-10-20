package school.faang.user_service.filters;

import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.user.UserFiltersDto;
import school.faang.user_service.entity.user.User;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PhoneFilterTest {

    private final PhoneFilter phoneFilter = new PhoneFilter();

    @Test
    public void testIsApplicable_WhenPhonePatternIsNotNullAndNotBlankReturnsTrue() {
        boolean result = phoneFilter.isApplicable(new UserFiltersDto(null, "793", 0, 0));
        assertTrue(result);
    }

    @Test
    public void testIsApplicable_WhenPhonePatternEmptyReturnsFalse() {
        boolean result = phoneFilter.isApplicable(new UserFiltersDto(null, "", 0, 0));
        assertFalse(result);
    }

    @Test
    public void testIsApplicable_WhenPhonePatternIsBlankReturnsFalse() {
        boolean result = phoneFilter.isApplicable(new UserFiltersDto(null, " ", 0, 0));
        assertFalse(result);
    }

    @Test
    public void testIsApplicable_WhenPhonePatternIsNullReturnsFalse() {
        boolean result = phoneFilter.isApplicable(new UserFiltersDto(null, null, 0, 0));
        assertFalse(result);
    }

    @Test
    public void testApply_WhenExactPhoneMatchReturnsSingleUser() {
        Stream<User> users = Stream.of(
                User.builder().phone("+71234567890").build(),
                User.builder().phone("+70123456789").build()
        );
        Stream<User> result = phoneFilter.apply(users, new UserFiltersDto(null, "+70123456789", 0, 0));
        List<User> userList = result.toList();
        assertEquals(1, userList.size());
        assertEquals("+70123456789", userList.get(0).getPhone());
    }

    @Test
    public void testApply_WhenPartialPhoneMatchReturnsMatchingUsers() {
        Stream<User> users = Stream.of(
                User.builder().phone("+71234567890").build(),
                User.builder().phone("+79547859674").build(),
                User.builder().phone("+71012347820").build()
        );
        Stream<User> result = phoneFilter.apply(users, new UserFiltersDto(null, "1234", 0, 0));
        List<User> userList = result.toList();
        assertEquals(2, userList.size());
        assertTrue(userList.get(0).getPhone().contains("1234"));
        assertTrue(userList.get(1).getPhone().contains("1234"));
    }

    @Test
    public void testApply_WhenNoPhoneMatchReturnsEmptyStream() {
        Stream<User> users = Stream.of(
                User.builder().phone("+71234567890").build(),
                User.builder().phone("+79547859674").build()
        );
        Stream<User> result = phoneFilter.apply(users, new UserFiltersDto(null, "111", 0, 0));
        List<User> userList = result.toList();
        assertEquals(0, userList.size());
    }
}