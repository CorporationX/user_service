package school.faang.user_service.service.mentorship.filter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.recommendation.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.service.mentorship.filtres.StatusRequestFilter;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static school.faang.user_service.service.mentorship.filter.MentorshipFilter.STATUS;

@ExtendWith(MockitoExtension.class)
public class StatusRequestFilterTest {
    @InjectMocks
    private StatusRequestFilter statusRequestFilter;
    private RequestFilterDto requestFilterDto;

    @BeforeEach
    public void init() {
        requestFilterDto = CreatingTestData.createMentorshipRequestDtoForTest(STATUS);
    }

    @Test
    void isApplicableTestTrue() {
        Assertions.assertTrue(statusRequestFilter.isApplicable(requestFilterDto));
    }

    @Test
    void isApplicableTestFalse() {
        RequestFilterDto requestFilterDto = new RequestFilterDto();
        Assertions.assertFalse(statusRequestFilter.isApplicable(requestFilterDto));
    }

    @Test
    void applyTest() {
        Stream<MentorshipRequest> mentorshipRequests = CreatingTestData.createMentorshipRequestForTest();

        assertDoesNotThrow(() -> statusRequestFilter.apply(mentorshipRequests, requestFilterDto));
    }

    @Test
    public void testApplyNullPointerException() {
        assertThrows(NullPointerException.class, () -> statusRequestFilter.apply(null, requestFilterDto));
    }
}
