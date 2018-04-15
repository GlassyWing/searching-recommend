package org.manlier.srapp.domain;

import java.util.Objects;

public class Component {

    private int id;
    private String name;
    private String describe;

    public Component(int id, String name, String describe) {
        this.id = id;
        this.name = name;
        this.describe = describe;
    }

    public Component(String name, String describe) {
        this.name = name;
        this.describe = describe;
    }

    public Component() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Component component = (Component) o;
        return id == component.id &&
                Objects.equals(name, component.name) &&
                Objects.equals(describe, component.describe);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, name, describe);
    }

    @Override
    public String toString() {
        return "Component{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", desc='" + describe + '\'' +
                '}';
    }
}
