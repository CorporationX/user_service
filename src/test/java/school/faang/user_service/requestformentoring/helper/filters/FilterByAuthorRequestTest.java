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
class FilterByAuthorRequestTest {
    private static final RequestFilterDto requestFilterDto = new RequestFilterDto();
    private static final MentorshipRequest mentorshipRequest = new MentorshipRequest();
    private static final User user = new User();
    private static final List<MentorshipRequest> menReqs = List.of(mentorshipRequest);
    private static final List<MentorshipRequest> listEmpty = new ArrayList<>();
    @InjectMocks
    private FilterByAuthorRequest filterByAuthorRequest;

    @Test
    void testFilterMenReqReturnNoEmptyListPositive() {
        requestFilterDto.setReceiverId(1L);
        mentorshipRequest.setReceiver(user);
        user.setId(1L);
        assertEquals(menReqs,
                assertDoesNotThrow(() -> filterByAuthorRequest.filterMenReq(requestFilterDto, menReqs)));
    }

    @Test
    void testFilterMenReqReturnEmptyListNotIdUserPositive() {
        requestFilterDto.setReceiverId(1L);
        mentorshipRequest.setReceiver(user);
        user.setId(2L);
        assertEquals(listEmpty,
                assertDoesNotThrow(() -> filterByAuthorRequest.filterMenReq(requestFilterDto, menReqs)));
    }

    @Test
    void testFilterMenReqReturnEmptyListNotDataFilterUserPositive() {
        requestFilterDto.setReceiverId(null);
        assertEquals(listEmpty,
                filterByAuthorRequest.filterMenReq(requestFilterDto, menReqs));
    }
}