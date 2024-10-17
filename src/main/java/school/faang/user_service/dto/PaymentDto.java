package school.faang.user_service.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Positive;

public record PaymentDto(

        @Positive(message = "Invalid user id")
        long userId,

        @Positive(message = "incorrect payment amount")
        Double amount,

        @Positive(message = "incorrect duration")
        Integer duration

) {
}
