package school.faang.user_service.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import school.faang.user_service.entity.Country;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

@Repository
public interface CountryRepository extends CrudRepository<Country, Long> {
    @Query("SELECT c.id FROM Country c WHERE c.title = :title")
    Optional<Long> findIdByTitle(@Param("title") String title);
}