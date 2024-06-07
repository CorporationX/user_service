package school.faang.user_service.service.user;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class CSVPart {
    private List<String> lines;
}
