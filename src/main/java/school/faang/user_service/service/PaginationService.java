package school.faang.user_service.service;

import java.util.List;

public interface PaginationService {
    <T> List<T> applyPagination(List<T> items, int page, int pageSize);
}
