package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import school.faang.user_service.elasticsearch_repository.UserRatingElasticsearchRepository;
import school.faang.user_service.entity.UserRating;

@Service
@RequiredArgsConstructor
public class UserSearchService {
    private final UserRatingElasticsearchRepository repository;

    public Page<UserRating> searchUsersByName(String username, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "score"));
        return repository.findByUsernameContaining(username, pageable);
    }
}
