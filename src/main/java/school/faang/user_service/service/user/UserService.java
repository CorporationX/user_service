package school.faang.user_service.service.user;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.user.UpdateUsersRankDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final static int BATCH_SIZE = 50;
    @PersistenceContext
    private final EntityManager entityManager;
    private final UserRepository userRepository;
    private final UserContext userContext;
    private final AvatarService avatarService;
    private final UserSkillGuaranteeRepository userSkillGuaranteeRepository;
    private final UserMapper userMapper;

    public Optional<User> findById(long userId) {
        return userRepository.findById(userId);
    }

    @Transactional
    public ResponseEntity<Void> updateUsersRankByUserIds(UpdateUsersRankDto userDto) {
        log.info("batch is starting {}", userDto);
        int batchCounter = 1;
        for (Map.Entry<Long, Double> userNewRank : userDto.getUsersRankByIds().entrySet()) {
            if (userNewRank.getValue() != 0.0) {
                BigDecimal value = BigDecimal.valueOf(userNewRank.getValue());
                double roundedValue = value.setScale(2, RoundingMode.HALF_UP).doubleValue();
                try {
                    userRepository.updateUserRankByUserId(userNewRank.getKey(), roundedValue);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                    if (roundedValue > userDto.getHalfUserRank()) {
                        userRepository.updateUserRankByUserIdToMax(userNewRank.getKey(), BigDecimal.valueOf(userDto.getMaximumUserRating()));
                    } else {
                        userRepository.updateUserRankByUserIdToMin(userNewRank.getKey(), BigDecimal.valueOf(userDto.getMinimumUserRating()));
                    }
                }
                batchCounter++;
            }
            if (batchCounter % BATCH_SIZE == 0) {
                flushAndClear();
            }
        }
        updatePassiveUsersRating(userDto);
        log.info("users rank success updated!");
        return ResponseEntity.ok().build();
    }

    @Transactional
    public void updatePassiveUsersRating(UpdateUsersRankDto userDto) {
        BigDecimal maxPossibleRating = BigDecimal.valueOf(userDto.getMaximumGrowthRating() * userDto.getRatingGrowthIntensive());
        BigDecimal roundedValue = maxPossibleRating.setScale(2, RoundingMode.HALF_UP);
        Set<Long> activeUsersIds = userDto.getUsersRankByIds().keySet();
        userRepository.updatePassiveUsersRatingWhichRatingLessThanRating(roundedValue.doubleValue(), activeUsersIds);
        userRepository.updatePassiveUsersRatingWhichRatingMoreThanRating(roundedValue.doubleValue(), activeUsersIds);
        flushAndClear();
    }

    @Transactional
    public void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }

    public String generateRandomAvatar() {
        Long userId = userContext.getUserId();
        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found"));
        String randomAvatarUrl = avatarService.generateRandomAvatar(UUID.randomUUID().toString(),
                userId + ".svg");
        UserProfilePic userProfilePic = new UserProfilePic();
        userProfilePic.setFileId(randomAvatarUrl);
        user.setUserProfilePic(userProfilePic);
        userRepository.save(user);
        return randomAvatarUrl;
    }

    public UserSkillGuarantee addGuaranty(long userId, SkillOffer skillOffer) {
        UserSkillGuarantee guarantee = UserSkillGuarantee.builder().user(
                        userRepository.findById(userId).get()
                ).guarantor(skillOffer.getRecommendation().getAuthor())
                .build();
        return userSkillGuaranteeRepository.save(guarantee);

    public UserDto getUserDtoById(long userId) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new DataValidationException("user not found!"));
        return userMapper.toDto(user);
    }

    public List<UserDto> getUserDtosByIds(List<Long> userIds) {
        List<User> users = userRepository.findAllByIds(userIds)
                .orElseThrow(() -> new DataValidationException("users not found!"));
        return users.stream()
                .map(userMapper::toDto)
                .toList();
    }
}

