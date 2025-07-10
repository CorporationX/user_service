package school.faang.user_service.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;
import school.faang.user_service.entity.user.Country;
import school.faang.user_service.exception.EntityNotFoundException;

import java.util.List;
import java.util.Set;

public interface CountryRepository extends JpaRepository<Country, Long> {

    List<Country> findByTitleIn(Set<String> titles);

    default Country getByIdOrThrow(long countryId) {
        return findById(countryId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Country %d not found", countryId)));
    }
}