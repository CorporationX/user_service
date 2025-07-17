package school.faang.user_service.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ErrorCode {
    UNEXPECTED_ERROR(10000, "Непредвиденная ошибка"),
    DB_TRANSACTION_ERROR(10001, "Ошибка при записи изменений в БД"),
    JSON_STRUCT_ERROR(10002, "Ошибка в структуре JSON"),
    JSON_PROCESSING_ERROR(10003, "Ошибка при обработке JSON"),
    BUSINESS_ERROR(11000, "Ошибка бизнес логики"),
    GOALS_MORE_THAN_MAXIMUM(11001, "Превышено число максимально допустимых целей для пользователя"),
    SKILL_NOT_EXISTS(11002, "Указанный навык не существует"),
    DEADLINE_IN_PAST(11003, "Дедлайн не может быть в прошлом"),
    DEADLINE_GREATER_PARENT(11004, "Дедлайн подцели не может быть позже родительской цели"),
    DATA_NOT_FOUND(12000, "Данные не найдены"),
    MENTOR_NOT_EXISTS(12001, "Ментор с указанным ID не существует"),
    MENTOR_NOT_ACTIVE(12002, "Ментор с указанным ID деактивирован"),
    MENTOR_EQUAL_USER(12003, "Пользователь не может самому себе быть ментором"),
    PARENT_NOT_EXISTS(12004, "Родительская цель с указанным ID не существует"),
    PARENT_NOT_ACTIVE(12005, "Родительская цель с указанным ID уже выполнена"),
    USER_NOT_EXISTS(12006, "Пользователь не существует"),
    USER_NOT_ACTIVE(12007, "Пользователь деактивирован"),
    GOAL_NOT_EXISTS(12008, "Цель не существует"),
    GOAL_COMPLETED(12009, "Цель уже выполнена"),
    REQUEST_VALIDATION_ERROR(13000, "Ошибка валидации"),
    VALIDATION_REQUIRED(13001, "Не указан обязательный параметр"),
    VALIDATION_EMPTY(13002, "Параметр не должен быть пустым"),
    ;

    private final Integer code;
    private final String description;
}
