package school.faang.user_service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.RequestFilter;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.validator.Predicates;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

class RequestFilterPredicatesTest {

    private MentorshipRequest request;
    private RequestFilter filter;
    private Predicates predicates;

    @BeforeEach
    void setUp() {
        // Setup dummy data
        User requester = new User();
        requester.setId(1L);

        User receiver = new User();
        receiver.setId(2L);

        request = new MentorshipRequest();
        request.setDescription("Test description");
        request.setRequester(requester);
        request.setReceiver(receiver);
        request.setStatus(RequestStatus.ACCEPTED);

        filter = new RequestFilter();
        filter.setRequesterId(1L);
        filter.setReceiverId(2L);
        filter.setStatus(RequestStatus.ACCEPTED);

        predicates = new Predicates();
    }

    @Test
    void testIsDescriptionEmptyPredicate() {
        // Scenario 1: Description is not empty
        assertFalse(predicates.isDescriptionEmptyPredicate.test(request));

        // Scenario 2: Description is empty
        request.setDescription("");
        assertTrue(predicates.isDescriptionEmptyPredicate.test(request));
    }

    @Test
    void testAreAuthorsMatchPredicate() {
        // Scenario 1: Authors (requesters) match
        assertTrue(predicates.areAuthorsMatch.test(request, filter));

        // Scenario 2: Authors (requesters) don't match
        filter.setRequesterId(99L);
        assertFalse(predicates.areAuthorsMatch.test(request, filter));
    }

    @Test
    void testIsReceiverMatchPredicate() {
        // Scenario 1: Receivers match
        assertTrue(predicates.isRecieverMatch.test(request, filter));

        // Scenario 2: Receivers don't match
        filter.setReceiverId(99L);
        assertFalse(predicates.isRecieverMatch.test(request, filter));
    }

    @Test
    void testIsStatusMatchPredicate() {
        // Scenario 1: Status matches
        assertTrue(predicates.isStatusMatch.test(request, filter));

        // Scenario 2: Status doesn't match
        filter.setStatus(RequestStatus.REJECTED);
        assertFalse(predicates.isStatusMatch.test(request, filter));
    }
}
