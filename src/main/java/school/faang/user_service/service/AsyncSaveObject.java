package school.faang.user_service.service;

public interface AsyncSaveObject<T> {

    String acceptSavingAndGetKey(T savedT) throws Exception;
}
