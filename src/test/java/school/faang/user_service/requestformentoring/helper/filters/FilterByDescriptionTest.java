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
class FilterByDescriptionTest {

    private static final RequestFilterDto requestFilterDto = new RequestFilterDto();
    private static final MentorshipRequest mentorshipRequest = new MentorshipRequest();
    private static final List<MentorshipRequest> menReqs = List.of(mentorshipRequest);
    private static final List<MentorshipRequest> listEmpty = new ArrayList<>();
    @InjectMocks
    private FilterByDescription filterByDescription;

    @Test
    void testFilterMenReqReturnNoEmptyListPositive() {
        requestFilterDto.setDescription("Test");
        mentorshipRequest.setDescription("Test");
        assertEquals(menReqs,
                assertDoesNotThrow(() -> filterByDescription.filterMenReq(requestFilterDto, menReqs)));
    }

    @Test
    void testFilterMenReqReturnEmptyListNotDescriptionPositive() {
        requestFilterDto.setDescription("Tests");
        mentorshipRequest.setDescription("Test");
        assertEquals(listEmpty,
                assertDoesNotThrow(() -> filterByDescription.filterMenReq(requestFilterDto, menReqs)));
    }

    @Test
    void testFilterMenReqReturnEmptyListNotDataFilterDescriptionPositive() {
        requestFilterDto.setDescription(null);
        assertEquals(listEmpty,
                filterByDescription.filterMenReq(requestFilterDto, menReqs));
    }
}