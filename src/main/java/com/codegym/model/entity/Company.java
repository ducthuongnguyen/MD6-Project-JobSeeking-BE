package com.codegym.model.entity;

import com.codegym.constant.Constant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "companies")
public class Company {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String email;
    private String password;
    private String avatar;
    private String address;
    private String phoneNumber;
    private String introduction;
    private Constant.Status status;
    private Integer proposed;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "company_role", joinColumns =
    @JoinColumn(name = "company_id"), inverseJoinColumns =
    @JoinColumn(name = "role_id"))
    Set<Role> roles = new HashSet<>();
}
