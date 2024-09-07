package school.faang.user_service.requestformentoring.helper.filters;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.User;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class FilterByRecipientRequestTest {

    private static final RequestFilterDto requestFilterDto = new RequestFilterDto();
    private static final MentorshipRequest mentorshipRequest = new MentorshipRequest();
    private static final User user = new User();
    private static final List<MentorshipRequest> menReqs = List.of(mentorshipRequest);
    private static final List<MentorshipRequest> listEmpty = new ArrayList<>();
    @InjectMocks
    private FilterByRecipientRequest filterByRecipientRequest;

    @Test
    void testFilterMenReqReturnNoEmptyListPositive() {
        requestFilterDto.setRequesterId(1L);
        mentorshipRequest.setRequester(user);
        user.setId(1L);
        assertEquals(menReqs,
                assertDoesNotThrow(() -> filterByRecipientRequest.filterMenReq(requestFilterDto, menReqs)));
    }

    @Test
    void testFilterMenReqReturnEmptyListNotIdUserPositive() {
        requestFilterDto.setRequesterId(1L);
        mentorshipRequest.setRequester(user);
        user.setId(2L);
        assertEquals(listEmpty,
                assertDoesNotThrow(() -> filterByRecipientRequest.filterMenReq(requestFilterDto, menReqs)));
    }

    @Test
    void testFilterMenReqReturnEmptyListNotDataFilterUserPositive() {
        requestFilterDto.setRequesterId(null);
        assertEquals(listEmpty,
                filterByRecipientRequest.filterMenReq(requestFilterDto, menReqs));
    }
}