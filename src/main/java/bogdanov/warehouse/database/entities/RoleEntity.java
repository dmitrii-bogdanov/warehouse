package bogdanov.warehouse.database.entities;

import bogdanov.warehouse.database.enums.Role;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;
import java.util.Collection;
import java.util.Locale;

@Entity
@Getter
@Setter
@ToString
@EqualsAndHashCode
@Table(name = "roles")
public class RoleEntity implements GrantedAuthority {

    @Id
    @GeneratedValue
    private long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Transient
    @ManyToMany(mappedBy = "roles")
    private Collection<UserEntity> user;

    protected RoleEntity() {
        super();
    }

    public RoleEntity(Role role) {
        this(role.getId(), role.name());
    }

    public RoleEntity(long id, String name){
        this.id = id;
        setName(name);
    }

    public void setName(String name) {
        this.name = name.toUpperCase(Locale.ROOT);
    }

    @Override
    public String getAuthority() {
        return getName();
    }
}
