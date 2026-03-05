package com.archify.generator.domain;

import java.util.ArrayList;
import java.util.List;

public class Architecture {
    private List<Service> services = new ArrayList<>();

    public List<Service> getServices() {
        return services;
    }

    public void setServices(List<Service> services) {
        this.services = services;
    }
}
