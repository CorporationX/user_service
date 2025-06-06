package school.faang.user_service.dto.transaction;

import lombok.Data;

@Data
public class UpdateTransactionDto {
    private String transactionStatus;
    private Integer verificationCode;
    private Long paymentNumber;
    private String message;
}
