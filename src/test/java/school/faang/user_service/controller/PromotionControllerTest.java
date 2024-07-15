package school.faang.user_service.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.promotion.PromotionDto;
import school.faang.user_service.entity.promotion.PromotionalPlan;
import school.faang.user_service.service.PromotionService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PromotionControllerTest {

    @InjectMocks
    private PromotionController promotionController;

    @Mock
    private PromotionService promotionService;

    private long userId;
    private long eventId;
    private String validPromotionalPlanName;
    private String invalidPromotionalPlanName;
    private PromotionDto promotionDto;

    @BeforeEach
    void setUp() {
        userId = 1L;
        eventId = 1L;
        validPromotionalPlanName = "basic";
        invalidPromotionalPlanName = "";
        promotionDto = new PromotionDto(null, userId, eventId, PromotionalPlan.BASIC, 10000);
        reset(promotionService);
    }

    @Test
    @DisplayName("Test promoting a user with valid parameters")
    void testPromoteUser() {
        when(promotionService.promoteUser(userId, PromotionalPlan.getFromName(validPromotionalPlanName))).thenReturn(promotionDto);

        PromotionDto result = promotionController.promoteUser(userId, validPromotionalPlanName);

        verify(promotionService).promoteUser(userId, PromotionalPlan.getFromName(validPromotionalPlanName));

        assertNotNull(result);
        assertEquals(promotionDto, result);
    }

    @Test
    void testPromoteUserInvalidName() {
        assertThrows(IllegalArgumentException.class, () -> promotionController.promoteUser(userId, invalidPromotionalPlanName));
    }

    @Test
    @DisplayName("Test promoting an event with valid parameters")
    void testPromoteEvent() {
        when(promotionService.promoteEvent(eventId, PromotionalPlan.getFromName(validPromotionalPlanName))).thenReturn(promotionDto);

        PromotionDto result = promotionController.promoteEvent(eventId, validPromotionalPlanName);

        verify(promotionService).promoteEvent(eventId, PromotionalPlan.getFromName(validPromotionalPlanName));

        assertNotNull(result);
        assertEquals(promotionDto, result);
    }

    @Test
    void testPromoteEventInvalidName() {
        assertThrows(IllegalArgumentException.class, () -> promotionController.promoteEvent(eventId, invalidPromotionalPlanName));
    }
}
