package school.faang.user_service.service.s3;

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

        return new MultipartFile() {
            @Override
            public String getName() {
                return multipartFile.getName();
            }

            @Override
            public String getOriginalFilename() {
                return multipartFile.getOriginalFilename();
            }

            @Override
            public String getContentType() {
                return multipartFile.getContentType();
            }

            @Override
            public boolean isEmpty() {
                return byteArrayOutputStream.size() == 0;
            }

            @Override
            public long getSize() {
                return byteArrayOutputStream.size();
            }

            @Override
            public byte[] getBytes() throws IOException {
                return byteArrayOutputStream.toByteArray();
            }

            @Override
            public InputStream getInputStream() throws IOException {
                return new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
            }

            @Override
            public void transferTo(File dest) throws IOException, IllegalStateException {
                Thumbnails.of(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()))
                        .size(maxSize, maxSize)
                        .toFile(dest);
            }
        };

    }
}
