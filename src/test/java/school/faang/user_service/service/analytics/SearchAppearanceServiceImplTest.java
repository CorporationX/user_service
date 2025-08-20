package school.faang.user_service.service.analytics;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import school.faang.user_service.dto.analytics.SearchAppearanceCreateDto;
import school.faang.user_service.dto.analytics.SearchAppearanceViewDto;
import school.faang.user_service.entity.analytics.SearchAppearance;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.mapper.analytics.SearchAppearanceMapper;
import school.faang.user_service.repository.analytics.SearchAppearanceRepository;
import school.faang.user_service.repository.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SearchAppearanceServiceImplTest {
    @Mock
    private SearchAppearanceRepository searchAppearanceRepo;
    @Mock
    private UserRepository userRepo;
    @Mock
    private SearchAppearanceMapper mapper;
    @InjectMocks
    private SearchAppearanceServiceImpl service;

    @Test
    void addSearchAppearance() {
        var searcher = new User();
        searcher.setId(1L);
        var searched = new User();
        searched.setId(2L);
        var dto = new SearchAppearanceCreateDto(1L, 2L, LocalDateTime.now());
        var entity = new SearchAppearance();

        when(userRepo.getByIdOrThrow(1L)).thenReturn(searcher);
        when(userRepo.getByIdOrThrow(2L)).thenReturn(searched);
        when(mapper.toEntity(dto)).thenReturn(entity);

        service.addSearchAppearance(dto);

        assertThat(entity.getSearcher()).isEqualTo(searcher);
        assertThat(entity.getSearched()).isEqualTo(searched);
    }

    @Test
    void getUserSearchAppearance() {
        var searchedId = 10L;
        var page = 0;
        var limit = 2;

        var entity1 = new SearchAppearance();
        entity1.setId(1L);
        var entity2 = new SearchAppearance();
        entity2.setId(2L);

        Page<SearchAppearance> pageResult =
                new PageImpl<>(List.of(entity1, entity2), PageRequest.of(page, limit), 2);

        when(searchAppearanceRepo.findAllBySearchedIdOrderBySearchedAtDesc(searchedId, PageRequest.of(page, limit)))
                .thenReturn(pageResult);

        var dtoList = List.of(
                new SearchAppearanceViewDto(1L, 1L, searchedId, LocalDateTime.now()),
                new SearchAppearanceViewDto(2L, 2L, searchedId, LocalDateTime.now())
        );
        when(mapper.toDtoList(pageResult.getContent())).thenReturn(dtoList);

        var result = service.getUserSearchAppearance(searchedId, limit, page);

        assertThat(result).hasSize(2);
        assertThat(result).isEqualTo(dtoList);
    }
}