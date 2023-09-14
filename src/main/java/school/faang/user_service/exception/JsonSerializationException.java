package school.faang.user_service.exception;

import java.text.MessageFormat;

public class JsonSerializationException extends RuntimeException{
    private String objectType;

    public JsonSerializationException(Object object){
        this.objectType = object.getClass().getSimpleName();
    }

    @Override
    public String getMessage() {
        return MessageFormat.format("Problem with serializing object of type {0} to JSON format", objectType);
    }

    @Override
    public String toString() {
        return "JsonSerializationException{" +
               "objectType='" + objectType + '\'' +
               ", message='" + getMessage() + '\'' +
               '}';
    }
}