package org.manlier.srapp.domain;

import java.util.Objects;

public class User {

    private String uuid;
    private int id;

    public User() {
    }

    public User(String uuid) {
        this.uuid = uuid;
    }

    public User(String uuid, int id) {
        this.uuid = uuid;
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id == user.id &&
                Objects.equals(uuid, user.uuid);
    }

    @Override
    public int hashCode() {

        return Objects.hash(uuid, id);
    }

    @Override
    public String toString() {
        return "User{" +
                "uuid='" + uuid + '\'' +
                ", id=" + id +
                '}';
    }
}
