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
class MentorshipAuthorRequestFilterTest {

    @InjectMocks
    private MentorshipAuthorRequestFilter filterByAuthorRequest;

    private User receiver = new User();
    private RequestFilterDto requestFilterDto = new RequestFilterDto();
    private static final long RECEIVER_ID = 1L;
    private static final long STREAM_COUNT = 1L;

    @Nested
    class PositiveTest {
        @Test
        @DisplayName("Возвращаем положительный результат")
        void whenValidateIsApplicableThenReturnNotNull() {
            requestFilterDto.setReceiverId(RECEIVER_ID);

            assertTrue(filterByAuthorRequest.isApplicable(requestFilterDto));
        }

        @Test
        @DisplayName("Возвращаем отфильтрованый список")
        void whenFilterThenReturn() {
            receiver.setId(RECEIVER_ID);

            MentorshipRequest firstMenReq = new MentorshipRequest();
            firstMenReq.setReceiver(receiver);

            Stream<MentorshipRequest> menReqs = Stream.of(firstMenReq);

            requestFilterDto.setReceiverId(RECEIVER_ID);

            Stream<MentorshipRequest> menReqsNew = filterByAuthorRequest.apply(menReqs, requestFilterDto);

            assertNotNull(menReqsNew);
            assertEquals(menReqsNew.count(), STREAM_COUNT);
        }

        @Nested
        class NegativeTest {
            @Test
            @DisplayName("Ничего не возвращаем ")
            void whenValidateIsApplicableThenBrake() {
                requestFilterDto.setReceiverId(null);

                assertFalse(filterByAuthorRequest.isApplicable(requestFilterDto));
            }
        }
    }
}