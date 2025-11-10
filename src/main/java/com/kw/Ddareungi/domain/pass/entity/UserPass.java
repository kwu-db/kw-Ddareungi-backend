package com.kw.Ddareungi.domain.pass.entity;

import com.kw.Ddareungi.domain.model.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.usertype.UserType;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class UserPass extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false, name = "user_pass_id")
    private Long id;

    private LocalDate activatedDate;
    private LocalDate expiredDate;
    @Enumerated(EnumType.STRING)
    private UserPassStatus userPassStatus;
}
