package school.faang.user_service.service.Filter;

import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.service.subscription.filter.PhoneFilter;
import java.util.List;
import java.util.stream.Stream;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class PhoneFilterTest {
    private final PhoneFilter phoneFilter = new PhoneFilter();
    private final static String PHONE1 = "89043354392";
    private final static String PHONE2 = "89230462456";
    private final static String PHONE3 = "89037572905";

    @Test
    public void testIsApplicableTrue() {
        UserFilterDto userFilterDto = createFilter(PHONE1);
        boolean result = phoneFilter.isApplicable(userFilterDto);

        assertTrue(result);
    }

    @Test
    public void testIsApplicableFalse() {
        UserFilterDto userFilterDto = createFilter(null);
        boolean result = phoneFilter.isApplicable(userFilterDto);

        assertFalse(result);
    }

    @Test
    public void testIsApplicableWhenNameIsEmpty() {
        UserFilterDto userFilterDto = createFilter("");
        boolean result = phoneFilter.isApplicable(userFilterDto);

        assertFalse(result);
    }

    @Test
    public void testIsApplicableWhenNameIsBlank() {
        UserFilterDto userFilterDto = createFilter("   ");
        boolean result = phoneFilter.isApplicable(userFilterDto);

        assertFalse(result);
    }

    @Test
    public void testApply() {
        Stream<User> users = createUsers(PHONE2, PHONE3);
        UserFilterDto filter = createFilter(PHONE2);

        List<User> result = phoneFilter.apply(users, filter).toList();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(PHONE2, result.get(0).getPhone());
    }

    @Test
    public void testApplyNotSuitableUsers() {
        Stream<User> users = createUsers(PHONE2, PHONE3);
        UserFilterDto filter = createFilter(PHONE1);

        List<User> result = phoneFilter.apply(users, filter).toList();

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    private Stream<User> createUsers(String phoneNumber1, String phoneNumber2) {
        return Stream.of(
                User.builder().phone(phoneNumber1).build(),
                User.builder().phone(phoneNumber2).build()
        );
    }

    private UserFilterDto createFilter(String phoneNumber) {
        return new UserFilterDto(null, phoneNumber, null, null);
    }
}
