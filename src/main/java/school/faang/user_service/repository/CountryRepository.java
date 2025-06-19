package school.faang.user_service.repository;

import org.springframework.data.repository.CrudRepository;
import school.faang.user_service.entity.Country;

import java.util.Optional;

public interface CountryRepository extends CrudRepository<Country, Long> {
    String findIdByTitle(String title);

    Optional<Country> findByTitle(String title);
}