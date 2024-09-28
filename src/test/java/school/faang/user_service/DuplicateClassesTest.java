package school.faang.user_service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class DuplicateClassesTest {

    private final static String FOLDER_PATH = "school.faang.user_service";

    private final static int ZERO_DUPS = 0;

    @Test
    @DisplayName("Check for dubs")
    void whenDuplicateClassesExistsThenThrowException() {
        assertEquals(ZERO_DUPS, checkForDuplicateClasses(FOLDER_PATH).size());
    }

    private static Map<String, Integer> checkForDuplicateClasses(String packageName) {
        String path = packageName.replace('.', '/');
        File directory = new File("src/main/java/" + path);
        Map<String, Integer> classMap = new HashMap<>();

        if (directory.exists()) {
            findClasses(directory, classMap);
        }

        Iterator<Map.Entry<String, Integer>> iterator = classMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Integer> entry = iterator.next();
            if (entry.getValue() <= 1) {
                iterator.remove();
            }
        }

        for (Map.Entry<String, Integer> entry : classMap.entrySet()) {
            if (entry.getValue() > 1) {
                System.out.println("Duplicate class found: " + entry.getKey());
            }
        }

        return classMap;
    }

    private static void findClasses(File directory, Map<String, Integer> classMap) {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    findClasses(file, classMap);
                } else if (file.isFile() && file.getName().endsWith(".java")) {
                    String className = file.getName().replace(".java", "");
                    classMap.put(className, classMap.getOrDefault(className, 0) + 1);
                }
            }
        }
    }
}
