package school.faang.user_service.filter.mentorship;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.mentorship.MentorshipFilterDto;
import school.faang.user_service.entity.MentorshipRequest;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class DescriptionFilterTest {
    private final DescriptionFilter descriptionFilter = new DescriptionFilter();

    @Test
    @DisplayName("Description is good")
    public void testIsApplicableTrue() {
        boolean result = descriptionFilter.isApplicable(new MentorshipFilterDto("Test description", null, null, null));
        assertTrue(result);
    }

    @Test
    @DisplayName("Description is empty")
    public void testIsApplicableFalseWhenDescriptionIsEmpty() {
        boolean result = descriptionFilter.isApplicable(new MentorshipFilterDto("", null, null, null));
        assertFalse(result);
    }

    @Test
    @DisplayName("Description is blank")
    public void testIsApplicableFalseWhenDescriptionIsBlank() {
        boolean result = descriptionFilter.isApplicable(new MentorshipFilterDto("    ", null, null, null));
        assertFalse(result);
    }

    @Test
    @DisplayName("Description is null")
    public void testIsApplicableFalse() {
        boolean result = descriptionFilter.isApplicable(new MentorshipFilterDto(null, null, null, null));
        assertFalse(result);
    }

    @Test
    @DisplayName("Apply Description filter")
    void apply_shouldFilterRequests() {
        MentorshipRequest r1 = MentorshipRequest.builder().description("Test").build();
        MentorshipRequest r2 = MentorshipRequest.builder().description("Request").build();

        MentorshipFilterDto dto = new MentorshipFilterDto("Test", null, null, null);

        List<MentorshipRequest> result = descriptionFilter.apply(Stream.of(r1, r2), dto).toList();
        assertEquals(1, result.size());
        assertEquals("Test", result.get(0).getDescription());
    }
}