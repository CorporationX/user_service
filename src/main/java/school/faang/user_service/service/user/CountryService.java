package school.faang.user_service.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.CountryDto;
import school.faang.user_service.mapper.CountryMapper;
import school.faang.user_service.repository.CountryRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CountryService {
    private final CountryRepository countryRepository;
    private final CountryMapper countryMapper;

    public CountryDto create(CountryDto countryDto) {
        return countryMapper.toDto(countryRepository.save(countryMapper.toEntity(countryDto)));
    }

    public Optional<Long> getIdByTitle(String title) {
        return countryRepository.findIdByTitle(title);
    }
}
