package school.faang.user_service.integration.factory;

import school.faang.user_service.entity.Country;
import school.faang.user_service.integration.CommonFactory;

public class CountryFactory extends CommonFactory {
    public static Country buildDefaultCountry() {
        return Country.builder()
                .title(COUNTRY_TITLE)
                .build();
    }
}
