package school.faang.user_service.filter.mentorshiprequest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.mentorshiprequest.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class MentorshipRequestStatusFilterTest {

    @InjectMocks
    private MentorshipRequestStatusFilter filterByRequestStatus;

    private static final RequestFilterDto requestFilterDto = new RequestFilterDto();
    private static final RequestStatus STATUS = RequestStatus.ACCEPTED;

    @Test
    @DisplayName("Возвращаем положительный результат")
    void whenValidateIsApplicableThenReturnNotNull() {
        requestFilterDto.setStatus(STATUS);

        assertTrue(filterByRequestStatus.isApplicable(requestFilterDto));
    }

    @Test
    void whenFilterThenReturn() {
        MentorshipRequest firstMenReq = new MentorshipRequest();
        firstMenReq.setStatus(STATUS);

        Stream<MentorshipRequest> menReqs = Stream.of(firstMenReq);

        requestFilterDto.setStatus(STATUS);

        Stream<MentorshipRequest> menReqsNew = filterByRequestStatus.apply(menReqs, requestFilterDto);

        assertNotNull(menReqsNew);
        assertEquals(menReqsNew.count(), 1L);
    }

    @Nested
    class NegativeTest {
        @Test
        @DisplayName("Ничего не возвращаем ")
        void whenValidateIsApplicableThenBrake() {
            requestFilterDto.setStatus(null);

            assertFalse(filterByRequestStatus.isApplicable(requestFilterDto));
        }
    }
}