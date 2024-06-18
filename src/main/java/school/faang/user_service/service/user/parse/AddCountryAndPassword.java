package school.faang.user_service.service.user.parse;

import org.springframework.stereotype.Component;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;

import java.util.Random;

@Component
public class AddCountryAndPassword {

    private final String COMBINATION = "QWERTYUIOPASDFGHJKLZXCVBNMqwertyuiopasdfghjklzxcvbnm1234567890!@#$%^&*()_+{}:<>?";

    public User addCountryAndPassword(User user, Country country) {
        user.setCountry(country);
        user.setPassword(passwordGenerator());
        return user;
    }

    private String passwordGenerator() {
        int length = 10;
        StringBuilder password = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            password.append(COMBINATION.charAt(random.nextInt(COMBINATION.length())));
        }
        return password.toString();
    }
}
