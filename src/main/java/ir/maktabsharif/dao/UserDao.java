package ir.maktabsharif.dao;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "blog_users", uniqueConstraints = {
        @UniqueConstraint(name = "uk_blog_users_username", columnNames = "username"),
        @UniqueConstraint(name = "uk_blog_users_email", columnNames = "email")
})
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 30)
    private String username;

    @Column(nullable = false, length = 254)
    private String email;

    @Column(name = "password", nullable = false, length = 512)
    private String password;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

}
