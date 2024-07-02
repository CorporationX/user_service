package school.faang.user_service.service.user.parse;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class Util {

    public static InputStream getInputStream() throws FileNotFoundException {
        String pathToCsv = "C:\\Users\\vladi\\Desktop\\Programming\\Faang-school\\Module 2,3,4\\CorporationX" +
                "\\user_service\\src\\main\\resources\\files\\students.csv";
        return new FileInputStream(pathToCsv);
    }
}
