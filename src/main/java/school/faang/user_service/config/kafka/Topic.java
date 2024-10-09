package school.faang.user_service.config.kafka;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Topic {
    private String name;
    private int numPartitions;
    private short replicationFactor;
}
