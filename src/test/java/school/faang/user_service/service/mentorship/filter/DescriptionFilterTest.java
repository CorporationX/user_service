package school.faang.user_service.service.mentorship.filter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.mentorship.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class DescriptionFilterTest {

    @InjectMocks
    private DescriptionFilter descriptionFilter;

    private RequestFilterDto requestFilterDto;

    private MentorshipRequest mentorshipRequest;

    @BeforeEach
    void prepareRequestFilterDto() {
        requestFilterDto = new RequestFilterDto();
        requestFilterDto.setDescription("Some description");
        mentorshipRequest = new MentorshipRequest();
        mentorshipRequest.setDescription("Some description");
    }

    @Test
    void testIsApplicableFalse() {
        requestFilterDto.setDescription(null);
        assertFalse(descriptionFilter.isApplicable(requestFilterDto));
    }

    @Test
    void testIsApplicableTrue() {
        assertTrue(descriptionFilter.isApplicable(requestFilterDto));
    }

    @Test
    void testApplyWithRequest() {
        assertEquals(1, descriptionFilter.apply(mentorshipRequest, requestFilterDto).toList().size());
    }

    @Test
    void testApplyWithoutRequest() {
        requestFilterDto.setDescription("Wrong description");
        assertEquals(0, descriptionFilter.apply(mentorshipRequest, requestFilterDto).toList().size());
    }
}
