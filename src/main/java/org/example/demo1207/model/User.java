package org.example.demo1207.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.demo1207.dto.UserDto;
import org.hibernate.annotations.UuidGenerator;

import java.util.Optional;

@Data
@Builder
@Entity
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @UuidGenerator
    private String id;
    private String firstName;
    private String lastName;
    @Column(unique = true)
    private String email;
    private String password;

    public UserDto mapUserToDto() {
        UserDto userDto = new UserDto();
        userDto.setId(id);
        userDto.setName(firstName + " " + lastName);
        userDto.setEmail(email);
        return userDto;
    }
}
