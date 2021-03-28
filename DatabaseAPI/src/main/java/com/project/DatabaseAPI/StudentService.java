package com.project.DatabaseAPI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentService {

	@Autowired
	private StudentRepository studentRepository;

	public List<Student> getAllStudents() {
		return studentRepository.findAll();
	}

  public Student getStudent(Integer id) {
    return studentRepository.findById(id).get();
  }

	public void addStudent(Student student) {
		studentRepository.save(student);
	}

  public void updateStudent(Integer id, Student updatedStudent) {
    Student existingStudent = getStudent(id);
    if (existingStudent != null) {
      studentRepository.save(updatedStudent);
    }
  }

	public void deleteStudent(Integer id) {
		studentRepository.deleteById(id);
	}
}
