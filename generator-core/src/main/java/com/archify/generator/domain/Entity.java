package com.archify.generator.domain;

import java.util.ArrayList;
import java.util.List;

public class Entity {
    private String name;
    private List<Field> fields = new ArrayList<>();

    public Entity() {
    }

    public Entity(String name, List<Field> fields) {
        this.name = name;
        this.fields = fields;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Field> getFields() {
        return fields;
    }

    public void setFields(List<Field> fields) {
        this.fields = fields;
    }
}
