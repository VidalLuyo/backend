package pe.edu.vg.hackathon.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.vg.hackathon.model.student;


public interface StudentRepository extends JpaRepository<student, Long> {
}
