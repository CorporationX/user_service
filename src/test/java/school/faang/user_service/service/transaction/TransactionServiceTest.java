package school.faang.user_service.service.transaction;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.payment.PaymentRequestDto;
import school.faang.user_service.dto.payment.PaymentResponseDto;
import school.faang.user_service.dto.transaction.TransactionResultDto;
import school.faang.user_service.dto.transaction.UpdateTransactionDto;
import school.faang.user_service.entity.transaction.Transaction;
import school.faang.user_service.entity.transaction.Payable;
import school.faang.user_service.entity.transaction.TransactionStatus;
import school.faang.user_service.mapper.TransactionMapper;
import school.faang.user_service.repository.TransactionRepository;
import school.faang.user_service.service.payment.PaymentService;

import school.faang.user_service.service.utils.TransactionServiceUtils;

import java.util.Currency;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {
    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private TransactionServiceUtils transactionServiceUtils;
    @Mock
    private PaymentService paymentService;
    @Mock
    private UserContext userContext;
    @Spy
    private TransactionMapper transactionMapper;
    @InjectMocks
    private TransactionService transactionService;

    @Test
    void buyItemSuccess() {
       
        Long userId = 1L;
        Long transactionNumber = 67890L;
        Currency currencyCode = Currency.getInstance("EUR");

        Payable mockItem = mock(Payable.class);

        Transaction transaction = new Transaction();
        transaction.setTransactionNumber(transactionNumber);
        transaction.setCurrencyCode(currencyCode);

        PaymentRequestDto paymentRequestDto = new PaymentRequestDto();
        PaymentResponseDto paymentResponseDto = new PaymentResponseDto();
        paymentResponseDto.setPaymentNumber(transactionNumber);

        UpdateTransactionDto updateTransactionDto = new UpdateTransactionDto();
        updateTransactionDto.setPaymentNumber(transactionNumber);
        updateTransactionDto.setTransactionStatus("SUCCESS");

        when(transactionServiceUtils.buildTransaction(userId, mockItem)).thenReturn(transaction);
        when(transactionRepository.save(transaction)).thenReturn(transaction);

        when(transactionMapper.toPaymentRequestDto(transaction)).thenReturn(paymentRequestDto);
        when(transactionMapper.toUpdateTransactionDto(paymentResponseDto)).thenReturn(updateTransactionDto);

        when(paymentService.buyItem(paymentRequestDto)).thenReturn(paymentResponseDto);

        when(transactionRepository.findTransactionByTransactionNumber(transactionNumber))
                .thenReturn(Optional.of(transaction));

        Transaction transactionResult = transactionService.buyItem(userId, mockItem);

        assertNotNull(transactionResult, "Результат не должен быть null");
        assertEquals(currencyCode, transactionResult.getCurrencyCode(), "Код валюты должен быть установлен в финальном DTO.");

        verify(transactionServiceUtils).buildTransaction(userId, mockItem);
        verify(transactionRepository).save(transaction);
        verify(transactionMapper).toPaymentRequestDto(transaction);
        verify(paymentService).buyItem(paymentRequestDto);
        verify(transactionMapper).toUpdateTransactionDto(paymentResponseDto);
        verify(transactionRepository).findTransactionByTransactionNumber(transactionNumber);

        assertEquals(TransactionStatus.SETTLED, transaction.getTransactionStatus(), "Статус транзакции должен быть SETTLED.");

        verify(transactionMapper).updateTransactionFromDto(updateTransactionDto, transaction);
    }
}