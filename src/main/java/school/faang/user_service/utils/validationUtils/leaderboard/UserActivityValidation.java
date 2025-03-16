package school.faang.user_service.utils.validationUtils.leaderboard;

import lombok.extern.slf4j.Slf4j;
import school.faang.user_service.dto.leaderboard.UserActionDto;
import school.faang.user_service.dto.leaderboard.UserActivityRequestDto;

@Slf4j
public class UserActivityValidation {
    public static final String USER_ACTIVITY_REQUEST_DTO_CANT_BE_NULL = "UserActivityRequestDto can't be null";
    public static final String USER_ACTIVITY_REQUEST_DTO_ID_CANT_BE_NULL = "ID of UserActivityRequestDto can't be null";
    public static final String USER_ID_CANT_BE_NULL = "User ID can't be null";
    public static final String COUNTRY_CANT_BE_NULL_OR_BLANK = "Country can't be null or blank";
    public static final String USERNAME_CANT_BE_NULL_OR_BLANK = "Username can't be null or blank";
    public static final String NUMBER_OF_TOP_USERS_MUST_BE_POSITIVE = "Requested number of top active users must be positive";
    public static final String REQUESTED_INVALID_RANGE_OF_USERS = "Requested invalid range of top active users. Provided range: [%d, %d]";
    public static final String USER_ACTION_DTO_CANT_BE_NULL = "UserActionDto can't be null";

    public static void validateUserActionDto(UserActionDto userActionDto) {
        if (userActionDto == null) {
            log.error(USER_ACTION_DTO_CANT_BE_NULL);
            throw new IllegalArgumentException(USER_ACTION_DTO_CANT_BE_NULL);
        }
    }

    public static void validateUserActivityRequestDto(UserActivityRequestDto userActivityRequestDto) {
        if (userActivityRequestDto == null) {
            log.error(USER_ACTIVITY_REQUEST_DTO_CANT_BE_NULL);
            throw new IllegalArgumentException(USER_ACTIVITY_REQUEST_DTO_CANT_BE_NULL);
        } else if (userActivityRequestDto.id() == null) {
            log.error(USER_ACTIVITY_REQUEST_DTO_ID_CANT_BE_NULL);
            throw new IllegalArgumentException(USER_ACTIVITY_REQUEST_DTO_ID_CANT_BE_NULL);
        } else if (userActivityRequestDto.userId() == null) {
            log.error(USER_ID_CANT_BE_NULL);
            throw new IllegalArgumentException(USER_ID_CANT_BE_NULL);
        } else if (userActivityRequestDto.country() == null || userActivityRequestDto.country().isBlank()) {
            log.error(COUNTRY_CANT_BE_NULL_OR_BLANK);
            throw new IllegalArgumentException(COUNTRY_CANT_BE_NULL_OR_BLANK);
        } else if (userActivityRequestDto.username() == null || userActivityRequestDto.username().isBlank()) {
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
