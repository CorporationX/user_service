package school.faang.user_service.util.multipart;

import net.coobird.thumbnailator.Thumbnails;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

@Component
public class MultipartFileCopyUtil {

    public MultipartFile compressionMultipartFile(MultipartFile multipartFile, int maxSize) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        Thumbnails.of(multipartFile.getInputStream())
                .size(maxSize, maxSize)
                .toOutputStream(byteArrayOutputStream);

        byte[] compressedBytes = byteArrayOutputStream.toByteArray();

        return MultipartFileFactory.create(
                compressedBytes,
                multipartFile.getName(),
                multipartFile.getOriginalFilename(),
                multipartFile.getContentType()
        );
    }
}
