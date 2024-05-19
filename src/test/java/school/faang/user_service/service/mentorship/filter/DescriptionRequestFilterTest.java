package school.faang.user_service.service.mentorship.filter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.recommendation.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.service.mentorship.filtres.DescriptionRequestFilter;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static school.faang.user_service.service.mentorship.filter.MentorshipFilter.DESCRIPTION;

@ExtendWith(MockitoExtension.class)
public class DescriptionRequestFilterTest {
    @InjectMocks
    private DescriptionRequestFilter descriptionRequestFilter;
    private RequestFilterDto requestFilterDto;

    @BeforeEach
    public void init() {
        requestFilterDto = CreatingTestData.createMentorshipRequestDtoForTest(DESCRIPTION);
    }

    @Test
    void isApplicableTestTrue() {
        RequestFilterDto requestFilterDto = RequestFilterDto.builder()
                .description("test").build();
        Assertions.assertTrue(descriptionRequestFilter.isApplicable(requestFilterDto));
    }

    @Test
    void isApplicableTestFalse() {
        RequestFilterDto requestFilterDto = new RequestFilterDto();
        Assertions.assertFalse(descriptionRequestFilter.isApplicable(requestFilterDto));
    }

    @Test
    void applyTest() {
        Stream<MentorshipRequest> mentorshipRequests = CreatingTestData.createMentorshipRequestForTest();

        assertDoesNotThrow(() -> descriptionRequestFilter.apply(mentorshipRequests, requestFilterDto));
    }

    @Test
    public void testApplyNullPointerException() {
        assertThrows(NullPointerException.class, () -> descriptionRequestFilter.apply(null, requestFilterDto));
    }
}
