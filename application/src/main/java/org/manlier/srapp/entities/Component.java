package org.manlier.srapp.entities;

import java.util.Objects;

public class Component {

    private String id;
    private String desc;

    public Component(String id, String desc) {
        this.id = id;
        this.desc = desc;
    }

    public Component() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Component component = (Component) o;
        return Objects.equals(id, component.id) &&
                Objects.equals(desc, component.desc);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, desc);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Component{" +
                "id='" + id + '\'' +
                ", desc='" + desc + '\'' +
                '}';
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
