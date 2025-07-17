package school.faang.user_service.util;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionTemplate;
import school.faang.user_service.exception.BusinessException;

import static school.faang.user_service.enums.ErrorCode.DB_TRANSACTION_ERROR;


@Component
@RequiredArgsConstructor
public class Helper {
    private final TransactionTemplate transactionTemplate;

    @FunctionalInterface
    public interface ThrowingSupplier<R, E extends Exception> {
        R get() throws E;
    }

    @FunctionalInterface
    public interface ThrowingFunction<T, R, E extends Exception> {
        R apply(T t) throws E;
    }

    public <T> T executeInTransaction(ThrowingFunction<TransactionStatus, T, BusinessException> action) {
        try {
            return transactionTemplate.execute(status -> {
                try {
                    return action.apply(status);
                } catch (BusinessException e) {
                    status.setRollbackOnly();
                    throw e;
                }
            });
        } catch (TransactionException e) {
            throw new BusinessException(HttpStatus.BAD_GATEWAY, DB_TRANSACTION_ERROR);
        }
    }

    public static String createPath(String path, String path2) {
        return String.format(
                "%s%s%s",
                path,
                path.isEmpty() ? "" : ".",
                path2
        );
    }
}
