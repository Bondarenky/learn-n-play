package com.bodanka.learnnplay.domain.entity;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

@Getter
@Entity
@Table(name = "tests")
@NoArgsConstructor
public class Test {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String title;

    @ManyToOne
    @JoinColumn(name = "theme_id", nullable = false)
    private Theme theme;

    @Type(JsonType.class)
    @Column(columnDefinition = "json")
    private String questions;

    public Test(String title, Theme theme, String questions) {
        this.title = title;
        this.theme = theme;
        this.questions = questions;
    }
}
