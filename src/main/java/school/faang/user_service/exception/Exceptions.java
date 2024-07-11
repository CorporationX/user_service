package school.faang.user_service.exception;

import org.springframework.stereotype.Component;

@Component
public class Exceptions extends RuntimeException {

    public void validateInputValuesEmptyMessage() {
        throw new DataValidationException("Вы не можете провести такое событие с такими навыками");
    }

    public void validateInputValuesIsNull() {
        throw new DataValidationException("Входящий аргумент не должен быть пустым");
    }

    public DataValidationException findByIdIsNull() {
        return new DataValidationException("Такого события не было найдено");
    }

    public DataValidationException notFindOwnerById() {
        return new DataValidationException("С таким id пользователь не найден");
    }

    public void cantGetUserSkills() {
        throw new DataValidationException("Пользователь не может провести такое событие с такими навыками");
    }

    public DataValidationException notGetOwnedEvents() {
        return new DataValidationException("Не найдены созданные события");
    }

    public DataValidationException notGetParticipatedEvents() {
        return new DataValidationException("Не найдены  пользователю все события, на которые пользователь зарегистрировался");
    }
}
