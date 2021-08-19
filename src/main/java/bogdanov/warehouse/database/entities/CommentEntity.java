package bogdanov.warehouse.database.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
public class CommentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String text;

    @Transient
    @OneToOne(mappedBy = "comment")
    private OrderEntity order;

    @Transient
    @OneToOne(mappedBy = "comment")
    private OperationEntity operation;

}