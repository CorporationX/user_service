package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.client.PaymentServiceClient;
import school.faang.user_service.dto.*;
import school.faang.user_service.dto.promotion.PromotionDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.promotion.Promotion;
import school.faang.user_service.entity.promotion.PromotionalPlan;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.exception.IllegalEntityException;
import school.faang.user_service.mapper.PromotionMapper;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.repository.promotiom.PromotionRepository;
import school.faang.user_service.validator.PaymentValidator;
import school.faang.user_service.validator.PromotionValidator;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
    UserMapper userMapper;

    @Mock
    PromotionValidator promotionValidator;

    @Mock
    PaymentValidator paymentValidator;

    @Mock
    PaymentServiceClient paymentServiceClient;

    private long userId;
    private long eventId;
    private String validPromotionalPlanName;
    private String invalidPromotionalPlanName;
    private String validCurrencyName;
    private String invalidCurrencyName;
    private User user;
    private Event event;
    private PromotionDto promotionDto;
    private PaymentResponse paymentResponse;
    private List<User> regularUsers;
    private List<User> promotedUsers;
    private List<User> combinedUsers;
    private List<UserDto> usersDto;

    @BeforeEach
    void setUp() {
        userId = 0;
        eventId = 0;
        validPromotionalPlanName = "basic";
        invalidPromotionalPlanName = "";
        validCurrencyName = "usd";
        invalidCurrencyName = "";
        user = new User();
        event = new Event();
        promotionDto = new PromotionDto(
            null,
            userId,
            eventId,
            PromotionalPlan.BASIC,
            0
        );
        paymentResponse = new PaymentResponse(
            PaymentStatus.SUCCESS,
            0,
            0,
            0,
            Currency.USD,
            "Success payment"
        );
        Promotion promotion = Promotion.builder().impressions(1).build();
        User firstUser = User.builder().id(1).promotion(promotion).build();
        User secondUser = User.builder().id(2).promotion(promotion).build();
        User thirdUser = User.builder().id(3).promotion(promotion).build();
        regularUsers = List.of(
            firstUser,
            secondUser,
            thirdUser
        );
        promotedUsers = List.of(
            secondUser,
            thirdUser
        );
        combinedUsers = List.of(
            secondUser,
            thirdUser,
            firstUser
        );
        UserDto firstUserDto = UserDto.builder().id(1L).build();
        UserDto secondUserDto = UserDto.builder().id(2L).build();
        UserDto thirdUserDto = UserDto.builder().id(3L).build();
        usersDto = List.of(
            secondUserDto,
            thirdUserDto,
            firstUserDto
        );
    }

    @Test
    @DisplayName("Test promoting an user with valid parameters")
    void testPromoteUser() {
        when(paymentServiceClient.sendPaymentRequest(any(PaymentRequest.class))).thenReturn(paymentResponse);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(promotionMapper.toDto(any(Promotion.class))).thenReturn(promotionDto);

        PromotionDto result = promotionService.promoteUser(userId, validPromotionalPlanName, validCurrencyName);

        verify(promotionValidator).validateUserAlreadyHasPromotion(anyLong());
        verify(paymentServiceClient).sendPaymentRequest(any(PaymentRequest.class));
        verify(paymentValidator).validatePaymentSuccess(any(PaymentResponse.class));
        verify(userRepository).findById(anyLong());
        verify(promotionRepository).save(any(Promotion.class));
        verify(userRepository).save(any(User.class));
        verify(promotionMapper).toDto(any(Promotion.class));

        assertNotNull(result);
        assertEquals(promotionDto, result);
    }

    @Test
    void testPromoteUserUserNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> promotionService.promoteUser(userId, validPromotionalPlanName, validCurrencyName));
    }

    @Test
    void testPromoteUsersInvalidPromotionalPlanName() {
        assertThrows(IllegalEntityException.class, () -> promotionService.promoteUser(userId, invalidPromotionalPlanName, validCurrencyName));
    }

    @Test
    void testPromoteUsersInvalidCurrencyName() {
        assertThrows(IllegalEntityException.class, () -> promotionService.promoteUser(userId, validPromotionalPlanName, invalidCurrencyName));
    }

    @Test
    @DisplayName("Test promoting an event with valid parameters")
    void testPromoteEvent() {
        when(paymentServiceClient.sendPaymentRequest(any(PaymentRequest.class))).thenReturn(paymentResponse);
        when(eventRepository.findById(anyLong())).thenReturn(Optional.of(event));
        when(promotionMapper.toDto(any(Promotion.class))).thenReturn(promotionDto);

        PromotionDto result = promotionService.promoteEvent(eventId, validPromotionalPlanName, validCurrencyName);

        verify(promotionValidator).validateEventAlreadyHasPromotion(anyLong());
        verify(paymentServiceClient).sendPaymentRequest(any(PaymentRequest.class));
        verify(paymentValidator).validatePaymentSuccess(any(PaymentResponse.class));
        verify(eventRepository).findById(anyLong());
        verify(promotionRepository).save(any(Promotion.class));
        verify(eventRepository).save(any(Event.class));
        verify(promotionMapper).toDto(any(Promotion.class));

        assertNotNull(result);
        assertEquals(promotionDto, result);
    }

    @Test
    void testPromoteEventEventNotFound() {
        when(eventRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> promotionService.promoteEvent(userId, validPromotionalPlanName, validCurrencyName));
    }

    @Test
    void testPromoteEventInvalidPromotionalPlanName() {
        assertThrows(IllegalEntityException.class, () -> promotionService.promoteEvent(userId, invalidPromotionalPlanName, validCurrencyName));
    }

    @Test
    void testPromoteEventInvalidCurrencyName() {
        assertThrows(IllegalEntityException.class, () -> promotionService.promoteEvent(userId, validPromotionalPlanName, invalidCurrencyName));
    }

    @Test
    void testShowPromotedUsersFirst() {
        when(userRepository.findAll()).thenReturn(regularUsers);
        when(userRepository.findPromotedUsers()).thenReturn(promotedUsers);
        when(userMapper.usersToUserDTOs(anyList())).thenReturn(usersDto);

        List<UserDto> result = promotionService.showPromotedUsersFirst();

        verify(userRepository).findAll();
        verify(userRepository).findPromotedUsers();
        verify(userMapper).usersToUserDTOs(combinedUsers);

        assertNotNull(result);
        assertEquals(usersDto, result);
    }
}