package school.faang.user_service.filter;

import school.faang.user_service.entity.User;

import java.util.function.Predicate;
import java.util.regex.Pattern;

public class UserFilterByNamePhoneExperience implements Predicate<User> {
    private final int experienceMin;
    private final int experienceMax;
    private final Pattern namePattern;
    private final Pattern phonePattern;

    public UserFilterByNamePhoneExperience(String nameRegex, String phoneRegex, int experienceMin, int experienceMax) {
        this.namePattern = nameRegex == null || nameRegex.isBlank() ? null : Pattern.compile(nameRegex);
        this.phonePattern = phoneRegex == null || phoneRegex.isBlank() ? null : Pattern.compile(phoneRegex);
        this.experienceMin = experienceMin;
        this.experienceMax = experienceMax;
    }

    @Override
    public boolean test(User user) {
        if (user == null) return false;

        boolean matchesName = (namePattern == null) || namePattern.matcher(user.getUsername()).matches();
        boolean matchesPhone = (phonePattern == null) || phonePattern.matcher(user.getPhone()).matches();
        boolean experienceInRange = user.getExperience() >= experienceMin && user.getExperience() <= experienceMax;

        return matchesName && matchesPhone && experienceInRange;
    }
}