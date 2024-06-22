package school.faang.user_service.service.user.parse;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@AllArgsConstructor
@Component
public class CsvPart {

    private List<String> lines;
}
