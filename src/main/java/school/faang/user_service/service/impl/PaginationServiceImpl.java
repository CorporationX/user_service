package school.faang.user_service.service.impl;

import org.springframework.stereotype.Service;
import school.faang.user_service.service.PaginationService;

import java.util.Collections;
import java.util.List;

@Service
public class PaginationServiceImpl implements PaginationService {

    @Override
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
