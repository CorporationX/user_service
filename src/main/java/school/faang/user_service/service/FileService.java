package school.faang.user_service.service;

import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

@Slf4j
@Component
public class FileService {

    public File resizeImageFile(File inputFile, int targetWidth, int targetHeight, String outputFileName) {
        File outputFile = new File(outputFileName);
        try {
            Thumbnails.of(inputFile)
                    .size(targetWidth, targetHeight)
                    .toFile(outputFile);
        } catch (IOException e) {
            String errMessage = "Could not resize and convert image file";
            log.error(errMessage, e);
            throw new RuntimeException(errMessage);
        }
        return outputFile;
    }

    public File convertByteArrayToFile(byte[] byteArray, String fileName) {
        File convertedFile = new File(fileName);
        try (FileOutputStream fileOutputStream = new FileOutputStream(convertedFile)) {
            fileOutputStream.write(byteArray);
        } catch (FileNotFoundException e) {
            log.error("Could not findFile during conversion from MultipartFile: to File", e);
            throw new RuntimeException(e);
        } catch (IOException e) {
            log.error("Caught and I/O exception during conversion from MultipartFile: to File", e);
            throw new RuntimeException(e);
        }
        return convertedFile;
    }

    public byte[] convertResponseToByteArray(ResponseEntity<Resource> response) {
        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            try {
                ByteArrayResource byteArrayResource = (ByteArrayResource) response.getBody();
                return byteArrayResource.getByteArray();
            } catch (Exception e) {
                log.error("Failed to read image bytes", e);
                throw new RuntimeException("Failed to read image bytes");
            }
        } else {
            log.error("Failed to fetch avatar HTTP {}", response.getStatusCode());
            throw new RuntimeException("Failed to fetch image: HTTP " + response.getStatusCode());
        }
    }
}
