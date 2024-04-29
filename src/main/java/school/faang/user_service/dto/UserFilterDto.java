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

        if (namePattern != null && !user.getUsername().matches(namePattern)) {
            matches = false;
        }
        if (aboutPattern != null && !user.getAboutMe().matches(aboutPattern)) {
            matches = false;
        }
        if (emailPattern != null && !user.getEmail().matches(emailPattern)) {
            matches = false;
        }
        if (contactPattern != null) {
            var matchedContactsList = user.getContacts().stream()
                    .map(Contact::getContact)
                    .filter(contact -> contact.matches(contactPattern))
                    .toList();

            if (matchedContactsList.size() == 0) {
                matches = false;
            }
        }
        if (countryPattern != null && !user.getCountry().getTitle().matches(countryPattern)) {
            matches = false;
        }
        if (cityPattern != null && !user.getCity().matches(cityPattern)) {
            matches = false;
        }
        if (phonePattern != null && !user.getPhone().matches(phonePattern)) {
            matches = false;
        }
        if (skillPattern != null) {
            var matchedSkillsList = user.getSkills().stream()
                    .map(Skill::getTitle)
                    .filter(skill -> skill.matches(skillPattern))
                    .toList();

            if (matchedSkillsList.size() == 0) {
                matches = false;
            }
        }
        if (experienceMax > 0 && experienceMin > 0) {
            var userExperience = user.getExperience();

            if (userExperience < experienceMin || userExperience > experienceMax) {
                matches = false;
            }
        }

        return matches;
    }
}
