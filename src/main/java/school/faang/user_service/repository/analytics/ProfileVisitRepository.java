package school.faang.user_service.repository.analytics;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import school.faang.user_service.entity.analytics.ProfileVisit;

/**
 * Репозиторий для работы с посещениями профиля.
 *
 * @author Myrza
 * @since 19.08.2025
 */
public interface ProfileVisitRepository extends JpaRepository<ProfileVisit, Long> {
    Page<ProfileVisit> findAllByVisitedIdOrderByVisitedAtDesc(long visitedId, Pageable pageable);
}
