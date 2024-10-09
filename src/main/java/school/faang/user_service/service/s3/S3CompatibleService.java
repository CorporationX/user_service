package school.faang.user_service.service.s3;

public interface S3CompatibleService {
    void uploadFile(byte[] fileData, String fileKey, String contentType);
    byte[] downloadFile(String fileKey);
    void deleteFile(String fileKey);
}
