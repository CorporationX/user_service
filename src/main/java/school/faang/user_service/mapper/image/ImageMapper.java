package school.faang.user_service.mapper.image;

import net.coobird.thumbnailator.Thumbnails;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Component
public class ImageMapper {

    public MultipartFile convertFilePermissions(MultipartFile file, int permission) throws IOException {
        Thumbnails.of(file.getInputStream())
                .size(permission, permission)
                .toFile(file.getOriginalFilename());

        return file;
    }
}
