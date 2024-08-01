package school.faang.user_service.filters;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.contact.Contact;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.*;

class UserContactFilterTest {
    private UserFilter filter;

    private UserFilterDto filterDtoNotNull;
    private UserFilterDto filterDtoNull;

    private Stream<User> users;

    @BeforeEach
    void setUp() {
        filter = new UserContactFilter();
        filterDtoNotNull = new UserFilterDto(
                null,
                null,
                null,
                "contact",
                null,
                null,
                null,
                null,
                null,
                null,
                1,
                5
        );
        filterDtoNull = new UserFilterDto(
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                1,
                5
        );
        users = Stream.<User>builder()
                .add(User.builder().contacts(List.of(Contact.builder().contact("My contact").build())).build())
                .add(User.builder().contacts(List.of(Contact.builder().contact("Nothing").build())).build())
                .build();
    }

    @Test
    void isApplicableTrue() {
        assertTrue(filter.isApplicable(filterDtoNotNull));
    }

    @Test
    void isApplicableFalse() {
        assertFalse(filter.isApplicable(filterDtoNull));
    }

    @Test
    void apply() {
        List<User> expectedUsers = List.of(User.builder().contacts(List.of(Contact.builder().contact("My contact").build())).build());
        List<User> actualResult = filter.apply(users, filterDtoNotNull).toList();
        assertEquals(expectedUsers, actualResult);
    }
}