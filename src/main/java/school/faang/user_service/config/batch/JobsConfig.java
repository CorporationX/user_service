package school.faang.user_service.config.batch;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "spring.batch.job.jobs")
@Data
public class JobsConfig {

    private JobProps userReindexingJob;

    @Data
    public static class JobProps {
        private String name;
        private int chunkSize;
        private int gridSize;
        private ReaderProps reader;
    }

    @Data
    public static class ReaderProps {
        private String name;
        private String methodName;
        private List<SortProps> sorts;
    }

    @Data
    public static class SortProps {
        private String field;
        private Sort.Direction direction;
    }
}
