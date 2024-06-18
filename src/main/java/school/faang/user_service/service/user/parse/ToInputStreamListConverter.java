package school.faang.user_service.service.user.parse;

import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Component
public class ToInputStreamListConverter {

    public List<InputStream> convertToInputStreamList(List<CSVPart> parts) {
        List<StringBuilder> stringBuilders = new ArrayList<>();
        List<InputStream> inputStreams = new ArrayList<>();
        for (CSVPart part : parts) {
            List<String> lines = part.getLines();
            StringBuilder sb = new StringBuilder();
            for (String line : lines) {
                sb.append(line).append(System.lineSeparator());
            }
            stringBuilders.add(sb);
        }
        for (StringBuilder stringBuilder : stringBuilders) {
            byte[] bytes = stringBuilder.toString().getBytes(StandardCharsets.UTF_8);
            inputStreams.add(new ByteArrayInputStream(bytes));
        }
        return inputStreams;
    }
}
