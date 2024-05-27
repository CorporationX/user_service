package school.faang.user_service.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import school.faang.user_service.dto.CountryDto;
import school.faang.user_service.entity.Country;

import java.util.List;

@Repository
public interface CountryDtoRepository extends CrudRepository<CountryDto, Long> {
    List<CountryDto> findAll();
}
