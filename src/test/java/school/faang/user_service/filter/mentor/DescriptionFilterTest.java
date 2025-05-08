package school.faang.user_service.filter.mentor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.mentor.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import java.util.List;



class DescriptionFilterTest {
    private final DescriptionFilter descriptionFilter = new DescriptionFilter();
    private RequestFilterDto requestFilterDto;

    @BeforeEach
    void setUp() {
        requestFilterDto = new RequestFilterDto();
    }

    //Positive
    @Test
    void testIsApplicable() {
        requestFilterDto.setDescription("Text");

        assertTrue(descriptionFilter.isApplicable(requestFilterDto));
    }

    @Test
    void testApplyFilter() {
        requestFilterDto.setDescription("Java");
        MentorshipRequest request1 = new MentorshipRequest();
        MentorshipRequest request2 = new MentorshipRequest();
        request1.setDescription("Java");
        request2.setDescription("C#");
        List<MentorshipRequest> requests = List.of(request1, request2);

        List<MentorshipRequest> result = descriptionFilter.apply(requests.stream(), requestFilterDto).toList();

        assertEquals(1, result.size());
        assertEquals("Java", result.get(0).getDescription());
    }

    @Test
    void testApplyCheckRegister() {
        requestFilterDto.setDescription("jAvA");
        MentorshipRequest request1 = new MentorshipRequest();
        MentorshipRequest request2 = new MentorshipRequest();
        request1.setDescription("Java");
        request2.setDescription("java");
        List<MentorshipRequest> requests = List.of(request1, request2);

        List<MentorshipRequest> result = descriptionFilter.apply(requests.stream(), requestFilterDto).toList();

        assertEquals(2, result.size());
        assertEquals("java", result.get(0).getDescription().toLowerCase());
        assertEquals("java", result.get(1).getDescription().toLowerCase());
    }

    //Negative
    @Test
    void testIsApplicableNullDescription() {
        requestFilterDto.setDescription(null);

        assertFalse(descriptionFilter.isApplicable(requestFilterDto));
    }

    @Test
    void testIsApplicableBlankDescription() {
        requestFilterDto.setDescription("");

        assertFalse(descriptionFilter.isApplicable(requestFilterDto));
    }

    @Test
    void testApplyEmptyListWhenNoMatches() {
        requestFilterDto.setDescription("Java");
        MentorshipRequest request1 = new MentorshipRequest();
        request1.setDescription("C# Developer");
        List<MentorshipRequest> requests = List.of(request1);

        List<MentorshipRequest> result = descriptionFilter.apply(requests.stream(), requestFilterDto).toList();

        assertTrue(result.isEmpty());
    }
}