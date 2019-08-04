package net.ameizi.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class Employee implements Serializable {
    private Long id;
    private String name;
    private String address;
    private Integer age;
    private Date createDate;
    private Date updateDate;
    private Department department;
}
