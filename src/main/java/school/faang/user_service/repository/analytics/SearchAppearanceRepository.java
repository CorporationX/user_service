package school.faang.user_service.repository.analytics;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import school.faang.user_service.entity.analytics.SearchAppearance;

/**
 * Репозиторий для работы с сущностью {@link SearchAppearance}.
 *
 * @author Myrza
 * @since 19.08.2025
 */
public interface SearchAppearanceRepository extends JpaRepository<SearchAppearance, Long> {
    Page<SearchAppearance> findAllBySearchedIdOrderBySearchedAtDesc(long searchedId, Pageable pageable);
}
