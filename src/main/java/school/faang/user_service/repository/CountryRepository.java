package school.faang.user_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import school.faang.user_service.entity.Country;

import java.util.Optional;

public interface CountryRepository extends JpaRepository<Country, Long> {
    Optional<Country> findByTitle(String title);
}
