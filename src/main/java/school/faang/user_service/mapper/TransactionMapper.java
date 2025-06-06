package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import school.faang.user_service.dto.payment.PaymentRequestDto;
import school.faang.user_service.dto.payment.PaymentResponseDto;
import school.faang.user_service.dto.transaction.TransactionResultDto;
import school.faang.user_service.dto.transaction.UpdateTransactionDto;
import school.faang.user_service.entity.transaction.Transaction;

@Mapper(componentModel = "spring")
public interface TransactionMapper {
    @Mapping(target = "currency", source = "currencyCode")
    @Mapping(target = "paymentNumber", source = "transactionNumber")
    PaymentRequestDto toPaymentRequestDto(Transaction transaction);

    @Mapping(target = "verificationCode", source = "verificationCode")
    @Mapping(target = "message", source = "message")
    @Mapping(target = "transactionStatus", ignore = true)
    void updateTransactionFromDto(UpdateTransactionDto updateTransactionDto, @MappingTarget Transaction transaction);

    @Mapping(target = "transactionStatus", source = "status")
    UpdateTransactionDto toUpdateTransactionDto(PaymentResponseDto response);

    @Mapping(target = "status", source = "transactionStatus")
    TransactionResultDto toDto(Transaction transaction);
}
