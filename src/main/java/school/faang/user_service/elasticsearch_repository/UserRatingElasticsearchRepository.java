package school.faang.user_service.elasticsearch_repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import org.springframework.stereotype.Repository;
import school.faang.user_service.entity.UserRating;


@Repository
public interface UserRatingElasticsearchRepository extends ElasticsearchRepository<UserRating, Long> {
    Page<UserRating> findByUsernameContaining(String username, Pageable pageable);

    //в конфиге покопаться или в зависимотях и разобраться с @Query, на крайняк string query
    //@Query("{\"bool\": {\"must\": [{\"match\": {\"username\": {\"query\": \"?0\", \"operator\": \"and\"}}}], \"sort\": [{\"score\": {\"order\": \"desc\"}}]}}")
    //Page<UserRating> findByUsernameUsingCustomQuery(String username, Pageable pageable);
}