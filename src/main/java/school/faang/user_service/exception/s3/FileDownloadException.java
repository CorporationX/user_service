package school.faang.user_service.exception.s3;

public class FileDownloadException extends RuntimeException{

    public FileDownloadException(String message) {
        super(message);
    }
}
