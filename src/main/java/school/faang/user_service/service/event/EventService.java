package school.faang.user_service.service.event;

import jakarta.persistence.criteria.Predicate;
import liquibase.util.StringUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.event.EventCreateEditDto;
import school.faang.user_service.dto.event.EventReadDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.filter.event.EventFilterDto;
import school.faang.user_service.mapper.event.EventCreateEditMapper;
import school.faang.user_service.mapper.event.EventReadMapper;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.valitator.event.EventCreateEditValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventService {

    private final EventRepository repository;
    private final EventCreateEditMapper createEditMapper;
    private final EventReadMapper readMapper;
    private final EventCreateEditValidator validator;
    private final EventReadMapper eventReadMapper;

    @Transactional
    public EventReadDto create(EventCreateEditDto eventDto) {
        var validationResult = validator.validate(eventDto);
        if (validationResult.hasErrors()) {
            throw new DataValidationException(validationResult.getErrors());
        }
        var event = createEditMapper.map(eventDto);
        repository.save(event);
        return readMapper.map(event);
    }

    public List<EventReadDto> findAll(EventFilterDto filter) {
        return repository.findAll((root, cq, cb) -> {
                    List<Predicate> predicates = new ArrayList<>();
                    if (StringUtil.isNotEmpty(filter.getTitle())) {
                        predicates.add(cb.like(root.get("title"), "%" + filter.getTitle() + "%"));
                    }
                    return cb.and(predicates.toArray(new Predicate[predicates.size()]));
                }).stream()
                .map(eventReadMapper::map)
                .toList();
    }

    public Optional<EventReadDto> findById(Long id) {
        return repository.findById(id)
                .map(eventReadMapper::map);
    }

    public boolean delete(Long id) {
        return repository.findById(id)
                .map(entity -> {
                    repository.delete(entity);
                    repository.flush();
                    return true;
                })
                .orElse(false);
    }
}
