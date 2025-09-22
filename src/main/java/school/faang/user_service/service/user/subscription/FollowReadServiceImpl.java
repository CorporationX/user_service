package school.faang.user_service.service.user.subscription;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.user.follower.FollowersPage;
import school.faang.user_service.repository.user.SubscriptionRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FollowReadServiceImpl implements FollowReadService {

    private static final int MIN_PAGE_SIZE = 1;
    private static final int MAX_PAGE_SIZE = 5_000;

    private final SubscriptionRepository subscriptionRepository;

    @Override
    public FollowersPage getFollowers(long authorId, String cursor, int limit) {
        int pageSize = normalizeLimit(limit);
        Long cursorId = parseCursor(cursor);

        List<Long> followerIds = (cursorId == null)
                ? subscriptionRepository.firstPageFollowerIds(authorId, PageRequest.ofSize(pageSize))
                : subscriptionRepository.nextPageFollowerIds(authorId, cursorId, PageRequest.ofSize(pageSize));

        String nextCursor = computeNextCursor(followerIds, pageSize);
        return new FollowersPage(followerIds, nextCursor);
    }

    @Override
    public FollowersPage getFollowing(long userId, String cursor, int limit) {
        int pageSize = normalizeLimit(limit);
        Long cursorId = parseCursor(cursor);

        List<Long> followingIds = (cursorId == null)
                ? subscriptionRepository.firstPageFollowingIds(userId, PageRequest.ofSize(pageSize))
                : subscriptionRepository.nextPageFollowingIds(userId, cursorId, PageRequest.ofSize(pageSize));

        String nextCursor = computeNextCursor(followingIds, pageSize);
        return new FollowersPage(followingIds, nextCursor);
    }

    private int normalizeLimit(int requested) {
        return Math.max(MIN_PAGE_SIZE, Math.min(requested, MAX_PAGE_SIZE));
    }

    private Long parseCursor(String cursor) {
        if (cursor == null || cursor.isBlank()) {
            return null;
        }
        try {
            return Long.parseLong(cursor);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String computeNextCursor(List<Long> ids, int pageSize) {
        if (ids == null || ids.size() < pageSize) {
            return null;
        }
        Long lastId = ids.get(ids.size() - 1);
        return String.valueOf(lastId);
    }
}
