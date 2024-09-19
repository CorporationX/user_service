package school.faang.user_service.service;

public interface AmazonS3Service<T> {

    String uploadFileAndGetKey(byte[] file, T forT);
}
