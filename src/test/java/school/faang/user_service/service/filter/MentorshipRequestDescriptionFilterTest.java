package school.faang.user_service.service.filter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.filter.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.service.mentorship.filter.MentorshipRequestDescriptionFilter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class MentorshipRequestDescriptionFilterTest {
    private final MentorshipRequestDescriptionFilter descriptionFilter = new MentorshipRequestDescriptionFilter();
    private RequestFilterDto filter;
    private List<MentorshipRequest> requests;
    private MentorshipRequest request1;
    private MentorshipRequest request2;
    private MentorshipRequest request3;


    @BeforeEach
    void setUp() {
        filter = RequestFilterDto.builder().build();

        request1 = new MentorshipRequest();
        request2 = new MentorshipRequest();
        request3 = new MentorshipRequest();

        request1.setDescription("description1");
        request2.setDescription("description2");
        request3.setDescription("description3");

        requests = new ArrayList<>();

        requests.add(request1);
        requests.add(request2);
        requests.add(request3);
    }

    @Test
    void testIsApplicableWhenDescriptionPatternNotEmpty() {
        filter.setDescriptionPattern("description");
        assertTrue(descriptionFilter.isApplicable(filter));
    }

    @Test
    void testNotApplicableWhenDescriptionPatternEmpty() {
        assertFalse(descriptionFilter.isApplicable(filter));
    }

    @Test
    void testFilterFilters() {
        filter.setDescriptionPattern("1");
        Stream<MentorshipRequest> filteredRequestsStream = descriptionFilter.apply(requests.stream(), filter);
        MentorshipRequest filteredRequest = filteredRequestsStream.findFirst().get();

        assertEquals(filteredRequest, request1);
    }
}
