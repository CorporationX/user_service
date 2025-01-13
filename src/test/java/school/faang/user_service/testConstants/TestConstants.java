package school.faang.user_service.testConstants;

import java.util.List;

public interface TestConstants {

    Long VALID_USER_ID = 1L;
    Long VALID_REQUESTER_ID = 1L;
    Long VALID_RECEIVER_ID = 2L;
    Long VALID_RECOMMENDATION_ID = 1L;
    Long INVALID_REQUESTER_ID = -1L;
    Long INVALID_RECEIVER_ID = -2L;
    Long VALID_GOAL_ID = 1L;
    Long PARENT_GOAL_ID = 1L;
    Long INVALID_GOAL_ID = -1L;
    Long INVALID_USER_ID = -1L;
    String FILTER_FIELD = "id";
    String MESSAGE = "Hello FAANG school";
    String GOAL_TITLE = "Learn Java";
    String REJECTION_REASON = "test rejection reason";
    String SUCCESS_MESSAGE = "Successfully completed";
    String GOAL_DESCRIPTION = "Master Java Core and Spring Boot";
    String ACTIVE_GOAL_EXCEPTION_MESSAGE = "User with ID %d cannot have more than %d active goals.";
    String RECOMMENDATION_REQUEST_NOT_FOUND_EXCEPTION_MESSAGE = "Recommendation request not found with ID: %d";
    String RECOMMENDATION_REQUEST_EXCEPTION_MESSAGE = "Recommendation request not found";
    String GOAL_DOES_NOT_EXIST_EXCEPTION_MESSAGE = "Goal with ID %d does not exist.";
    String USER_NOT_FOUND_EXCEPTION_MESSAGE = "User not found with id: %d";
    String SKILLS_EMPTY_OR_NULL_EXCEPTION_MESSAGE = "You have not provided any skill IDs";
    String SOME_SKILLS_DOES_NOT_EXIST_EXCEPTION_MESSAGE = "Some provided skill IDs do not exist in the database";
    String USER_SENT_RECOMMENDATION_REQUEST_LAST_SIX_MONTH_EXCEPTION_MESSAGE =
            "User with ID %d cannot send a new recommendation request. Last request was sent at %s. " +
                    "At least 6 months should elapse.";
    String VALID_SKILLS_EXCEPTION_MESSAGE = "Some of the provided skills do not exist in the database.";
    String ASSERT_EXCEPTION_MESSAGE = "Expected IllegalArgumentException occurred";
    int MAX_ACTIVE_GOALS = 3;
    int EXCEEDING_ACTIVE_GOALS = 5;
    int WITHIN_ACTIVE_GOALS = 2;
    int EXISTING_SKILLS_COUNT = 0;
    int ACTIVE_GOALS_COUNT = 0;
    List<Long> SKILL_IDS = List.of(1L, 2L);
    List<Long> USER_IDS = List.of(1L, 2L);

}

