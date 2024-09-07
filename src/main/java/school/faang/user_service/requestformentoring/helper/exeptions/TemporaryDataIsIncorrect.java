package school.faang.user_service.requestformentoring.helper.exeptions;

public class TemporaryDataIsIncorrect extends RuntimeException {

    public void TemporaryDataIsIncorrect() {
        System.out.println("3 months must have passed since the request was submitted!");
    }
}
