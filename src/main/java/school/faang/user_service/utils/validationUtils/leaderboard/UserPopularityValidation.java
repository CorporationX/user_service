package school.faang.user_service.utils.validationUtils.leaderboard;

import lombok.extern.slf4j.Slf4j;
import school.faang.user_service.dto.leaderboard.UserImpactDto;
import school.faang.user_service.dto.leaderboard.UserPopularityRequestDto;

@Slf4j
public class UserPopularityValidation {
    public static final String USER_POPULARITY_REQUEST_DTO_CANT_BE_NULL = "UserPopularityRequestDto can't be null";
    public static final String USER_POPULARITY_REQUEST_DTO_ID_CANT_BE_NULL = "ID of UserPopularityRequestDto can't be null";
    public static final String USER_ID_CANT_BE_NULL = "User ID can't be null";
    public static final String COUNTRY_CANT_BE_NULL_OR_BLANK = "Country can't be null or blank";
    public static final String USERNAME_CANT_BE_NULL_OR_BLANK = "Username can't be null or blank";
    public static final String NUMBER_OF_TOP_USERS_MUST_BE_POSITIVE = "Requested number of top popular users must be positive";
    public static final String REQUESTED_INVALID_RANGE_OF_USERS = "Requested invalid range of top popular users. Provided range: [%d, %d]";
    public static final String USER_IMPACT_DTO_CANT_BE_NULL = "UserImpactDto can't be null";

    public static void validateUserImpactDto(UserImpactDto userImpactDto) {
        if (userImpactDto == null) {
            log.error(USER_IMPACT_DTO_CANT_BE_NULL);
            throw new IllegalArgumentException(USER_IMPACT_DTO_CANT_BE_NULL);
        }
    }

    public static void validateUserPopularityRequestDto(UserPopularityRequestDto userPopularityRequestDto) {
        if (userPopularityRequestDto == null) {
            log.error(USER_POPULARITY_REQUEST_DTO_CANT_BE_NULL);
            throw new IllegalArgumentException(USER_POPULARITY_REQUEST_DTO_CANT_BE_NULL);
        } else if (userPopularityRequestDto.id() == null) {
            log.error(USER_POPULARITY_REQUEST_DTO_ID_CANT_BE_NULL);
            throw new IllegalArgumentException(USER_POPULARITY_REQUEST_DTO_ID_CANT_BE_NULL);
        } else if (userPopularityRequestDto.userId() == null) {
            log.error(USER_ID_CANT_BE_NULL);
            throw new IllegalArgumentException(USER_ID_CANT_BE_NULL);
        } else if (userPopularityRequestDto.country() == null || userPopularityRequestDto.country().isBlank()) {
            log.error(COUNTRY_CANT_BE_NULL_OR_BLANK);
            throw new IllegalArgumentException(COUNTRY_CANT_BE_NULL_OR_BLANK);
        } else if (userPopularityRequestDto.username() == null || userPopularityRequestDto.username().isBlank()) {
            log.error(USERNAME_CANT_BE_NULL_OR_BLANK);
            throw new IllegalArgumentException(USERNAME_CANT_BE_NULL_OR_BLANK);
        }
    }

    public static void validateTopN(int topN) {
        if (topN <= 0) {
            log.error(NUMBER_OF_TOP_USERS_MUST_BE_POSITIVE);
            throw new IllegalArgumentException(NUMBER_OF_TOP_USERS_MUST_BE_POSITIVE);
        }
    }

    public static void validateTopRange(int start, int end) {
        if (start <= 0 || start > end) {
            String message = String.format(REQUESTED_INVALID_RANGE_OF_USERS, start, end);
            log.error(message);
            throw new IllegalArgumentException(message);
        }
    }
}
