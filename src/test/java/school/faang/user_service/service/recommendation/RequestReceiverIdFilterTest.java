package school.faang.user_service.service.recommendation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.recommendation.RequestFilterDto;
import school.faang.user_service.entity.User;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class RequestReceiverIdFilterTest {

    @Mock
    private User user;

    @InjectMocks
    private RequestReceiverIdFilter requestReceiverIdFilter;

    @Test
    public void testIsApplicable_ValidInput() {
        RequestFilterDto filterDto = new RequestFilterDto();
        filterDto.setReceiverId(1L);

        boolean result = requestReceiverIdFilter.isApplicable(filterDto);

        assertEquals(true, result);
    }

    @Test
    public void testIsApplicable_NullReceiverId() {
        RequestFilterDto filterDto = new RequestFilterDto();
        filterDto.setReceiverId(null);

        boolean result = requestReceiverIdFilter.isApplicable(filterDto);

        assertEquals(false, result);
    }
}


