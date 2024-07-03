package school.faang.user_service.service.mentorship.filter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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

    @Nested
    class PositiveTests {

        @DisplayName("should return true when description in requestFilerDto != null")
        @Test
        void applicableTest() {
            assertTrue(descriptionFilter.isApplicable(requestFilterDto));
        }

        @DisplayName("should return 1 when description has the same value in both parameters of apply method")
        @Test
        void applyTest() {
            assertEquals(1, descriptionFilter.apply(mentorshipRequest, requestFilterDto).toList().size());
        }
    }

    @Nested
    class NegativeTests {

        @DisplayName("should return false when description in requestFilerDto == null")
        @Test
        void applicableTest() {
            requestFilterDto.setDescription(null);
            assertFalse(descriptionFilter.isApplicable(requestFilterDto));
        }

        @DisplayName("should return 0 when description hasn't the same value in both parameters of apply method")
        @Test
        void applyTest() {
            requestFilterDto.setDescription("Wrong description");
            assertEquals(0, descriptionFilter.apply(mentorshipRequest, requestFilterDto).toList().size());
        }
    }
}
