package net.ameizi.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
public class Department implements Serializable {
    private Long id;
    private String name;
    private List<Employee> employees;
    private Date createDate;
    private Date updateDate;
}
