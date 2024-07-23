package org.example.demo1207.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

@Data
@Builder
@Entity
@Table(name="users")
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @UuidGenerator
    private String id;
    private String firstName;
    private String lastName;
    @Column(unique=true)
    private String email;
    private String password;
}
