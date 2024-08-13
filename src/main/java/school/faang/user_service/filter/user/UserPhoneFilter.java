package school.faang.user_service.filter.user;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.filter.UserFilterDto;
import school.faang.user_service.entity.User;

@Component
public class UserPhoneFilter implements UserFilter {
    @Override
    public boolean checkingForNull(UserFilterDto userFilterDto) {
        return userFilterDto.getPhone() != null;
    }

    @Override
    public boolean filterUsers(User user, UserFilterDto userFilterDto) {
        int resultOfUser = extractNumbers(user.getPhone());
        int resultOfFilter = extractNumbers(userFilterDto.getPhone());
        return user.getPhone().contains(userFilterDto.getPhone()) || resultOfUser == resultOfFilter;
    }

    private int extractNumbers(String phone) {
        return phone.chars()
                .filter(Character::isDigit)
                .map(Character::getNumericValue)
                .reduce(0, (a, b) -> a * 10 + b);
    }
}