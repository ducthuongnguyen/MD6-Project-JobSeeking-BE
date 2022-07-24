package com.codegym.model.entity;

import com.codegym.constant.Constant;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "recruitment_news")
public class RecruitmentNews {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;

    @ManyToOne
    private Company company;
    @ManyToOne
    private Vacancy vacancy;
    @ManyToOne

    private Field field;
    private Integer salaryFrom;
    private Integer salaryTo;
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate expiredDate;
    private Integer employeeQuantity;
    private Double requiredExperience;
    private Constant.Gender gender;
    private String workingPlace;
    private String description;
    private Constant.Status status;
    private Constant.Proposal proposed;
    private Constant.WorkingType workingType;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "recruitmentNews_user", joinColumns =
    @JoinColumn(name = "recruitmentNews_id"), inverseJoinColumns =
    @JoinColumn(name = "user_id"))
    Set<User> users = new HashSet<>();


}
