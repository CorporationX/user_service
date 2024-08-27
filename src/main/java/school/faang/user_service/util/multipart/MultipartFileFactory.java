package school.faang.user_service.util.multipart;

import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public final class MultipartFileFactory {

    private MultipartFileFactory() {
    }

    public static MultipartFile create(byte[] fileContent, String name, String originalFilename, String contentType) {
        return new MultipartFile() {
            @Override
            public String getName() {
                return name;
            }

            @Override
            public String getOriginalFilename() {
                return originalFilename;
            }

            @Override
            public String getContentType() {
                return contentType;
            }

            @Override
            public boolean isEmpty() {
                return fileContent.length == 0;
            }

            @Override
            public long getSize() {
                return fileContent.length;
            }

            @Override
            public byte[] getBytes() {
                return fileContent;
            }

            @Override
            public InputStream getInputStream() {
                return new ByteArrayInputStream(fileContent);
            }

            @Override
            public void transferTo(File dest) throws IOException, IllegalStateException {
                try (OutputStream fileOutputStream = new FileOutputStream(dest)) {
                    fileOutputStream.write(fileContent);
                }
            }
        };
    }
}
