package com.archify.generator.domain;

import com.archify.generator.domain.enums.FieldType;

public class Field {
    private String name;
    private FieldType type;

    public Field() {
    }

    public Field(String name, FieldType type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public FieldType getType() {
        return type;
    }

    public void setType(FieldType type) {
        this.type = type;
    }
}
