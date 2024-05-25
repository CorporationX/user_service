package school.faang.user_service.exception;

public class ParseFileException extends RuntimeException {
    public ParseFileException(String csvFileIsEmpty) {
        super(csvFileIsEmpty);
    }
}
