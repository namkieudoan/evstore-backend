package com.project.shopapp.models;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
@Entity
@Table(name = "social_accounts")
@Data //toString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SocialAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "provider",length = 20,nullable = false)
    private String provider;

    @Column(name = "provider_id", length = 100,nullable = false)
    private String providerId;

    @Column(name = "name", length = 150)
    private String name;

    @Column(name = "email", length = 150)
    private String email;

    @ManyToOne
    @JoinColumn(name ="user_id")
    private User user;

}
