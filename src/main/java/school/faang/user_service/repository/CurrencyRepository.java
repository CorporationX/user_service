package school.faang.user_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import school.faang.user_service.entity.Currency;

/**
 * @author Evgenii Malkov
 */
@Repository
public interface CurrencyRepository extends JpaRepository<Currency, String> {
}
