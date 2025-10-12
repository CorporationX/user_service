package school.faang.user_service.service.event;

import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.dto.event.UpdateEventDto;
import java.util.List;

/**
 * Сервис для управления событиями.
 * Предоставляет методы для создания, обновления, фильтрации и удаления событий.
 */
public interface EventService {

    /**
     * Создаёт новое событие на основе переданных данных.
     * Преобразует DTO в сущность, сохраняет её и возвращает DTO созданного события.
     *
     * @param eventDto объект с данными нового события
     * @return DTO созданного события
     */
    EventDto create(EventDto eventDto);

    /**
     * Обновляет существующее событие по идентификатору.
     * Загружает сущность из репозитория, применяет обновления и возвращает обновлённое событие.
     *
     * @param eventId идентификатор обновляемого события
     * @param updateEventDto DTO с новыми данными для события
     * @return DTO обновлённого события
     * @throws IllegalArgumentException если событие не найдено
     */
    EventDto update(long eventId, UpdateEventDto updateEventDto);

    /**
     * Возвращает список событий, отфильтрованных по заданным критериям.
     * Каждый фильтр из цепочки применяется к списку событий.
     *
     * @param eventFilterDto DTO с параметрами фильтрации
     * @return список DTO событий, соответствующих фильтрам
     */
    List<EventDto> getByFilters(EventFilterDto eventFilterDto);

    /**
     * Удаляет событие по его идентификатору.
     *
     * @param eventId идентификатор удаляемого события
     */
    void delete(long eventId);
}


