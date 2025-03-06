package school.faang.user_service.util;

import school.faang.user_service.dto.UserDto;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

public class UserGenerator {
    private static final Long MIN_ID_RANGE = 1L;
    private static final Long MAX_ID_RANGE = 100L;
    private static final Long MIN_COUNT = 1L;
    private static final Long MAX_COUNT = 10L;

    public UserDto generateUserDto(Long userId, String userName) {
        Long count = generateId(MIN_COUNT, MAX_COUNT);
        List<Long> menteesId = generateListIds(count, MIN_ID_RANGE, MAX_ID_RANGE);
        List<Long> mentorsId = generateListIds(count, MIN_ID_RANGE, MAX_ID_RANGE);

        return generateUserDto(userId, userName, menteesId, mentorsId);
    }

    public UserDto generateUserDto(Long userId, String userName, List<Long> menteesId, List<Long> mentorsId) {
        return UserDto.builder()
            .id(userId)
            .username(userName)
            .menteesId(menteesId)
            .mentorsId(mentorsId)
            .build();
    }

    private List<Long> generateListIds(Long count, Long minRange, Long maxRange) {
        Long rangeSize = maxRange - minRange + 1;

        if (rangeSize <= count) {
            return LongStream.rangeClosed(minRange, maxRange)
                .boxed()
                .collect(Collectors.toList());
        }

        return ThreadLocalRandom.current()
            .longs(minRange, maxRange + 1)
            .distinct()
            .limit(count)
            .boxed()
            .collect(Collectors.toList());
    }

    private Long generateId(Long minRange, Long maxRange) {
        return ThreadLocalRandom.current().nextLong(minRange, maxRange + 1);
    }
}
