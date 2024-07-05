package school.faang.user_service.service.user.parse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Класс олицетворяющий часть файла.
 * Каждый List<String> включает строки файла.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Component
public class CsvPart {

    private List<String> lines;
}
