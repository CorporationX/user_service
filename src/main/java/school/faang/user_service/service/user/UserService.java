package school.faang.user_service.service.user;

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
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class UserService {
    private final static String PROMOTION_TARGET = "profile";

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
        deleteExpiredProfilePromotions();

        return priorityFilteredUsers.stream()
                .map(mapper::toDto)
                .toList();
    }

    private List<User> getFilteredUsersFromRepository(UserFilterDto filterDto) {
        return userFilters.stream()
                .filter(filter -> filter.isApplicable(filterDto))
                .map(filter -> filter.toSpecification(filterDto))
                .reduce(Specification::and)
                .map(spec -> userRepository.findAll((Specification<User>) spec))
                .orElseGet(Collections::emptyList);
    }

    private List<User> getPriorityFilteredUsers(List<User> filteredUsers, User callingUser) {

        Comparator<User> countryAndPriorityComparator = Comparator.comparing((User user) -> {
            if (user.getPromotions() == null || user.getPromotions().isEmpty()) {
                return 1;
            }

            Promotion targetPromotion = getTargetPromotion(user);

            if (targetPromotion != null &&
                    targetPromotion.getPriorityLevel() == 3 &&
                    !user.getCountry().equals(callingUser.getCountry())) {
                return 1;
            }

            if (targetPromotion == null) {
                return 1;
            }

            return 0;
        }).thenComparing(user -> {
            if (user.getPromotions() == null || user.getPromotions().isEmpty()) {
                return 0;
            }

            Promotion targetPromotion = getTargetPromotion(user);

            return targetPromotion != null ? -targetPromotion.getPriorityLevel() : 0;
        });

        return filteredUsers.stream()
                .sorted(countryAndPriorityComparator)
                .toList();
    }

    private void decrementRemainingShows(List<User> priorityFilteredUsers) {
        List<Long> promotionIds = priorityFilteredUsers.stream()
                .flatMap(user -> {
                    List<Promotion> promotions = user.getPromotions();
                    if (promotions == null) {
                        return Stream.empty();
                    }
                    return promotions.stream()
                            .filter(promotion -> PROMOTION_TARGET.equals(promotion.getPromotionTarget()) &&
                                    promotion.getRemainingShows() > 0)
                            .map(Promotion::getId);
                })
                .toList();

        if (!promotionIds.isEmpty()) {
            promotionRepository.decreaseRemainingShows(promotionIds, PROMOTION_TARGET);
        }
    }

    private void deleteExpiredProfilePromotions() {
        List<Promotion> expiredPromotions = promotionRepository.findAllExpiredPromotions(UserService.PROMOTION_TARGET);
        if (!expiredPromotions.isEmpty()) {
            promotionRepository.deleteAll(expiredPromotions);
        }
    }

    private Promotion getTargetPromotion(User user) {
        return user.getPromotions().stream()
                .filter(promotion -> PROMOTION_TARGET.equals(promotion.getPromotionTarget()))
                .findFirst()
                .orElse(null);
    }
}
