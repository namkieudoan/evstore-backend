package com.project.shopapp.models;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "users")
@Data //toString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "fullname",length = 100,nullable = false)
    private String fullName;

    @Column(name = "phone_number", length = 10,nullable = false)
    private String phone_number;

    @Column(name = "address", length = 200)
    private String address;

    @Column(name = "password", length = 200,nullable = false)
    private String password;

    private boolean active;

    @Column(name = "date_of_birth")
    private Date date_of_birth;

    @Column(name = "facebook_account_id")
    private int facebook_account_id;

    @Column(name = "google_account_id")
    private int google_account_id;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;

}
