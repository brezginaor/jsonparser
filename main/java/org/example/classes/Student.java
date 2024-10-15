package org.example.classes;

import java.util.List;

public class Student {
    private String name;
    private int age;
    private boolean isStudent;
    private List<Integer> grades;

    // Getters and Setters

    @Override
    public String toString() {
        return "Student{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", isStudent=" + isStudent +
                ", grades=" + grades +
                '}';
    }

    public void setName(String johnDoe) {
    }

    public void setAge(int i) {
    }

    public void setStudent(boolean b) {
    }

    public void setGrades(List<Integer> list) {
    }
}
