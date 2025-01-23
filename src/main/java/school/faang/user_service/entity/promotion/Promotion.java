package school.faang.user_service.entity.promotion;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "promotion")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Promotion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    @Enumerated(EnumType.STRING)
    private TargetType target;

    @OneToOne(cascade = CascadeType.ALL)
    private PromotionPlan plan;

    private int impressionsLimit;
    private int currentImpressions;
    private boolean isActive;

    private LocalDateTime startTime;

    public boolean isActive() {
        return currentImpressions < impressionsLimit;
    }

    public void activate() {
        this.isActive = true;
        this.startTime = LocalDateTime.now();
    }
}