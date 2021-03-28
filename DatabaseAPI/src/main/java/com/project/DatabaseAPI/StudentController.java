package com.project.DatabaseAPI;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StudentController {

  @Autowired
  private StudentRepository studentRepository; // do we need this in the controller?


  @Autowired
  private StudentService studentService;

  @GetMapping(path="api/students")
  public List<Student> listStudents() {
	  return studentService.getAllStudents();
  }

  @GetMapping("api/students/{id}")
  public ResponseEntity<Student> getStudent(@PathVariable int id) {
	  try {
		  Student student =  studentService.getStudent(id);
		  return new ResponseEntity<Student>(student, HttpStatus.OK);
	  }
	  catch(NoSuchElementException e) {
		  return new ResponseEntity<Student>(HttpStatus.NOT_FOUND);
	  }
  }

  @PostMapping("api/students")
  public void addStudent(@RequestBody Student student) {
	  studentService.addStudent(student);
  }

  @PutMapping("api/students/{id}")
  public ResponseEntity<?> updateStudent(@RequestBody Student student,
		  					@PathVariable Integer id) {
	  try {
		  studentService.updateStudent(id, student);
		  return new ResponseEntity<>(HttpStatus.OK);
	  }
	  catch (NoSuchElementException e) {
		  return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	  }
  }

  @DeleteMapping("api/students/{id}")
  public void deleteStudent(@PathVariable Integer id) {
      studentService.deleteStudent(id);
  }
}