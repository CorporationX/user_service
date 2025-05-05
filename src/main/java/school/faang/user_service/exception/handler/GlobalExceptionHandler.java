package school.faang.user_service.exception.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import school.faang.user_service.exception.PaymentFailedException;
import school.faang.user_service.exception.premium.PremiumAlreadyPurchasedException;
import school.faang.user_service.exception.premium.PremiumNotActiveException;
import school.faang.user_service.exception.premium.PremiumPaymentReplyNotReceivedException;
import school.faang.user_service.exception.premium.PremiumPriceReplyNotReceivedException;

import static school.faang.user_service.messages.ErrorMessages.NO_PREMIUM_PAYMENT_RESPONSE_RECEIVED_FROM_PAYMENT_SERVICE;
import static school.faang.user_service.messages.ErrorMessages.NO_PREMIUM_PRICE_RESPONSE_RECEIVED_FROM_PAYMENT_SERVICE;
import static school.faang.user_service.messages.ErrorMessages.THE_PAYMENT_ATTEMPT_WAS_UNSUCCESSFUL;
import static school.faang.user_service.messages.ErrorMessages.THE_USER_DOES_NOT_HAVE_AN_ACTIVE_PREMIUM_SUBSCRIPTION;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(PremiumPriceReplyNotReceivedException.class)
    public ResponseEntity<ErrorResponse> handlePremiumReplyNotReceivedException(
            PremiumPriceReplyNotReceivedException exception) {

        ErrorResponse errorResponse = ErrorResponse.builder(exception, HttpStatus.GATEWAY_TIMEOUT,
                        exception.getMessage())
                .title("Something went wrong with payment service.")
                .detail(NO_PREMIUM_PRICE_RESPONSE_RECEIVED_FROM_PAYMENT_SERVICE)
                .property("service", "payment")
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.GATEWAY_TIMEOUT);
    }

    @ExceptionHandler(PremiumPaymentReplyNotReceivedException.class)
    public ResponseEntity<ErrorResponse> handlePremiumPaymentReplyNotReceivedException(
            PremiumPaymentReplyNotReceivedException exception) {

        ErrorResponse errorResponse = ErrorResponse.builder(exception, HttpStatus.GATEWAY_TIMEOUT, exception.getMessage())
                .title("Something went wrong with payment service.")
                .detail(NO_PREMIUM_PAYMENT_RESPONSE_RECEIVED_FROM_PAYMENT_SERVICE)
                .property("service", "payment")
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.GATEWAY_TIMEOUT);
    }

    @ExceptionHandler(PremiumAlreadyPurchasedException.class)
    public ResponseEntity<ErrorResponse> handlePremiumAlreadyPurchasedException(
            PremiumAlreadyPurchasedException exception) {

        ErrorResponse errorResponse = ErrorResponse.builder(exception, HttpStatus.BAD_REQUEST, exception.getMessage())
                .title("Premium already purchased.")
                .detail("The user has already purchased the premium plan.")
                .property("service", "premium")
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(PremiumNotActiveException.class)
    public ResponseEntity<ErrorResponse> handlePremiumNotActiveException(
            PremiumNotActiveException exception) {

        ErrorResponse errorResponse = ErrorResponse.builder(exception, HttpStatus.BAD_REQUEST, exception.getMessage())
                .title("Premium already purchased.")
                .detail(THE_USER_DOES_NOT_HAVE_AN_ACTIVE_PREMIUM_SUBSCRIPTION)
                .property("service", "premium")
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(PaymentFailedException.class)
    public ResponseEntity<ErrorResponse> handlePaymentFailedException(
            PaymentFailedException exception) {

        ErrorResponse errorResponse = ErrorResponse.builder(exception, HttpStatus.BAD_REQUEST, exception.getMessage())
                .title("Payment failed.")
                .detail(THE_PAYMENT_ATTEMPT_WAS_UNSUCCESSFUL)
                .property("service", "payment")
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}
