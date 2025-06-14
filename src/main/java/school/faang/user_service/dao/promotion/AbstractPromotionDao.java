package school.faang.user_service.dao.promotion;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
public abstract class AbstractPromotionDao implements PromotionDao {

    public List<Long> processUpdateResults(List<Map.Entry<Long, Long>> entries, int[] updatedEvents) {
        log.info("Calling processUpdateResults method with updatedEvents length = {}", updatedEvents.length);
        List<Long> successUpdates = new ArrayList<>();
        List<Long> failed = new ArrayList<>();

        for (int i = 0; i < updatedEvents.length; i++) {
            if (updatedEvents[i] == 1 || updatedEvents[i] == -2) {
                successUpdates.add(entries.get(i).getKey());
            } else {
                failed.add(entries.get(i).getKey());
            }
        }
        log.info("Batch completed: {} succeeded, {} failed", successUpdates.size(), failed.size());
        return successUpdates;
    }
}
