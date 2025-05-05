package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import school.faang.user_service.dto.payment.CurrencyDto;
import school.faang.user_service.dto.payment.PaymentRequestDto;
import school.faang.user_service.dto.payment.PaymentResponseDto;
import school.faang.user_service.dto.payment.PaymentStatus;
import school.faang.user_service.dto.promotion.user.UserDto;
import school.faang.user_service.dto.promotion.user.UserPromotionDto;
import school.faang.user_service.entity.promotion.user.UserPromotion;
import school.faang.user_service.entity.promotion.user.UserPromotionCount;
import school.faang.user_service.enums.promotion.PromotionPriority;
import school.faang.user_service.enums.promotion.user.UserPromotionPricing;
import school.faang.user_service.enums.promotion.user.UserPromotionType;
import school.faang.user_service.exception.promotion.DuplicatePromotionException;
import school.faang.user_service.exception.promotion.PromotionNotFoundException;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.promotion.UserPromotionCountRepository;
import school.faang.user_service.repository.promotion.UserPromotionRepository;
import school.faang.user_service.utils.validationUtils.PromotionValidation;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserPromotionService {
    public static final String USER_DTO_CANNOT_BE_NULL = "User DTO can't be null";
    public static final String DUPLICATE_USER_PROMOTION_MESSAGE = "User promotion for userId=%d with " +
            "startDate=%s, endDate=%s already exists in DB";
    public static final String NO_USER_PROMOTION_FOUND = "No promotion found for userId=%d, startDate=%s," +
            " endDate=%s, userPercentage=%d and feedRank=%d";
    public static final String CANT_UPDATE_USER_PROMOTION_TYPE = "Can't update userPromotionType for userId=%d, " +
            "startDate=%s, endDate=%s and promotionPriority=%s.";
    public static final String CANT_UPDATE_USER_PROMOTION_PRIORITY = "Can't update promotionPriority for " +
            "userId=%d, startDate=%s, endDate=%s and userPromotionType=%s.";
    public static final String PAYMENT_FAILED_FOR_USER = "Payment failed for user with ID: {}";
    public static final String PAYMENT_SUCCESSFUL_FOR_USER = "Payment successful for user with ID: {}";
    public static final String CALCULATED_PRICE_DIFFERENCE_FOR_USER = "Calculated priceDifference for userID: {} is: {}";
    private static final String VALIDATING_USER_PROMOTION = "Validating user promotion with id={}";

    private static final BigDecimal USER_PROMOTION_PRICE_DECREASE = new BigDecimal("0.03");
    private static final BigDecimal USER_PROMOTION_PRICE_PER_MINUTE = new BigDecimal("5");
    private static final BigDecimal MAX_DISCOUNT = new BigDecimal("0.20");
    private static final BigDecimal DISCOUNT_THRESHOLD = new BigDecimal("50000");
    private static final BigDecimal SECONDS_IN_MINUTE = new BigDecimal("60");

    private final RestTemplate restTemplate;
    private final UserPromotionRepository userPromotionRepository;
    private final UserPromotionCountRepository userPromotionCountRepository;
    private final UserRepository userRepository;
    @Value("${payment.api.url}")
    private String paymentApiUrl;

    public ResponseEntity<String> processStartUserPromotion(
            UserDto userDto, UserPromotionDto promotionRequestDto, CurrencyDto currencyDto) {

        LocalDateTime startDate = promotionRequestDto.startDate();
        LocalDateTime endDate = promotionRequestDto.endDate();
        UserPromotionType promotionType = promotionRequestDto.userPromotionType();
        PromotionPriority promotionPriority = promotionRequestDto.promotionPriority();

        validateUserDto(userDto);
        log.info(VALIDATING_USER_PROMOTION, userDto.userId());
        PromotionValidation.validateUserPromotion(userDto.userId(), startDate, endDate,
                promotionType, promotionPriority);
        BigDecimal promotionPrice = calculateUserPromotionPrice(userDto.userId(),
                startDate, endDate, promotionType.getUserPercentage(), promotionPriority.getFeedRank());
        log.info("Calculated promotion price for userID: {} is: {}", userDto.userId(), promotionPrice);

        PaymentResponseDto paymentResponse = processPayment(userDto.userId(), promotionPrice, currencyDto);
        if (paymentResponse == null || paymentResponse.status() != PaymentStatus.SUCCESS) {
            log.error(PAYMENT_FAILED_FOR_USER, userDto.userId());
            return ResponseEntity.status(HttpStatus.PAYMENT_REQUIRED).body("Payment failed for user promotion");
        }
        log.info(PAYMENT_SUCCESSFUL_FOR_USER, userDto.userId());
        updateUserPromotionCount(userDto.userId(), promotionPrice);

        try {
            startUserPromotion(userDto, promotionRequestDto);
        } catch (DuplicatePromotionException ex) {
            log.error("Error starting user promotion for userID: {}: {}", userDto.userId(), ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
        return ResponseEntity.ok("User promotion started successfully");
    }

    public ResponseEntity<String> processEndUserPromotion(
            UserDto userDto, UserPromotionDto promotionRequestDto) {

        LocalDateTime startDate = promotionRequestDto.startDate();
        LocalDateTime endDate = promotionRequestDto.endDate();
        UserPromotionType promotionType = promotionRequestDto.userPromotionType();
        PromotionPriority promotionPriority = promotionRequestDto.promotionPriority();

        validateUserDto(userDto);
        log.info(VALIDATING_USER_PROMOTION, userDto.userId());
        PromotionValidation.validateUserPromotion(userDto.userId(), startDate,
                endDate, promotionType, promotionPriority);
        try {
            endUserPromotion(userDto, promotionRequestDto);
        } catch (PromotionNotFoundException ex) {
            log.error("Error ending user promotion. userID: {}: {}", userDto.userId(), ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
        return ResponseEntity.ok("User promotion ended successfully");
    }

    public ResponseEntity<String> processUpdateUserPromotionPriority(
            UserDto userDto, UserPromotionDto promotionRequestDto, CurrencyDto currencyDto) {

        LocalDateTime startDate = promotionRequestDto.startDate();
        LocalDateTime endDate = promotionRequestDto.endDate();
        UserPromotionType promotionType = promotionRequestDto.userPromotionType();
        PromotionPriority newPromotionPriority = promotionRequestDto.promotionPriority();

        validateUserDto(userDto);
        log.info(VALIDATING_USER_PROMOTION, userDto.userId());
        PromotionValidation.validateUserPromotion(userDto.userId(), startDate,
                endDate, promotionType, newPromotionPriority);
        BigDecimal priceDifference = calculateUserPromotionPriceDifferenceOnPriorityChange(
                userDto.userId(), promotionRequestDto);

        log.info(CALCULATED_PRICE_DIFFERENCE_FOR_USER, userDto.userId(), priceDifference);
        if (priceDifference.compareTo(BigDecimal.ZERO) > 0) {
            PaymentResponseDto paymentResponse = processPayment(userDto.userId(), priceDifference, currencyDto);
            if (paymentResponse == null || paymentResponse.status() != PaymentStatus.SUCCESS) {
                log.error(PAYMENT_FAILED_FOR_USER, userDto.userId());
                return ResponseEntity.status(HttpStatus.PAYMENT_REQUIRED).body("Payment failed for promotion priority update");
            }
            log.info(PAYMENT_SUCCESSFUL_FOR_USER, userDto.userId());
        }
        userPromotionRepository.updateUserFeedRank(userDto.userId(), startDate, endDate,
                promotionType.getUserPercentage(), newPromotionPriority.getFeedRank());
        return ResponseEntity.ok("User promotion priority updated successfully");
    }

    public ResponseEntity<String> processUpdateUserPromotionType(
            UserDto userDto, UserPromotionDto promotionRequestDto, CurrencyDto currencyDto) {

        LocalDateTime startDate = promotionRequestDto.startDate();
        LocalDateTime endDate = promotionRequestDto.endDate();
        UserPromotionType newPromotionType = promotionRequestDto.userPromotionType();
        PromotionPriority promotionPriority = promotionRequestDto.promotionPriority();

        validateUserDto(userDto);
        log.info(VALIDATING_USER_PROMOTION, userDto.userId());
        PromotionValidation.validateUserPromotion(userDto.userId(), startDate,
                endDate, newPromotionType, promotionPriority);
        BigDecimal priceDifference = calculateUserPromotionPriceDifferenceOnTypeChange(
                userDto.userId(), promotionRequestDto);

        log.info(CALCULATED_PRICE_DIFFERENCE_FOR_USER, userDto.userId(), priceDifference);
        if (priceDifference.compareTo(BigDecimal.ZERO) > 0) {
            PaymentResponseDto paymentResponse = processPayment(userDto.userId(), priceDifference, currencyDto);
            if (paymentResponse == null || paymentResponse.status() != PaymentStatus.SUCCESS) {
                log.error(PAYMENT_FAILED_FOR_USER, userDto.userId());
                return ResponseEntity.status(HttpStatus.PAYMENT_REQUIRED).body("Payment failed for promotion type update");
            }
            log.info(PAYMENT_SUCCESSFUL_FOR_USER, userDto.userId());
        }
        userPromotionRepository.updateUserPromotionPercentage(userDto.userId(), startDate, endDate,
                newPromotionType.getUserPercentage(), promotionPriority.getFeedRank());
        return ResponseEntity.ok("User promotion type updated successfully");
    }

    private void startUserPromotion(UserDto userDto, UserPromotionDto promotionRequestDto) {
        LocalDateTime startDate = promotionRequestDto.startDate();
        LocalDateTime endDate = promotionRequestDto.endDate();
        UserPromotionType promotionType = promotionRequestDto.userPromotionType();
        PromotionPriority promotionPriority = promotionRequestDto.promotionPriority();

        int promotionViewsPercentage = promotionType.getUserPercentage();
        int feedRank = promotionPriority.getFeedRank();
        UserPromotion userPromotion = userPromotionRepository.findPromotionByUserIdStartDateEndDate(
                userDto.userId(), startDate, endDate);
        if (userPromotion != null) {
            String message = String.format(DUPLICATE_USER_PROMOTION_MESSAGE, userDto.userId(), startDate, endDate);
            log.error(message);
            throw new DuplicatePromotionException(message);
        }

        UserPromotion userPromotionToSave = new UserPromotion();
        userPromotionToSave.setUser(userRepository.findById(userDto.userId()).get());
        userPromotionToSave.setPercentage(promotionViewsPercentage);
        userPromotionToSave.setStartDate(startDate);
        userPromotionToSave.setEndDate(endDate);
        userPromotionToSave.setFeedRank(feedRank);
        userPromotionRepository.save(userPromotionToSave);
    }

    private void endUserPromotion(UserDto userDto, UserPromotionDto promotionRequestDto) {
        LocalDateTime startDate = promotionRequestDto.startDate();
        LocalDateTime endDate = promotionRequestDto.endDate();
        UserPromotionType promotionType = promotionRequestDto.userPromotionType();
        PromotionPriority promotionPriority = promotionRequestDto.promotionPriority();

        int promotionViewsPercentage = promotionType.getUserPercentage();
        int feedRank = promotionPriority.getFeedRank();
        UserPromotion userPromotion = userPromotionRepository.findSamePromotion(
                userDto.userId(), startDate, endDate, promotionViewsPercentage, feedRank);

        if (userPromotion == null) {
            String message = String.format(NO_USER_PROMOTION_FOUND,
                    userDto.userId(), startDate, endDate, promotionViewsPercentage, feedRank);
            log.error(message);
            throw new PromotionNotFoundException(message);
        }
        userPromotionRepository.delete(userPromotion);
    }


    private BigDecimal calculateUserPromotionPrice(Long userId, LocalDateTime startDate, LocalDateTime endDate,
                                                   int userPercentage, int feedRank) {

        long seconds = Duration.between(startDate, endDate).toSeconds();
        BigDecimal cost = USER_PROMOTION_PRICE_PER_MINUTE.multiply(
                BigDecimal.valueOf(seconds).divide(SECONDS_IN_MINUTE, RoundingMode.CEILING));

        cost = cost.add(UserPromotionPricing.getPrice(userPercentage, feedRank));
        Integer discountCount = userPromotionCountRepository.findCountByUserId(userId);
        if (discountCount == null) {
            discountCount = 0;
        }
        BigDecimal discountPercentage = MAX_DISCOUNT.min(USER_PROMOTION_PRICE_DECREASE.multiply(
                BigDecimal.valueOf(discountCount)));
        return cost.multiply(BigDecimal.valueOf(1.00).subtract(discountPercentage));
    }

    private BigDecimal calculateUserPromotionPriceDifferenceOnTypeChange(
            Long userId, UserPromotionDto promotionRequestDto) {

        LocalDateTime startDate = promotionRequestDto.startDate();
        LocalDateTime endDate = promotionRequestDto.endDate();
        UserPromotionType newPromotionType = promotionRequestDto.userPromotionType();
        PromotionPriority promotionPriority = promotionRequestDto.promotionPriority();

        Integer oldUserPercentage = userPromotionRepository.getUserPercentage(userId, startDate, endDate,
                promotionPriority.getFeedRank());
        if (oldUserPercentage == null) {
            String message = String.format(CANT_UPDATE_USER_PROMOTION_TYPE + ". No such promotion exists",
                    userId, startDate, endDate, promotionPriority);
            log.error(message);
            throw new PromotionNotFoundException(message);
        } else if (oldUserPercentage == newPromotionType.getUserPercentage()) {
            String message = String.format(CANT_UPDATE_USER_PROMOTION_TYPE + ". Such promotion already exists",
                    userId, startDate, endDate, promotionPriority);
            log.error(message);
            throw new IllegalArgumentException(message);
        }
        return calculateUserPromotionPrice(userId, startDate, endDate,
                newPromotionType.getUserPercentage(), promotionPriority.getFeedRank())
                .subtract(calculateUserPromotionPrice(userId, startDate, endDate,
                        oldUserPercentage, promotionPriority.getFeedRank()));
    }

    private BigDecimal calculateUserPromotionPriceDifferenceOnPriorityChange(
            Long userId, UserPromotionDto promotionRequestDto) {

        LocalDateTime startDate = promotionRequestDto.startDate();
        LocalDateTime endDate = promotionRequestDto.endDate();
        UserPromotionType promotionType = promotionRequestDto.userPromotionType();
        PromotionPriority newPromotionPriority = promotionRequestDto.promotionPriority();

        Integer oldFeedRank = userPromotionRepository.getUserFeedRank(userId, startDate, endDate,
                promotionType.getUserPercentage());
        if (oldFeedRank == null) {
            String message = String.format(CANT_UPDATE_USER_PROMOTION_PRIORITY + ". No such promotion exists",
                    userId, startDate, endDate, promotionType);
            log.error(message);
            throw new PromotionNotFoundException(message);
        } else if (oldFeedRank == newPromotionPriority.getFeedRank()) {
            String message = String.format(CANT_UPDATE_USER_PROMOTION_PRIORITY + ". Such promotion already exists",
                    userId, startDate, endDate, promotionType);
            log.error(message);
            throw new IllegalArgumentException(message);
        }
        return calculateUserPromotionPrice(userId, startDate, endDate,
                promotionType.getUserPercentage(), newPromotionPriority.getFeedRank())
                .subtract(calculateUserPromotionPrice(userId, startDate, endDate,
                        promotionType.getUserPercentage(), oldFeedRank));
    }

    private PaymentResponseDto processPayment(Long entityId, BigDecimal amount, CurrencyDto currencyDto) {
        PaymentRequestDto paymentRequest = new PaymentRequestDto(entityId, amount, currencyDto);
        log.info("Initiating payment request: {}", paymentRequest);
        return restTemplate.postForObject(paymentApiUrl, paymentRequest, PaymentResponseDto.class);
    }

    private void updateUserPromotionCount(long userId, BigDecimal cost) {
        Integer discountCountOld = userPromotionCountRepository.findCountByUserId(userId);
        int discountCountNew = cost.divide(DISCOUNT_THRESHOLD, RoundingMode.FLOOR).intValue();
        if (discountCountOld == null) {
            UserPromotionCount userPromotionCount = new UserPromotionCount();
            userPromotionCount.setCount(discountCountNew);
            userPromotionCount.setUserId(userId);
            userPromotionCountRepository.save(userPromotionCount);
            log.info("Saved new user promotion count. userId: {} with count: {}", userId, discountCountNew);
        } else {
            userPromotionCountRepository.incrementCountByUserId(userId, discountCountNew);
            log.info("Incremented user promotion count. userId: {} by: {}", userId, discountCountNew);
        }
    }

    private void validateUserDto(UserDto userDto) {
        if (userDto == null) {
            log.error(USER_DTO_CANNOT_BE_NULL);
            throw new IllegalArgumentException(USER_DTO_CANNOT_BE_NULL);
        }
    }
}
