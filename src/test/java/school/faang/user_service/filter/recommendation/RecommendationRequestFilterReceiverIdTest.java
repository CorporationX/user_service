package school.faang.user_service.filter.recommendation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.recommendation.RecommendationRequestFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.user.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
public class RecommendationRequestFilterReceiverIdTest {
    @Test
    public void testIsApplicableTrue() {
        RecommendationRequestFilterDto dto =
                new RecommendationRequestFilterDto(1L, 2L, null, null);
        assertNotNull(dto.receiverId());
    }

    @Test
    public void testIsApplicableFalse() {
        RecommendationRequestFilterDto dto =
                new RecommendationRequestFilterDto(null, null, null, null);
        assertNull(dto.receiverId());
    }

    @Test
    public void testApply() {
        User oneU = new User();
        User twoU = new User();
        oneU.setId(1L);
        twoU.setId(2L);
        RecommendationRequest one = new RecommendationRequest(1L, oneU, twoU, "Hello",
                RequestStatus.ACCEPTED, null, null, null,
                LocalDateTime.of(2025, 7, 9, 22, 0),
                LocalDateTime.of(2025, 7, 9, 23, 0));
        RecommendationRequest two = new RecommendationRequest(2L, twoU, new User(), "Hello",
                RequestStatus.ACCEPTED, null, null, null,
                LocalDateTime.of(2025, 7, 9, 22, 0),
                LocalDateTime.of(2025, 7, 9, 23, 0));
        Stream<RecommendationRequest> recommendationRequestStream = Stream.of(one, two);
        RecommendationRequestFilterDto recommendationRequestFilterDto =
                new RecommendationRequestFilterDto(1L, 2L, null, null);

        List<RecommendationRequest> list = recommendationRequestStream
                .filter(recommendationRequest ->
                        recommendationRequestFilterDto.receiverId()
                                .equals(recommendationRequest.getReceiver().getId()))
                .toList();
        List<RecommendationRequest> result = List.of(new RecommendationRequest(1L, oneU, twoU, "Hello",
                RequestStatus.ACCEPTED, null, null, null,
                LocalDateTime.of(2025, 7, 9, 22, 0),
                LocalDateTime.of(2025, 7, 9, 23, 0)));

        assertEquals(list, result);
    }
}
