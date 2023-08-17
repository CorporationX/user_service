package school.faang.user_service.service.filter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.filter.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.User;
import school.faang.user_service.dto.mentorship.service.mentorship.filter.MentorshipRequestReceiverNameFilter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MentorshipRequestReceiverNameFilterTest {
    private final MentorshipRequestReceiverNameFilter descriptionFilter = new MentorshipRequestReceiverNameFilter();
    private RequestFilterDto filter;
    private List<MentorshipRequest> requests;
    private MentorshipRequest request1;
    private MentorshipRequest request2;
    private MentorshipRequest request3;
    private User user1;
    private User user2;
    private User user3;


    @BeforeEach
    void setUp() {
        filter = RequestFilterDto.builder().build();

        request1 = new MentorshipRequest();
        request2 = new MentorshipRequest();
        request3 = new MentorshipRequest();

        user1 = new User();
        user2 = new User();
        user3 = new User();

        user1.setUsername("user1");
        user2.setUsername("user2");
        user3.setUsername("user3");

        request1.setReceiver(user1);
        request2.setReceiver(user2);
        request3.setReceiver(user3);

        requests = new ArrayList<>();

        requests.add(request1);
        requests.add(request2);
        requests.add(request3);
    }

    @Test
    void testIsApplicableWhenReceiverNamePatternNotEmpty() {
        filter.setReceiverNamePattern("1");
        assertTrue(descriptionFilter.isApplicable(filter));
    }

    @Test
    void testNotApplicableWhenDescriptionPatternEmpty() {
        assertFalse(descriptionFilter.isApplicable(filter));
    }

    @Test
    void testFilterFilters() {
        filter.setReceiverNamePattern("2");
        Stream<MentorshipRequest> filteredRequestsStream = descriptionFilter.apply(requests.stream(), filter);
        MentorshipRequest filteredRequest = filteredRequestsStream.findFirst().get();

        assertEquals(filteredRequest, request2);
    }
}
