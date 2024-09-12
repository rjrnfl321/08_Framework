package edu.kh.test.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import edu.kh.test.dto.Student;

@Controller
@RequestMapping("student")
public class StudentController {

	/**
	 * @param model
	 * @param student
	 * @return
	 */
	@PostMapping("select")
	public String selectStudent(Model model, @ModelAttribute Student student) {
		model.addAttribute("name", student.getStdName());
		model.addAttribute("age", student.getStdAge());
		model.addAttribute("addr", student.getStdAddress());
		return "student/select";
		
	}
	
}