package school.faang.user_service.requestformentoring.helper.filters;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class FilterByRequestStatusTest {

    private static final RequestFilterDto requestFilterDto = new RequestFilterDto();
    private static final MentorshipRequest mentorshipRequest = new MentorshipRequest();
    private static final List<MentorshipRequest> menReqs = List.of(mentorshipRequest);
    private static final List<MentorshipRequest> listEmpty = new ArrayList<>();
    @InjectMocks
    private FilterByRequestStatus filterByRequestStatus;

    @Test
    void testFilterMenReqReturnNoEmptyListPositive() {
        requestFilterDto.setStatus(RequestStatus.ACCEPTED);
        mentorshipRequest.setStatus(RequestStatus.ACCEPTED);
        assertEquals(menReqs,
                assertDoesNotThrow(() -> filterByRequestStatus.filterMenReq(requestFilterDto, menReqs)));
    }

    @Test
    void testFilterMenReqReturnEmptyListNotIdUserPositive() {
        requestFilterDto.setStatus(RequestStatus.REJECTED);
        mentorshipRequest.setStatus(RequestStatus.ACCEPTED);
        assertEquals(listEmpty,
                assertDoesNotThrow(() -> filterByRequestStatus.filterMenReq(requestFilterDto, menReqs)));
    }

    @Test
    void testFilterMenReqReturnEmptyListNotDataFilterUserPositive() {
        requestFilterDto.setStatus(null);
        assertEquals(listEmpty,
                filterByRequestStatus.filterMenReq(requestFilterDto, menReqs));
    }
}