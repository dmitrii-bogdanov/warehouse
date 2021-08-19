package bogdanov.warehouse.database.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Entity
@Getter
@Setter
@Table(name = "orders")
public class OrderEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    private OrderTypeEntity type;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    private OrderStatusEntity status;

    @ElementCollection
    private Map<NomenclatureEntity, Long> operationItems = new HashMap<>();

    @OneToMany(mappedBy = "order")
    private Collection<OperationEntity> operations = new ArrayList<>();

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    private UserEntity createdBy;

    private LocalDateTime timeCreated;

    @ManyToOne(fetch = FetchType.EAGER)
    private UserEntity acceptedBy;

    private LocalDateTime timeAccepted;

    @ManyToOne(fetch = FetchType.EAGER)
    private UserEntity approvedBy;

    private LocalDateTime timeApproved;

    @ManyToOne(fetch = FetchType.EAGER)
    private UserEntity completedBy;

    private LocalDateTime timeCompleted;

    @ManyToOne(fetch = FetchType.EAGER)
    private UserEntity closedBy;

    private LocalDateTime timeClosed;

    @ManyToOne(fetch = FetchType.EAGER)
    private UserEntity abortedBy;

    private LocalDateTime timeAborted;

    @OneToOne(fetch = FetchType.LAZY)
    private CommentEntity comment;

}
