package pe.edu.vg.hackathon.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "student")
public class student {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String dni;
    private String firstName;
    private String lastName;
    private int promotion;
    private LocalDateTime fecha = LocalDateTime.now();
}
