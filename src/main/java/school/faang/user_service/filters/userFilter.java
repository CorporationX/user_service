package school.faang.user_service.filters;

import school.faang.user_service.commonMessages.MessagesForExceptions;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.List;

public class userFilter {
    public List<UserDto> applyFilter(List<User> users, UserFilterDto filter){
        isApplicable(users);
        return users.stream()
                .filter(user -> filterUser(user, filter))
                .map(UserMapper.INSTANCE::userToDto)
                .toList();
    }

    private Boolean filterUser(User user, UserFilterDto filter){
        return user.getUsername().matches(filter.getNamePattern()) &&
                user.getEmail().matches(filter.getEmailPattern()) &&
                user.getPhone().matches(filter.getPhonePattern()) &&
                user.getAboutMe().matches(filter.getAboutPattern()) &&
                user.getCountry().getTitle().matches(filter.getCountryPattern()) &&
                user.getCity().matches(filter.getCityPattern()) &&
                user.getExperience() > filter.getExperienceMin() &&
                user.getExperience() < filter.getExperienceMax() &&
                user.getContacts().stream().allMatch(contact -> contact.getContact().matches(filter.getContactPattern())) &&
                user.getSkills().stream().allMatch(skill -> skill.getTitle().matches(filter.getSkillPattern()));
    }

    private void isApplicable(List<User> users){
        for (User user : users) {
            if (user == null) {
                throw new NullPointerException(MessagesForExceptions);
            }
        }
    }
}
