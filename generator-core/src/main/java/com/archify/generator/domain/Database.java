package com.archify.generator.domain;

import com.archify.generator.domain.enums.DatabaseType;

public class Database {
    private DatabaseType type;

    public Database() {
    }

    public Database(DatabaseType type) {
        this.type = type;
    }

    public DatabaseType getType() {
        return type;
    }

    public void setType(DatabaseType type) {
        this.type = type;
    }
}
