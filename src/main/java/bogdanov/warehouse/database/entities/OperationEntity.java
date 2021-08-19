package bogdanov.warehouse.database.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Entity
@Getter
@Setter
@Table(name = "operations")
public class OperationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    private OrderEntity order;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    private OperationTypeEntity type;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    private OperationStatusEntity status;

    @ElementCollection
    private Map<NomenclatureEntity, Integer> operationItems = new HashMap<>();

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    private UserEntity createdBy;

    private LocalDateTime timeCreated;

    @ManyToOne(fetch = FetchType.EAGER)
    private UserEntity acceptedBy;

    private LocalDateTime timeAccepted;

    @ManyToOne(fetch = FetchType.EAGER)
    private UserEntity completedBy;

    private LocalDateTime timeCompleted;

    @ManyToOne(fetch = FetchType.EAGER)
    private UserEntity approvedBy;

    private LocalDateTime timeApproved;

    @ManyToOne(fetch = FetchType.EAGER)
    private UserEntity abortedBy;

    private LocalDateTime timeAborted;

    @OneToOne(fetch = FetchType.LAZY)
    private CommentEntity comment;

}
