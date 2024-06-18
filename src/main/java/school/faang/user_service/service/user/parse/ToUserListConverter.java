package school.faang.user_service.service.user.parse;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.Person;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.CountryRepository;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Component
@AllArgsConstructor
public class ToUserListConverter {

    private CountryRepository countryRepository;

    private UserMapper userMapper;

    private AddCountryAndPassword addCountryAndPassword;

    public List<User> convertToUserList(List<Person> persons) {
        List<User> users = new ArrayList<>();
        Iterable<Country> iterableForCountries = countryRepository.findAll();

        for (Person person : persons) {
            Iterator<Country> iteratorForCountries = iterableForCountries.iterator();
            String countryName = person.getContactInfo().getAddress().getCountry();
            User user = userMapper.toUser(person);

            while (iteratorForCountries.hasNext()) {
                Country existingCountry = iteratorForCountries.next();
                if (countryName.equals(existingCountry.getTitle())) {
                    users.add(addCountryAndPassword.addCountryAndPassword(user, existingCountry));
                }
            }

            if (user.getCountry() == null) {
                Country newCountry = new Country();
                newCountry.setTitle(countryName);
                countryRepository.save(newCountry);
                iterableForCountries = countryRepository.findAll();

                for (Country existingNewCountry : iterableForCountries) {
                    if (countryName.equals(existingNewCountry.getTitle())) {
                        user = addCountryAndPassword.addCountryAndPassword(user, countryRepository.findById(existingNewCountry.getId()).get());
                    }
                }
                users.add(user);
            }
        }
        return users;
    }
}
