package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.client.PaymentServiceClient;
import school.faang.user_service.dto.*;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.entity.promotion.AudienceReach;
import school.faang.user_service.entity.promotion.Promotion;
import school.faang.user_service.exception.AlreadyPurchasedException;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.exception.PaymentFailureException;
import school.faang.user_service.mapper.PromotionMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.repository.promotiom.PromotionRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PromotionServiceTest {

    @InjectMocks
    private PromotionService promotionService;

    @Mock
    private PromotionRepository promotionRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EventRepository eventRepository;

    @Mock
    PromotionMapper promotionMapper;

    @Mock
    PaymentServiceClient paymentServiceClient;

    private long userId;
    private long eventId;
    private User user;
    private Event event;
    private PromotionalPlan promotionalPlan;
    private PromotionDto promotionDto;
    private PaymentRequest paymentRequest;
    private PaymentResponse successPaymentResponse;
    private PaymentResponse errorPaymentResponse;

    private ArgumentCaptor<Promotion> promotionCaptor;

    @BeforeEach
    void setUp() {
        userId = 1L;
        eventId = 1L;
        user = new User();
        event = new Event();
        promotionalPlan = PromotionalPlan.BASIC;
        promotionDto = new PromotionDto(
            null,
            userId,
            eventId,
            0,
            promotionalPlan.getAudienceReach()
        );
        paymentRequest = new PaymentRequest(
            0,
            new BigDecimal(promotionalPlan.getCost()),
            Currency.USD
        );
        successPaymentResponse = new PaymentResponse(
            PaymentStatus.SUCCESS,
            0,
            0,
            new BigDecimal(promotionalPlan.getCost()),
            Currency.USD,
            "Success payment"
        );
        errorPaymentResponse = new PaymentResponse(
            PaymentStatus.ERROR,
            0,
            0,
            new BigDecimal(promotionalPlan.getCost()),
            Currency.USD,
            "Error payment"
        );
        promotionCaptor = ArgumentCaptor.forClass(Promotion.class);
        reset(promotionRepository, userRepository, eventRepository, promotionMapper, paymentServiceClient);
    }

    @Test
    @DisplayName("Test promoting a user with valid parameters")
    void testPromoteUser() {
        when(promotionRepository.existsByUserId(userId)).thenReturn(false);
        when(paymentServiceClient.sendPaymentRequest(paymentRequest)).thenReturn(successPaymentResponse);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(promotionMapper.toDto(promotionCaptor.capture())).thenReturn(promotionDto);

        PromotionDto result = promotionService.promoteUser(userId, promotionalPlan);

        verify(promotionRepository).existsByUserId(userId);
        verify(paymentServiceClient).sendPaymentRequest(paymentRequest);
        verify(userRepository).findById(userId);
        verify(promotionRepository).save(promotionCaptor.getValue());
        verify(promotionMapper).toDto(promotionCaptor.getValue());

        assertNotNull(result);
        assertEquals(result, promotionDto);
    }

    @Test
    @DisplayName("Test promoting a user that has already purchased promotion")
    void testPromoteUserThrowsAlreadyPurchasedException() {
        when(promotionRepository.existsByUserId(userId)).thenReturn(true);

        assertThrows(AlreadyPurchasedException.class, () -> promotionService.promoteUser(userId, promotionalPlan));
    }

    @Test
    @DisplayName("Test promoting a user with payment failure")
    void testPromoteUserThrowsPaymentFailureException() {
        when(promotionRepository.existsByUserId(userId)).thenReturn(false);
        when(paymentServiceClient.sendPaymentRequest(paymentRequest)).thenReturn(errorPaymentResponse);

        assertThrows(PaymentFailureException.class, () -> promotionService.promoteUser(userId, promotionalPlan));
    }

    @Test
    @DisplayName("Test promoting a user that does not exist")
    void testPromoteUserThrowsEntityNotFoundException() {
        when(promotionRepository.existsByUserId(userId)).thenReturn(false);
        when(paymentServiceClient.sendPaymentRequest(paymentRequest)).thenReturn(successPaymentResponse);
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> promotionService.promoteUser(userId, promotionalPlan));
    }

    @Test
    @DisplayName("Test promoting an event with valid parameters")
    void testPromoteEvent() {
        when(promotionRepository.existsByEventId(eventId)).thenReturn(false);
        when(paymentServiceClient.sendPaymentRequest(paymentRequest)).thenReturn(successPaymentResponse);
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(promotionMapper.toDto(promotionCaptor.capture())).thenReturn(promotionDto);

        PromotionDto result = promotionService.promoteEvent(userId, promotionalPlan);

        verify(promotionRepository).existsByEventId(eventId);
        verify(paymentServiceClient).sendPaymentRequest(paymentRequest);
        verify(eventRepository).findById(eventId);
        verify(promotionRepository).save(promotionCaptor.getValue());
        verify(promotionMapper).toDto(promotionCaptor.getValue());

        assertNotNull(result);
        assertEquals(result, promotionDto);
    }

    @Test
    @DisplayName("Test promoting an event that has already purchased promotion")
    void testPromoteEventThrowsAlreadyPurchasedException() {
        when(promotionRepository.existsByEventId(eventId)).thenReturn(true);

        assertThrows(AlreadyPurchasedException.class, () -> promotionService.promoteEvent(userId, promotionalPlan));
    }

    @Test
    @DisplayName("Test promoting an event with payment failure")
    void testPromoteEventThrowsPaymentFailureException() {
        when(promotionRepository.existsByEventId(eventId)).thenReturn(false);
        when(paymentServiceClient.sendPaymentRequest(paymentRequest)).thenReturn(errorPaymentResponse);

        assertThrows(PaymentFailureException.class, () -> promotionService.promoteEvent(userId, promotionalPlan));
    }

    @Test
    @DisplayName("Test promoting an event that does not exist")
    void testPromoteEventThrowsEntityNotFoundException() {
        when(promotionRepository.existsByEventId(eventId)).thenReturn(false);
        when(paymentServiceClient.sendPaymentRequest(paymentRequest)).thenReturn(successPaymentResponse);
        when(eventRepository.findById(eventId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> promotionService.promoteEvent(userId, promotionalPlan));
    }
}