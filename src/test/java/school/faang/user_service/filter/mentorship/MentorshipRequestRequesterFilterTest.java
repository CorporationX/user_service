package school.faang.user_service.filter.mentorship;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.mentorship.MentorshipRequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class MentorshipRequestRequesterFilterTest {
    private static final Long REQUESTER_ID = 1L;
    private final MentorshipRequestRequesterFilter mentorshipRequestRequesterFilter =
            new MentorshipRequestRequesterFilter();

    @Test
    public void testIsNotApplicable() {
        boolean isApplicable = mentorshipRequestRequesterFilter.isApplicable(new MentorshipRequestFilterDto());
        assertFalse(isApplicable);
    }

    @Test
    public void testIsApplicable() {
        MentorshipRequestFilterDto mentorshipRequestFilterDto = prepareDataToDto(REQUESTER_ID);
        boolean isApplicable = mentorshipRequestRequesterFilter.isApplicable(mentorshipRequestFilterDto);
        assertTrue(isApplicable);
    }

    @Test
    public void testApply() {
        MentorshipRequestFilterDto mentorshipRequestFilterDto = prepareDataToDto(REQUESTER_ID);
        Stream<MentorshipRequest> requests = prepareStreamOfRequests();
        List<MentorshipRequest> mentorshipRequests = mentorshipRequestRequesterFilter.apply(requests,
                mentorshipRequestFilterDto).toList();
        assertEquals(1, mentorshipRequests.size());
        assertEquals(REQUESTER_ID, Optional.ofNullable(mentorshipRequests.get(0).getRequester().getId())
                .orElse(null));
    }

    private Stream<MentorshipRequest> prepareStreamOfRequests() {
        List<MentorshipRequest> mentorshipRequestList = fillListOfRequests();

        return mentorshipRequestList.stream();
    }

    private List<MentorshipRequest> fillListOfRequests() {
        List<MentorshipRequest> mentorshipRequestList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            User user = new User();
            user.setId((long) i);
            MentorshipRequest mentorshipRequest = new MentorshipRequest();
            mentorshipRequest.setRequester(user);
            mentorshipRequestList.add(mentorshipRequest);
        }
        return mentorshipRequestList;
    }

    private MentorshipRequestFilterDto prepareDataToDto(Long id) {
        MentorshipRequestFilterDto mentorshipRequestFilterDto = new MentorshipRequestFilterDto();
        mentorshipRequestFilterDto.setRequesterId(id);
        return mentorshipRequestFilterDto;
    }
}
