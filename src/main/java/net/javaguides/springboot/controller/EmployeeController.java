package net.javaguides.springboot.controller;

import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import net.javaguides.springboot.model.Employee;
import net.javaguides.springboot.service.EmployeeService;

@Controller
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    // display list of employees
    @GetMapping("/")
    public String viewHomePage(Model model) {
        return findPaginated(1, "id", "dsc", model);
    }

    @GetMapping("/showNewEmployeeForm")
    public String showNewEmployeeForm(@RequestParam(required = false) Long id, Model model) {
        Employee employee;
        String formattedJoiningDate = ""; // Default to empty string
        String formattedBirthDate = "";

        if (id != null) {
            employee = employeeService.getEmployeeById(id);
            if (employee != null && employee.getJoiningDate() != null) {
                formattedJoiningDate = employee.getJoiningDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            }
            if (employee != null && employee.getBirthDate() != null) {
                formattedBirthDate = employee.getBirthDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            }
            
        } else {
            employee = new Employee();
        }

        model.addAttribute("employee", employee);
        model.addAttribute("formattedJoiningDate", formattedJoiningDate);
        model.addAttribute("formattedBirthDate", formattedBirthDate);

        return "new_employee";
    }

    @PostMapping("/saveEmployee")
    public String saveEmployee(@ModelAttribute Employee employee) {
        // save employee to database
        employeeService.saveEmployee(employee);
        return "redirect:/";
    }

    @GetMapping("/deleteEmployee/{id}")
    public String deleteEmployee(@PathVariable long id) {
        // call delete employee method
        this.employeeService.deleteEmployeeById(id);
        return "redirect:/";
    }

    @GetMapping("/page/{pageNo}")
    public String findPaginated(@PathVariable int pageNo,
            @RequestParam String sortField,
            @RequestParam String sortDir,
            Model model) {
        int pageSize = 20;

        Page<Employee> page = employeeService.findPaginated(pageNo, pageSize, sortField, sortDir);
        List<Employee> listEmployees = page.getContent();

        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
        model.addAttribute("listEmployees", listEmployees);
        return "index";
    }
}
