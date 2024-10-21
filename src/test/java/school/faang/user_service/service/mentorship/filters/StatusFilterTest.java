package school.faang.user_service.service.mentorship.filters;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.mentorship.request.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.service.mentorship.filter.RequestFilter;
import school.faang.user_service.service.mentorship.filter.StatusFilter;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StatusFilterTest {
    private RequestFilter statusFilter;
    private RequestFilterDto filterDto;

    @BeforeEach
    void setUp() {
        statusFilter = new StatusFilter();
    }

    @Test
    @DisplayName("Applicable filter")
    void requesterFilterTest_isFilterApplicable() {
        filterDto = initFilterDto(RequestStatus.PENDING);

        assertTrue(statusFilter.isApplicable(filterDto));
    }

    @Test
    @DisplayName("Not applicable filter")
    void requesterFilterTest_isNotApplicable() {
        filterDto = initFilterDto(null);

        assertFalse(statusFilter.isApplicable(filterDto));
    }

    @Test
    @DisplayName("Get filtered requests")
    void requesterFilterTest_getFilteredRequests() {
        filterDto = initFilterDto(RequestStatus.PENDING);
        Stream<MentorshipRequest> requests = Stream.of(
                initRequest(RequestStatus.PENDING),
                initRequest(RequestStatus.PENDING),
                initRequest(RequestStatus.REJECTED));
        filterDto = initFilterDto(RequestStatus.PENDING);

        List<MentorshipRequest> result = statusFilter.apply(requests, filterDto).toList();
        assertEquals(2, result.size());
        assertTrue(result.stream()
                .map(MentorshipRequest::getStatus)
                .allMatch(status -> status == RequestStatus.PENDING));
    }

    @Test
    @DisplayName("Get filtered empty list of requests")
    void requesterFilterTest_getFilteredEmptyRequests() {
        filterDto = initFilterDto(RequestStatus.PENDING);
        Stream<MentorshipRequest> requests = Stream.empty();

        List<MentorshipRequest> result = statusFilter.apply(requests, filterDto).toList();
        assertTrue(result.isEmpty());
    }

    private RequestFilterDto initFilterDto(RequestStatus status) {
        return RequestFilterDto.builder()
                .statusFilter(status)
                .build();
    }

    private MentorshipRequest initRequest(RequestStatus status) {
        MentorshipRequest request = new MentorshipRequest();
        request.setStatus(status);
        return request;
    }
}
