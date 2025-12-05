package pe.edu.vg.hackathon.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.vg.hackathon.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}