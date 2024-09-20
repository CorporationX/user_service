package school.faang.user_service.util.picture;

import org.junit.jupiter.api.Test;
import school.faang.user_service.util.FileReader;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DefaultProfilePictureReaderTest {

    private final FileReader fileReader = new DefaultProfilePictureReader();

    @Test
    void readFile_ExistsProfilePicture() {
        String filename = "image/default-profile-picture.svg";
        int correctLength = 1988;

        byte[] image = fileReader.readFile(filename);

        assertEquals(correctLength, image.length);
    }

    @Test
    void readFile_NotFoundProfilePicture() {
        String filename = "";
        String correctMessage = "The default avatar was not found";

        Throwable exception = assertThrows(RuntimeException.class,
                () -> fileReader.readFile(filename));

        assertEquals(correctMessage, exception.getMessage());
    }
}