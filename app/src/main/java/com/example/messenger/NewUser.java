package com.example.messenger;

public class NewUser{

    private String id;
    private boolean online;
    private String name;
    private String surname;
    private int age;
    private String token;

    public String getToken() {
        return token;
    }

    public NewUser(String id, String name, String surname, int age, boolean online, String token) {
        this.id = id;
        this.online = online;
        this.name = name;
        this.surname = surname;
        this.age = age;
        this.token = token;
    }

    public NewUser() {
    }

    public String getId() {
        return id;
    }

    public boolean isOnline() {
        return online;
    }

    public int getAge() {
        return age;
    }


    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    @Override
    public String toString() {
        return "NewUser{" +
                "id='" + id + '\'' +
                ", online=" + online +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", age=" + age +
                ", token='" + token + '\'' +
                '}';
    }
}
