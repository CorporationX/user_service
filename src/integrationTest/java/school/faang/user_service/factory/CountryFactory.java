package school.faang.user_service.factory;

import school.faang.user_service.CommonFactory;
import school.faang.user_service.entity.Country;

public class CountryFactory extends CommonFactory {
    public static Country buildDefaultCountry() {
        return Country.builder()
                .title(COUNTRY_TITLE)
                .build();
    }
}
