package school.faang.user_service.filter.mentorship;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.mentorship.MentorshipRequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.User;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class MentorshipRequestDescriptionFilterTest {
    private static final String DESCRIPTION = "Hard work";
    private static final String DESCRIPTION_ANOTHER = "Work";
    private final MentorshipRequestDescriptionFilter mentorshipRequestDescriptionFilter =
            new MentorshipRequestDescriptionFilter();

    @Test
    public void testIsNotApplicable() {
        boolean isApplicable = mentorshipRequestDescriptionFilter.isApplicable(new MentorshipRequestFilterDto());
        assertFalse(isApplicable);
    }

    @Test
    public void testIsApplicable() {
        MentorshipRequestFilterDto mentorshipRequestFilterDto = prepareDataToDto(DESCRIPTION);
        boolean isApplicable = mentorshipRequestDescriptionFilter.isApplicable(mentorshipRequestFilterDto);
        assertTrue(isApplicable);
    }

    @Test
    public void testApply() {
        MentorshipRequestFilterDto mentorshipRequestFilterDto = prepareDataToDto(DESCRIPTION);
        Stream<MentorshipRequest> requests = prepareStreamOfRequests();
        List<MentorshipRequest> mentorshipRequests = mentorshipRequestDescriptionFilter.apply(requests,
                mentorshipRequestFilterDto).toList();
        assertEquals(5, mentorshipRequests.size());
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
            if (i < 5) {
                mentorshipRequest.setDescription(DESCRIPTION);
            } else {
                mentorshipRequest.setDescription(DESCRIPTION_ANOTHER);
            }
            mentorshipRequestList.add(mentorshipRequest);
        }
        return mentorshipRequestList;
    }

    private MentorshipRequestFilterDto prepareDataToDto(String description) {
        MentorshipRequestFilterDto mentorshipRequestFilterDto = new MentorshipRequestFilterDto();
        mentorshipRequestFilterDto.setDescription(description);
        return mentorshipRequestFilterDto;
    }
}
