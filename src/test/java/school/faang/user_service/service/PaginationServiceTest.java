package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.model.filter_dto.UserFilterDto;
import school.faang.user_service.model.entity.User;
import school.faang.user_service.service.impl.PaginationServiceImpl;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PaginationServiceTest {

    private PaginationServiceImpl paginationService;

    private static final String ITEM_1 = "item1";
    private static final String ITEM_2 = "item2";
    private static final String ITEM_3 = "item3";
    private static final String ITEM_4 = "item4";
    private static final String ITEM_5 = "item5";

    @BeforeEach
    void setUp() {
        paginationService = new PaginationServiceImpl();
    }

    private User createUser(long id, String username) {
        return User.builder()
                .id(id)
                .username(username)
                .build();
    }

    @Test
    void testApplyPagination_ValidPagination() {
        List<String> items = List.of(ITEM_1, ITEM_2, ITEM_3, ITEM_4, ITEM_5);
        List<String> paginatedItems = paginationService.applyPagination(items, 1, 2);

        assertEquals(2, paginatedItems.size());
        assertEquals(ITEM_1, paginatedItems.get(0));
        assertEquals(ITEM_2, paginatedItems.get(1));
    }

    @Test
    void testApplyPagination_PageExceedsSize() {
        List<String> items = List.of(ITEM_1, ITEM_2, ITEM_3, ITEM_4, ITEM_5);
        List<String> paginatedItems = paginationService.applyPagination(items, 3, 2);

        assertEquals(1, paginatedItems.size());
        assertEquals(ITEM_5, paginatedItems.get(0));
    }

    @Test
    void testApplyPagination_OffsetExceedsSize() {
        List<String> items = List.of(ITEM_1, ITEM_2, ITEM_3);
        List<String> paginatedItems = paginationService.applyPagination(items, 5, 2);

        assertTrue(paginatedItems.isEmpty());
    }

    @Test
    void testApplyPagination_InvalidPageOrPageSize() {
        List<String> items = List.of(ITEM_1, ITEM_2, ITEM_3);
        List<String> paginatedItems = paginationService.applyPagination(items, 0, -1);

        assertEquals(items.size(), paginatedItems.size());
        assertEquals(items, paginatedItems);
    }

    @Test
    void testApplyPagination_EmptyList() {
        List<String> paginatedItems = paginationService.applyPagination(Collections.emptyList(), 1, 2);

        assertTrue(paginatedItems.isEmpty());
    }

    @Test
    void testFilterAndPaginateUsers_WithPagination() {
        List<User> users = List.of(
                createUser(1L, "user1"),
                createUser(2L, "user2"),
                createUser(3L, "user3")
        );

        UserFilterDto filter = new UserFilterDto();
        filter.setPage(1);
        filter.setPageSize(2);

        List<User> paginatedUsers = paginationService.applyPagination(users, filter.getPage(), filter.getPageSize());

        assertEquals(2, paginatedUsers.size());
        assertEquals("user1", paginatedUsers.get(0).getUsername());
        assertEquals("user2", paginatedUsers.get(1).getUsername());
    }
}
