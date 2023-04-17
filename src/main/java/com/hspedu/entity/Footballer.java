package com.hspedu.entity;

/**
 * @author Zexi He.
 * @date 2023/4/17 16:01
 * @description:
 */
public class Footballer {

    private String id;
    private String name;
    private String club;

    public Footballer() {
    }

    public Footballer(String id, String name, String club) {
        this.id = id;
        this.name = name;
        this.club = club;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getClub() {
        return club;
    }

    public void setClub(String club) {
        this.club = club;
    }

    @Override
    public String toString() {
        return "Footballer{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", club='" + club + '\'' +
                '}';
    }
}
