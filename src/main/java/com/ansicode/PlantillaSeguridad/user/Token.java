package com.ansicode.PlantillaSeguridad.user;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity

public class Token {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String token;


    private LocalDateTime fechaCreacion;

    private LocalDateTime fechaExpiracion;

    private LocalDateTime fechaValidacion;

    @ManyToOne
    @JoinColumn(name = "userId",nullable = false)
    private User user;

}
