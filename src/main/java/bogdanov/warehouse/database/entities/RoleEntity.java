package bogdanov.warehouse.database.entities;

import bogdanov.warehouse.database.enums.Role;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;
import java.util.Collection;
import java.util.Locale;

@Entity
@Data
@Table(name = "roles")
@NoArgsConstructor
public class RoleEntity implements GrantedAuthority {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Transient
    @ManyToMany(mappedBy = "roles")
    private Collection<UserEntity> user;

    public RoleEntity(Role role) {
        this(role.getId(), role.name());
    }

    public RoleEntity(Long id, String name) {
        this.id = id;
        setName(name);
    }

    @Override
    public String getAuthority() {
        return getName();
    }
}
