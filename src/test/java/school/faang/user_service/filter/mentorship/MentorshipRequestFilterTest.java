package school.faang.user_service.filter.mentorship;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.mentorship.MentorshipRequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertAll;

class MentorshipRequestFilterTest {
    private static final String DESCRIPTION = "description";
    private static final Long REQUESTER_ID = 1L;
    private static final Long RECEIVER_ID = 2L;
    private static final RequestStatus STATUS = RequestStatus.PENDING;
    private List<MentorshipRequestFilter> filterList;
    private List<MentorshipRequest> requests;

    @BeforeEach
    void setUp() {
        filterList = List.of(
                new MentorshipRequestDescriptionFilter(),
                new MentorshipRequestRequesterFilter(),
                new MentorshipRequestReceiverFilter(),
                new MentorshipRequestStatusFilter()
        );
        initializeRequests();
    }

    @Test
    void isApplicable_ShouldReturnDescriptionFilter() {
        MentorshipRequestFilterDto filterDto = MentorshipRequestFilterDto.builder()
                .description(DESCRIPTION)
                .build();
        filterList.stream()
                .filter(filter -> filter.isApplicable(filterDto))
                .findFirst()
                .ifPresentOrElse(
                        filter -> assertTrue(filter instanceof MentorshipRequestDescriptionFilter),
                        Assertions::fail);
    }

    @Test
    void isApplicable_ShouldReturnRequesterFilter() {
        MentorshipRequestFilterDto filterDto = MentorshipRequestFilterDto.builder()
                .requesterId(REQUESTER_ID)
                .build();
        filterList.stream()
                .filter(filter -> filter.isApplicable(filterDto))
                .findFirst()
                .ifPresentOrElse(
                        filter -> assertTrue(filter instanceof MentorshipRequestRequesterFilter),
                        Assertions::fail);
    }

    @Test
    void isApplicable_ShouldReturnReceiverFilter() {
        MentorshipRequestFilterDto filterDto = MentorshipRequestFilterDto.builder()
                .receiverId(RECEIVER_ID)
                .build();
        filterList.stream()
                .filter(filter -> filter.isApplicable(filterDto))
                .findFirst()
                .ifPresentOrElse(
                        filter -> assertTrue(filter instanceof MentorshipRequestReceiverFilter),
                        Assertions::fail);
    }

    @Test
    void isApplicable_ShouldReturnStatusFilter() {
        MentorshipRequestFilterDto filterDto = MentorshipRequestFilterDto.builder()
                .status(STATUS.toString())
                .build();
        filterList.stream()
                .filter(filter -> filter.isApplicable(filterDto))
                .findFirst()
                .ifPresentOrElse(
                        filter -> assertTrue(filter instanceof MentorshipRequestStatusFilter),
                        Assertions::fail);
    }

    @Test
    void apply_shouldReturnFilteredRequests() {
        MentorshipRequestFilterDto filterDto = MentorshipRequestFilterDto.builder()
                .description(DESCRIPTION)
                .requesterId(REQUESTER_ID)
                .receiverId(RECEIVER_ID)
                .status(STATUS.toString())
                .build();

        filterList.stream()
                .filter(filter -> filter.isApplicable(filterDto))
                .forEach(filter -> filter.apply(requests, filterDto));

        assertAll(() -> {
            assertEquals(1, requests.size());
            assertEquals(DESCRIPTION, requests.get(0).getDescription());
            assertEquals(REQUESTER_ID, requests.get(0).getRequester().getId());
            assertEquals(RECEIVER_ID, requests.get(0).getReceiver().getId());
            assertEquals(STATUS, requests.get(0).getStatus());
        });
    }

    private void initializeRequests() {
        requests = new ArrayList<>();
        requests.add(MentorshipRequest.builder()
                .description(DESCRIPTION)
                .requester(User.builder().id(REQUESTER_ID).build())
                .receiver(User.builder().id(RECEIVER_ID).build())
                .status(STATUS)
                .build());
        requests.add(MentorshipRequest.builder()
                .description("another description")
                .requester(User.builder().id(REQUESTER_ID).build())
                .receiver(User.builder().id(RECEIVER_ID).build())
                .status(STATUS)
                .build());
        requests.add(MentorshipRequest.builder()
                .description(DESCRIPTION)
                .requester(User.builder().id(2L).build())
                .receiver(User.builder().id(RECEIVER_ID).build())
                .status(STATUS)
                .build());
        requests.add(MentorshipRequest.builder()
                .description(DESCRIPTION)
                .requester(User.builder().id(REQUESTER_ID).build())
                .receiver(User.builder().id(1L).build())
                .status(STATUS)
                .build());
        requests.add(MentorshipRequest.builder()
                .description(DESCRIPTION)
                .requester(User.builder().id(REQUESTER_ID).build())
                .receiver(User.builder().id(RECEIVER_ID).build())
                .status(RequestStatus.REJECTED)
                .build());
    }
}