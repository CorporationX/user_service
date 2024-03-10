package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import school.faang.user_service.elasticsearch_repository.UserRatingElasticsearchRepository;
import school.faang.user_service.entity.UserRating;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserSearchServiceTest {

    @Mock
    private UserRatingElasticsearchRepository repository;

    @InjectMocks
    private UserSearchService service;

    @Test
    void searchUsersByName_shouldReturnUsers() {
        String username = "testUser";
        int page = 0;
        int size = 10;
        UserRating userRating = new UserRating();
        Page<UserRating> expectedPage = new PageImpl<>(Collections.singletonList(userRating));

        when(repository.findByUsernameContaining(eq(username), any(PageRequest.class)))
                .thenReturn(expectedPage);

        Page<UserRating> result = service.searchUsersByName(username, page, size);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0)).isEqualTo(userRating);
    }
}
