package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.mentorship.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.service.mentorship.MentorshipRequestService;
import school.faang.user_service.service.mentorship.filter.MentorshipRequestFilter;
import school.faang.user_service.service.mentorship.filter.MentorshipRequestFilterByDescription;
import school.faang.user_service.service.mentorship.filter.MentorshipRequestFilterByReceiver;
import school.faang.user_service.service.mentorship.filter.MentorshipRequestFilterByRequestStatus;
import school.faang.user_service.service.mentorship.filter.MentorshipRequestFilterByRequester;
import school.faang.user_service.service.mentorship.filter.MentorshipRequestFilterByUpdatedTime;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class MentorshipRequestFilterTest {

    @InjectMocks
    private MentorshipRequestService requestService;
    private List<MentorshipRequestFilter> filters = getFilters();
    private Stream<MentorshipRequest> allRequests;
    private RequestFilterDto filterDto;
    private User requester;
    private User receiver;
    private MentorshipRequest request1;
    private MentorshipRequest request2;
    private MentorshipRequest request3;
    private MentorshipRequest request4;
    private MentorshipRequest request5;
    private final long REQUESTER_ID = 1L;
    private final long RECEIVER_ID = 2L;
    private final LocalDateTime TEST_TIME = LocalDateTime.now();

    @BeforeEach
    void initData() {
        filterDto = RequestFilterDto.builder().build();
        requester = User.builder()
                .id(1L)
                .build();
        receiver = User.builder()
                .id(2L)
                .build();

        request1 = MentorshipRequest.builder()
                .description("description")
                .requester(requester)
                .receiver(receiver)
                .updatedAt(TEST_TIME)
                .status(RequestStatus.REJECTED)
                .build();
        request2 = MentorshipRequest.builder()
                .description("another description")
                .requester(requester)
                .receiver(receiver)
                .updatedAt(TEST_TIME.minusMonths(1))
                .status(RequestStatus.ACCEPTED)
                .build();
        request3 = MentorshipRequest.builder()
                .description("description")
                .requester(requester)
                .receiver(requester)
                .updatedAt(TEST_TIME)
                .status(RequestStatus.PENDING)
                .build();
        request4 = MentorshipRequest.builder()
                .description("description")
                .requester(requester)
                .receiver(receiver)
                .updatedAt(TEST_TIME.minusMonths(2))
                .status(RequestStatus.ACCEPTED)
                .build();
        request5 = MentorshipRequest.builder()
                .description("   ")
                .requester(receiver)
                .receiver(receiver)
                .updatedAt(TEST_TIME)
                .status(RequestStatus.ACCEPTED)
                .build();

        allRequests = List.of(request1, request2, request3, request4, request5).stream();
    }

    @Test
    void testRequestFilteringWithEmptyFilter() {
        List<MentorshipRequest> expectedList = List.of(request1, request2, request3, request4, request5);
        List<MentorshipRequest> actualList = doFiltering(filterDto);

        assertEquals(expectedList, actualList);
    }

    @Test
    void testRequestFilteringByDescription() {
        filterDto.setDescription("description");

        List<MentorshipRequest> actualList = doFiltering(filterDto);
        List<MentorshipRequest> expectedList = List.of(request1, request2, request3, request4);

        assertEquals(expectedList, actualList);
    }

    @Test
    void testRequestFilteringByReceiver() {
        filterDto.setReceiver(RECEIVER_ID);

        List<MentorshipRequest> actualList = doFiltering(filterDto);
        List<MentorshipRequest> expectedList = List.of(request1, request2, request4, request5);

        assertEquals(expectedList, actualList);
    }

    @Test
    void testRequestFilteringByRequester() {
        filterDto.setRequester(REQUESTER_ID);

        List<MentorshipRequest> actualList =doFiltering(filterDto);
        List<MentorshipRequest> expectedList = List.of(request1, request2, request3, request4);

        assertEquals(expectedList, actualList);
    }

    @Test
    void testRequestFilteringByUpdatedAt() {
        filterDto.setUpdatedAt(TEST_TIME);

        List<MentorshipRequest> actualList = doFiltering(filterDto);
        List<MentorshipRequest> expectedList = List.of(request1, request3, request5);

        assertEquals(expectedList, actualList);
    }

    @Test
    void testRequestFilteringByStatus() {
        filterDto.setRequestStatus(RequestStatus.ACCEPTED);

        List<MentorshipRequest> actualList = doFiltering(filterDto);
        List<MentorshipRequest> expectedList = List.of(request2, request4, request5);

        assertEquals(expectedList, actualList);
    }

    @Test
    void testRequestFilteringWithMixFilters() {
        filterDto.setDescription("description");
        filterDto.setReceiver(RECEIVER_ID);
        filterDto.setRequester(REQUESTER_ID);

        List<MentorshipRequest> actualList = doFiltering(filterDto);
        List<MentorshipRequest> expectedList = List.of(request1, request2, request4);

        assertEquals(expectedList, actualList);
    }

    @Test
    void testRequestFilteringWithAllFilters() {
        filterDto.setDescription("description");
        filterDto.setRequester(REQUESTER_ID);
        filterDto.setReceiver(RECEIVER_ID);
        filterDto.setUpdatedAt(TEST_TIME);
        filterDto.setRequestStatus(RequestStatus.REJECTED);

        List<MentorshipRequest> actualList = doFiltering(filterDto);
        List<MentorshipRequest> expectedList = List.of(request1);

        assertEquals(expectedList, actualList);
    }

    @Test
    void testRequestFilteringWithEmptyList() {
        filterDto.setUpdatedAt(TEST_TIME.minusDays(10));

        List<MentorshipRequest> actualList = doFiltering(filterDto);
        List<MentorshipRequest> expectedList = new ArrayList<>();

        assertEquals(expectedList, actualList);
    }

    private List<MentorshipRequest> doFiltering(RequestFilterDto filterDto) {
        List<MentorshipRequestFilter> applicableFilters = filters.stream()
                .filter(filter -> filter.isApplicable(filterDto)).toList();

        for (MentorshipRequestFilter filter : applicableFilters) {
            allRequests = filter.apply(allRequests, filterDto);
        }
        return allRequests.toList();
    }

    private List<MentorshipRequestFilter> getFilters() {
        return List.of(new MentorshipRequestFilterByDescription(), new MentorshipRequestFilterByReceiver(),
                new MentorshipRequestFilterByRequester(), new MentorshipRequestFilterByRequestStatus(),
                new MentorshipRequestFilterByUpdatedTime());
    }
}