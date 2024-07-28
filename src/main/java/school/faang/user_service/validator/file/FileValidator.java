package school.faang.user_service.validator.file;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class FileValidator {

    private static final String CSV_EXTENSION = "csv";

    public void validateFile(MultipartFile file) {
        String fileName = file.getOriginalFilename();
        if (fileName == null || !fileName.toLowerCase().endsWith("." + CSV_EXTENSION)) {
            throw new IllegalArgumentException("File must have a .csv extension!");
        }

        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty!");
        }
    }
}
