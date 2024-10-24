package school.faang.user_service.filter.mentorshiprequest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.mentorshiprequest.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class MentorshipDescriptionFilterTest {

    @InjectMocks
    private MentorshipDescriptionFilter filterByDescription;

    private static final RequestFilterDto requestFilterDto = new RequestFilterDto();
    private static final String DESCRIPTION = "Test";

    @Nested
    class PositiveTest {
        @Test
        @DisplayName("Возвращаем положительный результат")
        void whenValidateIsApplicableThenReturnNotNull() {
            requestFilterDto.setDescription(DESCRIPTION);

            assertTrue(filterByDescription.isApplicable(requestFilterDto));
        }

        @Test
        void whenFilterThenReturn() {
            MentorshipRequest firstMenReq = new MentorshipRequest();
            firstMenReq.setDescription(DESCRIPTION);

            Stream<MentorshipRequest> menReqs = Stream.of(firstMenReq);

            requestFilterDto.setDescription(DESCRIPTION);

            Stream<MentorshipRequest> menReqsNew = filterByDescription.apply(menReqs, requestFilterDto);

            assertNotNull(menReqsNew);
            assertEquals(menReqsNew.count(), 1L);
        }
    }

    @Nested
    class NegativeTest {
        @Test
        @DisplayName("Ничего не возвращаем ")
        void whenValidateIsApplicableThenBrake() {
            requestFilterDto.setDescription(null);

            assertFalse(filterByDescription.isApplicable(requestFilterDto));
        }
    }
}