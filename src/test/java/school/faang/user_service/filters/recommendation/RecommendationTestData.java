package school.faang.user_service.filters.recommendation;

import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.user.User;

public final class RecommendationTestData {

    public static final long AUTHOR_ID_1 = 123L;
    public static final long AUTHOR_ID_2 = 456L;
    public static final long RECEIVER_ID_1 = 789L;
    public static final long RECEIVER_ID_2 = 456L;

    public static final long REC_ID_1 = 1L;
    public static final long REC_ID_2 = 2L;
    public static final long REC_ID_3 = 3L;

    public static final String CONTENT_1 = "Recommendation 1";
    public static final String CONTENT_2 = "Recommendation 2";
    public static final String CONTENT_3 = "Recommendation 3";

    private RecommendationTestData() {
    }

    public static User user(long id) {
        return User.builder().id(id).build();
    }

    public static Recommendation rec(long id, long authorId, long receiverId, String content) {
        return Recommendation.builder()
                .id(id)
                .content(content)
                .author(user(authorId))
                .receiver(user(receiverId))
                .build();
    }

}