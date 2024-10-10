package school.faang.user_service.service;

import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class PaginationService {

    public <T> List<T> applyPagination(List<T> items, int page, int pageSize) {
        if (page <= 0 || pageSize <= 0) {
            return items;
        }
        int offset = (page - 1) * pageSize;
        int toIndex = Math.min(offset + pageSize, items.size());
        if (offset >= items.size()) {
            return Collections.emptyList();
        }
        return items.subList(offset, toIndex);
    }
}
