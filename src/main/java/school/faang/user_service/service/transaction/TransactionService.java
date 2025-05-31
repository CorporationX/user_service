package school.faang.user_service.service.transaction;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.transaction.TransactionResultDto;
import school.faang.user_service.dto.transaction.UpdateTransactionDto;
import school.faang.user_service.entity.transaction.Transaction;
import school.faang.user_service.entity.transaction.Payable;
import school.faang.user_service.entity.transaction.TransactionStatus;
import school.faang.user_service.mapper.TransactionMapper;
import school.faang.user_service.repository.TransactionRepository;
import school.faang.user_service.service.payment.PaymentService;
import school.faang.user_service.service.utils.TransactionServiceUtils;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionService {
    private static final String SUCCESS = "SUCCESS";

    private final TransactionRepository transactionRepository;
    private final TransactionServiceUtils transactionServiceUtils;
    private final PaymentService paymentService;
    private final TransactionMapper transactionMapper;
    private final UserContext userContext;

    private Transaction createTransaction(Long userId, Payable item) {
        Transaction transaction = transactionServiceUtils.buildTransaction(userId, item);
        transactionRepository.save(transaction);
        return transaction;
    }

    public TransactionResultDto buyItem(Long userId, Payable item) {
            return updateTransaction(
                    transactionMapper.toUpdateTransactionDto(
                            paymentService.buyItem(
                                    transactionMapper.toPaymentRequestDto(
                                            createTransaction(userId, item)))
                    ));
    }

    private TransactionResultDto updateTransaction(UpdateTransactionDto updateTransaction) {
        Long transactionNumber = updateTransaction.getPaymentNumber();
        Transaction transaction = transactionRepository.findTransactionByTransactionNumber(transactionNumber)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Transaction with number %s not found!", transactionNumber)));
        if (updateTransaction.getTransactionStatus().equals((SUCCESS))) {
            transaction.setTransactionStatus(TransactionStatus.SETTLED);
            log.info("Transaction {} get status Settled successfully", transactionNumber);
        } else {
            transaction.setTransactionStatus(TransactionStatus.FAILED);
            log.info("Transaction {} get status Failed successfully", transactionNumber);
        }
        transactionMapper.updateTransactionFromDto(updateTransaction, transaction);
        TransactionResultDto transactionResultDto = transactionMapper.toDto(transaction);
        transactionResultDto.setCurrency(transaction.getCurrencyCode());
        return transactionResultDto;
    }

}
