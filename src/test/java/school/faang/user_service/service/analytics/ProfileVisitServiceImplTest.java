package school.faang.user_service.service.analytics;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import school.faang.user_service.dto.analytics.ProfileVisitCreateDto;
import school.faang.user_service.dto.analytics.ProfileVisitViewDto;
import school.faang.user_service.entity.analytics.ProfileVisit;
import school.faang.user_service.mapper.analytics.ProfileVisitMapper;
import school.faang.user_service.repository.analytics.ProfileVisitRepository;
import school.faang.user_service.repository.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static school.faang.user_service.service.analytics.ProfileVisitServiceTestData.buildUser;
import static school.faang.user_service.service.analytics.ProfileVisitServiceTestData.toEntity;

@ExtendWith(MockitoExtension.class)
class ProfileVisitServiceImplTest {
    @Mock
    private ProfileVisitRepository visitRepo;
    @Mock
    private UserRepository userRepo;
    @Mock
    private ProfileVisitMapper mapper;
    @InjectMocks
    private ProfileVisitServiceImpl service;

    @Test
    @DisplayName("успушное добавление записи о посещении")
    void addVisit() {
        var visitor = buildUser(1L);
        var visited = buildUser(2L);
        var visitedAt = LocalDateTime.now();
        var visit = new ProfileVisitCreateDto(visitor.getId(), visited.getId(), visitedAt);
        var entityBeforeSet = toEntity(visit);
        when(userRepo.getByIdOrThrow(visitor.getId())).thenReturn(visitor);
        when(userRepo.getByIdOrThrow(visited.getId())).thenReturn(visited);
        when(mapper.toEntity(visit)).thenReturn(entityBeforeSet);
        service.addVisit(visit);
        verify(visitRepo).save(argThat(saved ->
                saved.getVisitor().getId().equals(visitor.getId())
                        && saved.getVisited().getId().equals(visited.getId())
                        && saved.getVisitedAt().equals(visitedAt)
        ));
    }

    @Test
    @DisplayName("успешное получение посетителей пользователя с пагинацией")
    void getUserVisitors_success() {
        // given
        long visitedId = 2L;
        int limit = 10;
        int offset = 0;

        var visit1 = new ProfileVisit();
        visit1.setId(1L);
        var visit2 = new ProfileVisit();
        visit2.setId(2L);

        List<ProfileVisit> visits = List.of(visit1, visit2);
        Page<ProfileVisit> page = new PageImpl<>(visits);

        var dto1 = new ProfileVisitViewDto(1L, 10L, 2L, LocalDateTime.now());
        var dto2 = new ProfileVisitViewDto(2L, 11L, 2L, LocalDateTime.now());

        List<ProfileVisitViewDto> dtoList = List.of(dto1, dto2);
        var pageable = PageRequest.of(offset, limit);
        when(visitRepo.findAllByVisitedIdOrderByVisitedAtDesc(eq(visitedId), eq(pageable)))
                .thenReturn(page);
        when(mapper.toDtoList(visits)).thenReturn(dtoList);

        // when
        var result = service.getUserVisitors(visitedId, limit, offset);

        // then
        assertThat(result).containsExactlyElementsOf(dtoList);

        verify(visitRepo).findAllByVisitedIdOrderByVisitedAtDesc(visitedId, pageable);
        verify(mapper).toDtoList(visits);
    }
}