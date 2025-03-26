package school.faang.user_service.exception;

public class MinioUploadException extends RuntimeException {
    private static final String MINIO_UPLOAD_EXCEPTION_MSG = "Error while uploading avatar to MinIO";

    public MinioUploadException() {
        super(MINIO_UPLOAD_EXCEPTION_MSG);
    }
}
