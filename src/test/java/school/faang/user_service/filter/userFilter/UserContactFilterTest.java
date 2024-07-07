package school.faang.user_service.filter.userFilter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.contact.Contact;

import java.util.List;
import java.util.stream.Stream;

public class UserContactFilterTest {

    private static final UserFilterDto EMPTY_USER_FILTER_DTO = new UserFilterDto();
    private static final UserFilterDto USER_FILTER_DTO = UserFilterDto.builder().contactPattern("contact").build();
    private final UserContactFilter userContactFilter = new UserContactFilter();
    private List<User> users;

    @BeforeEach
    void init() {
        users = List.of(
                User.builder().contacts(List.of(Contact.builder().contact("my contact").build())).build(),
                User.builder().contacts(List.of(Contact.builder().contact("your contact").build())).build(),
                User.builder().contacts(List.of(Contact.builder().contact("something").build())).build());
    }


    @Test
    @DisplayName("Test applicable false for contact filter")
    public void testApplicableTrue() {
        Assertions.assertFalse(userContactFilter.isApplicable(EMPTY_USER_FILTER_DTO));
    }

    @Test
    @DisplayName("Test applicable true for contact filter")
    public void testApplicableFalse() {
        Assertions.assertTrue(userContactFilter.isApplicable(USER_FILTER_DTO));
    }

    @Test
    @DisplayName("Test apply for contact filter")
    public void testApply() {

        List<User> expectedList = List.of(
                User.builder().contacts(List.of(Contact.builder().contact("my contact").build())).build(),
                User.builder().contacts(List.of(Contact.builder().contact("your contact").build())).build());

        Stream<User> apply = userContactFilter.apply(users, USER_FILTER_DTO);
        Assertions.assertEquals(expectedList, apply.toList());
    }
}
