package com.archify.generator.domain;

import java.util.ArrayList;
import java.util.List;

import com.archify.generator.domain.enums.PersistenceStyle;

public class Service {
    private String name;
    private Database database;
    private PersistenceStyle persistenceStyle = PersistenceStyle.JPA;
    private List<Entity> entities = new ArrayList<>();
    private List<ServiceCall> calls = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Database getDatabase() {
        return database;
    }

    public void setDatabase(Database database) {
        this.database = database;
    }

    public PersistenceStyle getPersistenceStyle() {
        return persistenceStyle;
    }

    public void setPersistenceStyle(PersistenceStyle persistenceStyle) {
        this.persistenceStyle = persistenceStyle;
    }

    public List<Entity> getEntities() {
        return entities;
    }

    public void setEntities(List<Entity> entities) {
        this.entities = entities;
    }

    public List<ServiceCall> getCalls() {
        return calls;
    }

    public void setCalls(List<ServiceCall> calls) {
        this.calls = calls;
    }
}
