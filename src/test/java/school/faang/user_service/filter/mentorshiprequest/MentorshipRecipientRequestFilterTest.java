package school.faang.user_service.filter.mentorshiprequest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.mentorshiprequest.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.User;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class MentorshipRecipientRequestFilterTest {

    @InjectMocks
    private MentorshipRecipientRequestFilter filterByRecipientRequest;

    private static final RequestFilterDto requestFilterDto = new RequestFilterDto();
    private static final User requester = new User();
    private static final long REQUESTER_ID = 1L;
    private static final long STREAM_COUNT = 1L;

    @Test
    @DisplayName("Возвращаем положительный результат")
    void whenValidateIsApplicableThenReturnNotNull() {
        requestFilterDto.setRequesterId(REQUESTER_ID);

        assertTrue(filterByRecipientRequest.isApplicable(requestFilterDto));
    }

    @Test
    void whenFilterThenReturn() {
        requester.setId(REQUESTER_ID);

        MentorshipRequest firstMenReq = new MentorshipRequest();
        firstMenReq.setRequester(requester);

        Stream<MentorshipRequest> menReqs = Stream.of(firstMenReq);

        requestFilterDto.setRequesterId(REQUESTER_ID);

        Stream<MentorshipRequest> menReqsNew = filterByRecipientRequest.apply(menReqs, requestFilterDto);

        assertNotNull(menReqsNew);
        assertEquals(menReqsNew.count(), STREAM_COUNT);
    }

    @Nested
    class NegativeTest {
        @Test
        @DisplayName("Ничего не возвращаем ")
        void whenValidateIsApplicableThenBrake() {
            requestFilterDto.setRequesterId(null);

            assertFalse(filterByRecipientRequest.isApplicable(requestFilterDto));
        }
    }
}