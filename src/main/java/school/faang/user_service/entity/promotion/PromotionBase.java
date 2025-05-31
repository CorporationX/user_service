package school.faang.user_service.entity.promotion;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.promotion.enums.Plan;
import school.faang.user_service.entity.promotion.enums.ViewWidth;
import school.faang.user_service.entity.transaction.Payable;
import school.faang.user_service.entity.transaction.Transaction;
import school.faang.user_service.entity.transaction.TransactionPurpose;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@MappedSuperclass
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class PromotionBase extends Product implements Payable {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id")
    private User client;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany
    @JoinTable(
            name = "transaction_product",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "transaction_id"))
    private List<Transaction> transactions = new ArrayList<>();

    @Column(name = "active")
    private Boolean active;

    @Column(name = "current_views")
    private long currentViews;

    @Column(name = "num_promoted_views")
    private Integer numPromotedViews;

    @Enumerated(EnumType.STRING)
    @Column(name = "view_width")
    private ViewWidth viewWidth;

    @Enumerated(EnumType.STRING)
    @Column(name = "plan")
    private Plan plan;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_purpose")
    private TransactionPurpose transactionPurpose;

    @Column(name = "name")
    private String name;

    @Override
    public TransactionPurpose getPurpose() {
        return TransactionPurpose.PROMOTION;
    }
}
