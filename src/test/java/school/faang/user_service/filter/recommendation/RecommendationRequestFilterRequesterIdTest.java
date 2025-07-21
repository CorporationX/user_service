package school.faang.user_service.filter.recommendation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.recommendation.RecommendationRequestFilterDto;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
public class RecommendationRequestFilterRequesterIdTest {


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
}
