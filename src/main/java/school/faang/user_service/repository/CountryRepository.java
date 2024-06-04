package school.faang.user_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;

import java.util.Optional;
import java.util.stream.Stream;

@Repository
public interface CountryRepository extends JpaRepository<Country, Long> {

    @Query(nativeQuery = true, value = """
            SELECT * FROM country
            WHERE title = :name
            """)
    Optional<Country> findByName(String name);
}