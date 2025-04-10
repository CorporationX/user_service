package school.faang.user_service.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import school.faang.user_service.entity.Country;

public interface CountryRepository extends JpaRepository<Country, Long> {
    Optional<Country> findByTitle(String title);
}
