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
    public static final String EMPTY_FILTER = "Filter must contain at least one field";

    // Сообщения об ошибках при работе с Event
    public static final String EVENT_NOT_FOUND = "Ивент с id %d не найден";

    // Сообщения об ошибках при работе с User
    public static final String USER_NOT_FOUND = "User with id = %s is not found";

    // Сообщения об ошибках при работе с Skill
    public static final String SKILL_ALREADY_EXIST = "Skill '%s' is already existed";
    public static final String BLANK_SKILL_TITLE = "Skill title can not be blank";
    public static final String USER_HAS_SKILL = "User %s already has skill %s";
    public static final String SKILL_NOT_FOUND = "Skill with id = %s is not found";
    public static final String CONDITION_FOR_OFFERS_AMOUNT_FAILED = "Condition for offers amount failed";
    public static final String NULL_OBJECT_IN_SKILL_MAPPER = "There is null object in SkillMapper";
    public static final String NOT_ENOUGH_SKILLS =
            "Недостаточно навыков для создания данного мероприятия. Отсутствуют навыки: %s";

    // Сообщения об ошибках при работе с Recommendation
    public static final String RECOMMENDATION_NOT_FOUND = "Recommendation is not found";

    //Сообщения при работе с Event
    public static final String DELETED_EVENT_MESSAGE = "Ивент с id %d удалён";
}
