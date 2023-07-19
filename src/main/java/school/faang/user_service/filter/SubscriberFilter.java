package school.faang.user_service.filter;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;

@Component
public class SubscriberFilter {
    public boolean matchesFilters(User user, UserFilterDto filter) {
        return matchesNamePattern(user, filter.getNamePattern()) &&
                matchesAboutPattern(user, filter.getAboutPattern()) &&
                matchesEmailPattern(user, filter.getEmailPattern()) &&
                matchesContactPattern(user, filter.getContactPattern()) &&
                matchesCountryPattern(user, filter.getCountryPattern()) &&
                matchesCityPattern(user, filter.getCityPattern()) &&
                matchesPhonePattern(user, filter.getPhonePattern()) &&
                matchesSkillPattern(user, filter.getSkillPattern()) &&
                matchesExperienceRange(user, filter.getExperienceMin(), filter.getExperienceMax());
    }

    private boolean matchesNamePattern(User user, String namePattern) {
        if (namePattern == null || namePattern.isEmpty()) {
            return true;
        }
        return user.getUsername().toLowerCase().contains(namePattern.toLowerCase());
    }

    private boolean matchesAboutPattern(User user, String aboutPattern) {
        if (aboutPattern == null || aboutPattern.isEmpty()) {
            return true;
        }
        return user.getAboutMe() != null && user.getAboutMe().toLowerCase().contains(aboutPattern.toLowerCase());
    }

    private boolean matchesEmailPattern(User user, String emailPattern) {
        if (emailPattern == null || emailPattern.isEmpty()) {
            return true;
        }
        return user.getEmail().toLowerCase().contains(emailPattern.toLowerCase());
    }

    private boolean matchesContactPattern(User user, String contactPattern) {
        if (contactPattern == null || contactPattern.isEmpty()) {
            return true;
        }
        return user.getContacts().stream()
                .anyMatch(contact -> contact.getContact().toLowerCase().contains(contactPattern.toLowerCase()));
    }

    private boolean matchesCountryPattern(User user, String countryPattern) {
        if (countryPattern == null || countryPattern.isEmpty()) {
            return true;
        }
        return user.getCountry().getTitle().toLowerCase().contains(countryPattern.toLowerCase());
    }

    private boolean matchesCityPattern(User user, String cityPattern) {
        if (cityPattern == null || cityPattern.isEmpty()) {
            return true;
        }
        String userCity = user.getCity() != null ? user.getCity() : "";
        return userCity.toLowerCase().contains(cityPattern.toLowerCase());
    }

    private boolean matchesPhonePattern(User user, String phonePattern) {
        if (phonePattern == null || phonePattern.isEmpty()) {
            return true;
        }
        return user.getPhone() != null && user.getPhone().contains(phonePattern);
    }

    private boolean matchesSkillPattern(User user, String skillPattern) {
        if (skillPattern == null || skillPattern.isEmpty()) {
            return true;
        }
        return user.getSkills().stream()
                .anyMatch(skill -> skill.getTitle().toLowerCase().contains(skillPattern.toLowerCase()));
    }

    private boolean matchesExperienceRange(User user, int minExperience, int maxExperience) {
        if (minExperience == 0 && maxExperience == 0) {
            return true;
        }
        int userExperience = user.getExperience() != null ? user.getExperience() : 0;
        return userExperience >= minExperience && userExperience <= maxExperience;
    }
}
