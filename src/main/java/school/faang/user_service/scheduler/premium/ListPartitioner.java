package school.faang.user_service.scheduler.premium;

import org.apache.commons.collections4.ListUtils;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.premium.Premium;

import java.util.List;

@Component
public class ListPartitioner {
    public List<List<Premium>> partition(List<Premium> list, int batchSize) {
        return ListUtils.partition(list, batchSize);
    }
}
