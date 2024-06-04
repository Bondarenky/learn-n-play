package com.bodanka.learnnplay.domain.entity;

import com.bodanka.learnnplay.domain.Grade;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "classes")
public class Class {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private Grade grade;

    @OneToMany(mappedBy = "clazz")
    private List<Theme> themes = new ArrayList<>();

    public Class(Grade grade) {
        this.grade = grade;
    }
}
