package school.faang.user_service.S3;

import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.entity.User;

import java.io.IOException;
import java.io.InputStream;

public interface S3Service {
    // Метод для загрузки файла в Amazon S3 и связывания его с пользователем
    User uploadFile(MultipartFile file, String folder) throws IOException;

    // Метод для удаления файла из Amazon S3
    void deleteFile(String folder, String key) throws IOException;

    // Метод для загрузки файла из Amazon S3
    InputStream downloadFile(String folder, String key) throws IOException;

    String[] saveResizedImages(MultipartFile file, String folder) throws IOException;

}
