package school.faang.user_service.service.premium;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.repository.premium.PremiumRepository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Сервис для удаления батчей просроченных премиум-подписок.
 *
 * Этот класс вынесен в отдельный сервис для обеспечения корректной работы транзакций.
 * В Spring вызов методов с аннотацией {@code @Transactional} внутри одного бина (self-invocation)
 * не приводит к созданию транзакции, так как вызов обходит прокси. Чтобы избежать этой проблемы,
 * метод {@link #deleteBatch(List)} был вынесен в отдельный сервис.
 *
 * <p>Теперь вызов этого метода происходит через другой бин, что гарантирует применение
 * транзакционного поведения.</p>
 */

@Service
@RequiredArgsConstructor
@Slf4j
public class BatchDeletionService {

    private final PremiumRepository premiumRepository;

    /**
     * Удаляет батч просроченных премиум-подписок в рамках транзакции.
     *
     * @param batch список подписок, которые нужно удалить.
     *              Каждая подписка должна содержать уникальный идентификатор (id).
     */

    @Transactional
    public void deleteBatch(List<Premium> batch) {
        List<Long> ids = batch.stream()
                .map(Premium::getId)
                .collect(Collectors.toList());
        premiumRepository.deleteAllById(ids);
        log.debug("Deleted batch of {} premiums", batch.size());
    }
}