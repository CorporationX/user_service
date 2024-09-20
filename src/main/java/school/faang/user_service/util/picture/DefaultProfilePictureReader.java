package school.faang.user_service.util.picture;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import school.faang.user_service.util.FileReader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
public class DefaultProfilePictureReader implements FileReader {

    @Override
    public byte[] readFile(String fileName) {
        try {
            ClassPathResource resource = new ClassPathResource(fileName);
            Path path = resource.getFile().toPath();
            return Files.readAllBytes(path);
        } catch (IOException exception) {
            throw new RuntimeException("The default avatar was not found", exception);
        }
    }
}
