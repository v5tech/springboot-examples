package net.ameizi.controller;

import net.ameizi.annotation.JSONFieldFilter;
import net.ameizi.annotation.JSONFieldFilters;
import net.ameizi.vo.Department;
import net.ameizi.vo.Employee;
import org.hibernate.validator.constraints.Range;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

@RestController
public class EmployeeController {

    @JSONFieldFilters({
            @JSONFieldFilter(type = Employee.class, filter = {"createDate", "updateDate"}),
            @JSONFieldFilter(type = Department.class, filter = {"createDate", "updateDate", "employees"})
    })
    @GetMapping("/employee")
    public Employee employee() {
        Employee employee = new Employee();
        employee.setId(1L);
        employee.setName("张三");
        employee.setAddress("陕西西安");
        employee.setAge(18);
        employee.setCreateDate(new Date());
        employee.setUpdateDate(new Date());
        Department department = new Department();
        department.setId(1L);
        department.setName("技术部");
        department.setCreateDate(new Date());
        department.setUpdateDate(new Date());
        employee.setDepartment(department);
        return employee;
    }


    @GetMapping("/employee/save")
    public Employee save(@NotBlank String name, @Range(min = 0, max = 100) @NotNull Integer age) {
        Employee employee = new Employee();
        employee.setId(1L);
        employee.setName(name);
        employee.setAddress("陕西西安");
        employee.setAge(age);
        employee.setCreateDate(new Date());
        employee.setUpdateDate(new Date());
        return employee;
    }

}
