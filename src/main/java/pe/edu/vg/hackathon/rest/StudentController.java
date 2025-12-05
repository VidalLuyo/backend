package pe.edu.vg.hackathon.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.vg.hackathon.model.User;
import pe.edu.vg.hackathon.model.student;
import pe.edu.vg.hackathon.repository.StudentRepository;

import java.util.List;

@RestController
@RequestMapping("/v1/api/student")
public class StudentController {

    @Autowired
    private StudentRepository studentRepository;

    @GetMapping
    public List<student> getUsers() {
        return studentRepository.findAll();
    }

    @PostMapping
    public student createUser(@RequestBody student user) {
        return studentRepository.save(user);
    }

    @GetMapping("/{id}")
    public student getUser(@PathVariable Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @PutMapping("/{id}")
    public student updateUser(@PathVariable Long id, @RequestBody student user) {
        student existingUser = studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found"));
        existingUser.setDni(user.getDni());
        existingUser.setFirstName(user.getFirstName());
        existingUser.setLastName(user.getLastName());
        existingUser.setPromotion(user.getPromotion());
        existingUser.setFecha(user.getFecha());
        return studentRepository.save(existingUser);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        studentRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

}
