package school.faang.user_service.repository;

import feign.Param;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import school.faang.user_service.entity.Country;

import java.util.List;
import java.util.Optional;

@Repository
public interface CountryRepository extends CrudRepository<Country, Long> {

//    @Query("SELECT a.* FROM Country a WHERE a.title IN :titles")
//    List<Country> findByTitle(@Param("titles") List<String> titles);

    List<Country> findByTitleIn(List<String> titles);
}