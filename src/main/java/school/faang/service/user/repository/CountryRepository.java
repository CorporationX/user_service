package school.faang.service.user.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import school.faang.service.user.entity.Country;

@Repository
public interface CountryRepository extends CrudRepository<Country, Long> {
}