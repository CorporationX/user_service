package school.faang.user_service.filters;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class UserFilter {
    public List<User> filterUsers(Stream<User> userStream, UserFilterDto filter) {
        Predicate<User> namePredicate = user -> filter.getNamePattern() == null || filter.getNamePattern().isBlank() || user.getUsername().contains(filter.getNamePattern());
        Predicate<User> aboutPredicate = user -> filter.getAboutPattern() == null || filter.getAboutPattern().isBlank() || user.getAboutMe().contains(filter.getAboutPattern());
        Predicate<User> emailPredicate = user -> filter.getEmailPattern() == null || filter.getEmailPattern().isBlank() || user.getEmail().contains(filter.getEmailPattern());
        Predicate<User> contactPredicate = user -> filter.getContactPattern() == null || filter.getContactPattern().isBlank() || user.getContacts().stream()
                .anyMatch(contact -> contact.getContact().contains(filter.getContactPattern()));
        Predicate<User> countryPredicate = user -> filter.getCountryPattern() == null || filter.getCountryPattern().isBlank() || user.getCountry().getTitle().contains(filter.getCountryPattern());
        Predicate<User> cityPredicate = user -> filter.getCityPattern() == null || filter.getCityPattern().isBlank() || user.getCity().contains(filter.getCityPattern());
        Predicate<User> phonePredicate = user -> filter.getPhonePattern() == null || filter.getPhonePattern().isBlank() || user.getPhone().contains(filter.getPhonePattern());
        Predicate<User> skillPredicate = user -> filter.getSkillPattern() == null || filter.getSkillPattern().isBlank() || user.getSkills().stream()
                .anyMatch(skill -> skill.getTitle().contains(filter.getSkillPattern()));
        Predicate<User> experienceMinPredicate = user -> Integer.toString(filter.getExperienceMin()).isBlank() || user.getExperience() > filter.getExperienceMin();
        Predicate<User> experienceMaxPredicate = user -> Integer.toString(filter.getExperienceMax()).isBlank() || user.getExperience() < filter.getExperienceMax();

        return userStream.filter(namePredicate)
                .filter(aboutPredicate)
                .filter(emailPredicate)
                .filter(contactPredicate)
                .filter(countryPredicate)
                .filter(cityPredicate)
                .filter(phonePredicate)
                .filter(skillPredicate)
                .filter(experienceMinPredicate)
                .filter(experienceMaxPredicate)
                .collect(Collectors.toList());
    }
}
