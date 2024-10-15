package school.faang.user_service.service;

import school.faang.user_service.model.entity.Country;

public interface CountryService {
    Country findOrCreateCountry(String countryName);
}
