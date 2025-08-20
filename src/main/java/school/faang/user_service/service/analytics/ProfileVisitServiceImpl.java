package school.faang.user_service.service.analytics;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.analytics.ProfileVisitCreateDto;
import school.faang.user_service.dto.analytics.ProfileVisitViewDto;
import school.faang.user_service.mapper.analytics.ProfileVisitMapper;
import school.faang.user_service.repository.analytics.ProfileVisitRepository;
import school.faang.user_service.repository.user.UserRepository;

import java.util.List;

/**
 * Реализация сервиса {@link ProfileVisitService} для работы с визитами профиля.
 * <p>
 * Отвечает за:
 * <ul>
 *     <li>сохранение факта посещения профиля пользователем,</li>
 *     <li>получение истории посетителей конкретного пользователя с пагинацией.</li>
 * </ul>
 * </p>
 *
 * @author Myrza
 * @since 19.08.2025
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ProfileVisitServiceImpl implements ProfileVisitService {
    private final ProfileVisitRepository visitRepo;
    private final UserRepository userRepo;
    private final ProfileVisitMapper mapper;

    @Override
    @Transactional
    public void addVisit(ProfileVisitCreateDto visit) {
        log.info("add visit {}", visit);
        var visitor = userRepo.getByIdOrThrow(visit.visitorId());
        var visited = userRepo.getByIdOrThrow(visit.visitedId());
        var entity = mapper.toEntity(visit);
        log.info("entity: {}", entity);
        entity.setVisitor(visitor);
        entity.setVisited(visited);
        visitRepo.save(entity);
    }

    @Override
    public List<ProfileVisitViewDto> getUserVisitors(Long visitedId, int limit, int page) {
        log.info("limit {} page {}", limit, page);
        var pageable = PageRequest.of(page, limit);
        var visits = visitRepo.findAllByVisitedIdOrderByVisitedAtDesc(visitedId, pageable);
        return mapper.toDtoList(visits.getContent());
    }
}
