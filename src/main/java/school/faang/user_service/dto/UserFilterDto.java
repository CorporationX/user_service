package school.faang.user_service.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.contact.Contact;

@Data
@NoArgsConstructor
public class UserFilterDto {
    private String namePattern;
    private String aboutPattern;
    private String emailPattern;
    private String contactPattern;
    private String countryPattern;
    private String cityPattern;
    private String phonePattern;
    private String skillPattern;
    private int experienceMin;
    private int experienceMax;
    private int page;           //непонятный фильтр
    private int pageSize;       //непонятный фильтр

    public boolean matches(User user) {
        boolean matches = true;

        matches = matchesNamePattern(user, matches);
        matches = mathcesAboutPattern(user, matches);
        matches = matchesEmailPattern(user, matches);
        matches = matchesContactPattern(user, matches);
        matches = matchesCountryPattern(user, matches);
        matches = matchesCityPattern(user, matches);
        matches = matchesPhonePattern(user, matches);
        matches = matchesSkillPattern(user, matches);
        matches = matchesExperienceBounds(user, matches);

        return matches;
    }

    private boolean matchesExperienceBounds(User user, boolean matches) {
        if (experienceMax > 0 || experienceMin > 0) {
            var userExperience = user.getExperience();

            if (userExperience < experienceMin || (experienceMax > 0 && userExperience > experienceMax)) {
                matches = false;
            }


        }
        return matches;
    }

    private boolean matchesSkillPattern(User user, boolean matches) {
        if (skillPattern != null) {
            var matchedSkillsList = user.getSkills().stream()
                    .map(Skill::getTitle)
                    .filter(skill -> skill.matches(skillPattern))
                    .toList();

            if (matchedSkillsList.size() == 0) {
                matches = false;
            }
        }
        return matches;
    }

    private boolean matchesPhonePattern(User user, boolean matches) {
        if (phonePattern != null && !user.getPhone().matches(phonePattern)) {
            matches = false;
        }
        return matches;
    }

    private boolean matchesCityPattern(User user, boolean matches) {
        if (cityPattern != null && !user.getCity().matches(cityPattern)) {
            matches = false;
        }
        return matches;
    }

    private boolean matchesCountryPattern(User user, boolean matches) {
        if (countryPattern != null && !user.getCountry().getTitle().matches(countryPattern)) {
            matches = false;
        }
        return matches;
    }

    private boolean matchesContactPattern(User user, boolean matches) {
        if (contactPattern != null) {
            var matchedContactsList = user.getContacts().stream()
                    .map(Contact::getContact)
                    .filter(contact -> contact.matches(contactPattern))
                    .toList();

            if (matchedContactsList.size() == 0) {
                matches = false;
            }
        }
        return matches;
    }

    private boolean matchesEmailPattern(User user, boolean matches) {
        if (emailPattern != null && !user.getEmail().matches(emailPattern)) {
            matches = false;
        }
        return matches;
    }

    private boolean mathcesAboutPattern(User user, boolean matches) {
        if (aboutPattern != null && !user.getAboutMe().matches(aboutPattern)) {
            matches = false;
        }
        return matches;
    }

    private boolean matchesNamePattern(User user, boolean matches) {
        if (namePattern != null && !user.getUsername().matches(namePattern)) {
            matches = false;
        }
        return matches;
    }
}
