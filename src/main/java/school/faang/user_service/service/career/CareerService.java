package school.faang.user_service.service.career;

import school.faang.user_service.dto.career.CareerDto;
import school.faang.user_service.dto.career.CreateCareerDto;
import school.faang.user_service.dto.career.UpdateCareerDto;

/**
 * Сервис для управления данными о карьере пользователей.
 * Предоставляет основные операции для работы с карьерой:
 * добавление, обновление и получение данных о карьере.
 */
public interface CareerService {

    /**
     * Добавляет новые данные о карьере для указанного пользователя.
     * Проверяет валидность данных (дата начала не в будущем,
     * дата окончания не раньше даты начала и т.д.).
     *
     * @param userId  идентификатор пользователя, для которого добавляется карьера
     * @param careerDto DTO с данными для создания карьеры
     * @return CareerDto с данными созданной карьеры, включая присвоенный ID
     * @throws DataValidationException если данные не прошли валидацию
     * @throws RuntimeException если пользователь с указанным ID не найден
     */
    CareerDto addCareer(Long userId, CreateCareerDto careerDto);

    /**
     * Обновляет существующие данные о карьере.
     * Проверяет, что пользователь имеет право на обновление (является владельцем карьеры),
     * а также валидность обновляемых данных.
     *
     * @param userId идентификатор пользователя, выполняющего обновление
     * @param careerId идентификатор карьеры, которую необходимо обновить
     * @param careerDto DTO с обновленными данными карьеры
     * @return CareerDto с обновленными данными карьеры
     * @throws DataValidationException если данные не прошли валидацию
     * @throws ForbiddenException если пользователь не является владельцем карьеры
     * @throws RuntimeException если карьера с указанным ID не найдена
     */
    CareerDto updateCareer(Long userId, Long careerId, UpdateCareerDto careerDto);

    /**
     * Получает данные о карьере по её идентификатору.
     * Проверяет, что пользователь имеет доступ к просмотру карьеры.
     *
     * @param careerId идентификатор карьеры
     * @return CareerDto с данными запрошенной карьеры
     * @throws RuntimeException если карьера с указанным ID не найдена
     */
    CareerDto getById(Long careerId);
}
