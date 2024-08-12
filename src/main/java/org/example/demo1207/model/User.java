package org.example.demo1207.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.demo1207.dto.UserDto;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
@Builder
@Entity
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
public class User implements UserDetails {
    @Id
    @UuidGenerator
    private String id;
    private String firstname;
    private String lastname;
    @Column(unique = true)
    private String email;
    private String password;
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    private Set<Role> roles = new HashSet<>();

    public UserDto mapUserToDto() {
        UserDto userDto = new UserDto();
        userDto.setId(id);
        userDto.setName(firstname + " " + lastname);
        userDto.setEmail(email);
        return userDto;
    }

    public User modifyRole(String roleModification) {
        if (roles == null) {
            roles = new HashSet<>();
        }
        roleModification = roleModification.trim().toUpperCase();
        boolean isAddition = roleModification.startsWith("+");
        boolean isRemoval = roleModification.startsWith("-");
        String roleName = roleModification.substring(1).trim();
        if (!roleName.startsWith("ROLE_")) {
            roleName = "ROLE_" + roleName;
        }
        Role role;
        try {
            role = Role.valueOf(roleName);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid role: " + roleName);
        }
        if (isAddition) {
            this.roles.add(role);
        } else if (isRemoval) {
            this.roles.remove(role);
        }
        return this;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream().map(role -> new SimpleGrantedAuthority(role.name())).collect(Collectors.toList());
    }

    @Override
    public String getUsername() {
        return email;
    }
}
