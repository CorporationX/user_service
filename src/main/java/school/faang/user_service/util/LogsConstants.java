package school.faang.user_service.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public final class LogsConstants {

    // Сообщения об ошибках валидации
    public static final String NULL_TITLE = "Отсутствует название";
    public static final String NULL_ID = "Отсутствует id";
    public static final String NULL_START_DATE = "Отсутствует дата начала";
    public static final String WRONG_USER_ID = "Невалидный id пользователя";
    public static final String WRONG_START_DATE = "Дата начала не может быть в прошлом";
    public static final String WRONG_END_DATE = "Дата окончания должна быть в будущем";
    public static final String NOT_POSITIVE_NUMBER = "Число должно быть больше 0";
    public static final String TEXT_LIMIT_FROM_3_TO_255 = "Название должно содержать от 3 до 255 символов";
    public static final String TEXT_LIMIT_TO_2000 = "Описание не должно превышать 2000 символов";

    // Сообщения об ошибках при работе с Event
    public static final String EVENT_NOT_FOUND = "Ивент с id %d не найден";
    public static final String NOT_ENOUGH_SKILLS =
            "Недостаточно навыков для создания данного мероприятия. Отсутствуют навыки: %s";

    // Сообщения об ошибках при работе с User
    public static final String USER_NOT_FOUND = "Пользователь с id %d не найден";
}
