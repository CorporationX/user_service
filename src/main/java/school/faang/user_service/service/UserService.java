package school.faang.user_service.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.promotion.Promotion;
import school.faang.user_service.filter.user.UserFilter;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.PromotionRepository;
import school.faang.user_service.repository.UserRepository;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PromotionRepository promotionRepository;
    private final List<UserFilter> userFilters;
    private final UserMapper mapper;

    @Transactional
    public List<UserDto> getFilteredUsers(UserFilterDto filterDto, Long callingUserId) {
        User callingUser = userRepository.findById(callingUserId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        List<User> filteredUsers = getFilteredUsersFromRepository(filterDto);
        List<User> priorityFilteredUsers = getPriorityFilteredUsers(filteredUsers, callingUser);

        decrementRemainingShows(priorityFilteredUsers);
        deleteExpiredProfilePromotions("profile");

        return priorityFilteredUsers.stream()
                .map(mapper::toDto)
                .toList();
    }

    List<User> getFilteredUsersFromRepository(UserFilterDto filterDto) {
        return userFilters.stream()
                .filter(filter -> filter.isApplicable(filterDto))
                .map(filter -> filter.toSpecification(filterDto))
                .reduce(Specification::and)
                .map(spec -> userRepository.findAll((Specification<User>) spec))
                .orElseGet(Collections::emptyList);
    }

    private List<User> getPriorityFilteredUsers(List<User> filteredUsers, User callingUser) {
        return filteredUsers.stream()
                .filter(user -> user.getPromotion() == null ||
                        (user.getPromotion().getPromotionTarget().equals("profile")) &&
                                user.getPromotion().getRemainingShows() > 0)
                .sorted(Comparator.comparing(
                                (User user) -> {
                                    if (user.getPromotion() != null && user.getPromotion().getPriorityLevel() == 3
                                            && !user.getCountry().equals(callingUser.getCountry())) {
                                        return 1;
                                    }
                                    return user.getPromotion() != null ? 0 : 1;
                                })
                        .thenComparing(user -> user.getPromotion() != null ? -user.getPromotion().getPriorityLevel() : 0))
                .toList();
    }

    private void decrementRemainingShows(List<User> priorityFilteredUsers) {
        List<Long> promotionIds = priorityFilteredUsers.stream()
                .filter(user -> user.getPromotion() != null && user.getPromotion().getRemainingShows() > 0)
                .map(user -> user.getPromotion().getId())
                .toList();

        if (!promotionIds.isEmpty()) {
            promotionRepository.decreaseRemainingShows(promotionIds, "profile");
        }
    }

    private void deleteExpiredProfilePromotions(String promotionTarget) {
        List<Promotion> expiredPromotions = promotionRepository.findAllExpiredPromotions(promotionTarget);
        if (!expiredPromotions.isEmpty()) {
            promotionRepository.deleteAll(expiredPromotions);
        }
    }
}
