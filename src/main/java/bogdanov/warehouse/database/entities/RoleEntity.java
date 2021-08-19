package bogdanov.warehouse.database.entities;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;
import java.util.Collection;

@Entity
@Getter
@Setter
@Table(name = "roles")
public class RoleEntity implements GrantedAuthority {

    @Id
    private long id;

    @Column(nullable = false)
    private String name;

    @Transient
    @ManyToMany(mappedBy = "roles")
    private Collection<UserEntity> user;

    protected RoleEntity() {
        super();
    }

    public RoleEntity(ROLE role) {
        this.id = role.getId();
        this.name = role.name();
    }

    @Override
    public String getAuthority() {
        return getName();
    }
}
