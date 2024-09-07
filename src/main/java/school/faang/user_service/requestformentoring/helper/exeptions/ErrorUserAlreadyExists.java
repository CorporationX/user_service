package school.faang.user_service.requestformentoring.helper.exeptions;

public class ErrorUserAlreadyExists extends RuntimeException {

    public ErrorUserAlreadyExists(String name) {
        super("Такой пользователь уже является вашим рецензентом! " + name);
    }
}
