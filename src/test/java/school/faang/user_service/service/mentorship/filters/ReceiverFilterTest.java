package school.faang.user_service.service.mentorship.filters;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.mentorship.request.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.User;
import school.faang.user_service.service.mentorship.filter.ReceiverFilter;
import school.faang.user_service.service.mentorship.filter.RequestFilter;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ReceiverFilterTest {
    private RequestFilter receiverFilter;
    private RequestFilterDto filterDto;

    @BeforeEach
    void setUp() {
        receiverFilter = new ReceiverFilter();
    }

    @Test
    @DisplayName("Applicable filter")
    void requesterFilterTest_isFilterApplicable() {
        filterDto = initFilterDto(1L);

        assertTrue(receiverFilter.isApplicable(filterDto));
    }

    @Test
    @DisplayName("Not applicable filter")
    void requesterFilterTest_isNotApplicable() {
        filterDto = initFilterDto(null);

        assertFalse(receiverFilter.isApplicable(filterDto));
    }

    @Test
    @DisplayName("Get filtered requests")
    void requesterFilterTest_getFilteredRequests() {
        filterDto = initFilterDto(1L);
        Stream<MentorshipRequest> requests = Stream.of(
                initRequest(1L),
                initRequest(1L),
                initRequest(2L));
        filterDto = initFilterDto(1L);

        List<MentorshipRequest> result = receiverFilter.apply(requests, filterDto).toList();
        assertEquals(2, result.size());
        assertTrue(result.stream()
                .map(request -> request.getReceiver().getId())
                .allMatch(receiverId -> receiverId.equals(1L)));
    }

    @Test
    @DisplayName("Get filtered empty list of requests")
    void requesterFilterTest_getFilteredEmptyRequests() {
        filterDto = initFilterDto(1L);
        Stream<MentorshipRequest> requests = Stream.empty();

        List<MentorshipRequest> result = receiverFilter.apply(requests, filterDto).toList();
        assertTrue(result.isEmpty());
    }

    private RequestFilterDto initFilterDto(Long receiverId) {
        return RequestFilterDto.builder()
                .receiverIdFilter(receiverId)
                .build();
    }

    private MentorshipRequest initRequest(Long requesterId) {
        MentorshipRequest request = new MentorshipRequest();
        User receiver = new User();
        receiver.setId(requesterId);
        request.setReceiver(receiver);
        return request;
    }
}
