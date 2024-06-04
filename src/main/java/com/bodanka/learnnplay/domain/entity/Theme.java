package com.bodanka.learnnplay.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "themes")
@NoArgsConstructor
public class Theme {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String title;

    @OneToMany(mappedBy = "theme")
    private List<Test> tests = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "class_id", nullable = false)
    private Class clazz;

    public Theme(String title, User user, Class clazz) {
        this.title = title;
        this.user = user;
        this.clazz = clazz;
    }
}
