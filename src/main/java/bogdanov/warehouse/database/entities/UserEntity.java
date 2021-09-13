package bogdanov.warehouse.database.entities;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.beans.Transient;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

@Entity
@Data
@Table(name = "users")
public class UserEntity implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private boolean isEnabled = false;

    @ManyToMany(fetch = FetchType.EAGER)
    private Collection<RoleEntity> roles = new HashSet<>();

    @OneToOne(fetch = FetchType.EAGER)
    private PersonEntity person;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return getRoles();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

}
