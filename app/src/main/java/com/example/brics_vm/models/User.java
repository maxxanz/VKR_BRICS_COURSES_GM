package com.example.brics_vm.models;

public class User {
    private Integer id;  // ← изменили с int на Integer
    private String user_type;
    private String first_name;
    private String last_name;
    private String country;
    private String university;
    private String email;
    private String password;
    private String created_at;

    // Пустой конструктор
    public User() {}

    // Конструктор для регистрации
    public User(String email, String password, String first_name, String last_name,
                String country, String user_type, String university) {
        this.email = email;
        this.password = password;
        this.first_name = first_name;
        this.last_name = last_name;
        this.country = country;
        this.user_type = user_type;
        this.university = university;
        // id остается null, не будет включен в JSON
    }

    // Геттеры и сеттеры
    public Integer getId() { return id; }  // ← изменили тип
    public void setId(Integer id) { this.id = id; }  // ← изменили тип

    public String getUser_type() { return user_type; }
    public void setUser_type(String user_type) { this.user_type = user_type; }

    public String getFirst_name() { return first_name; }
    public void setFirst_name(String first_name) { this.first_name = first_name; }

    public String getLast_name() { return last_name; }
    public void setLast_name(String last_name) { this.last_name = last_name; }

    public String getName() {
        return first_name + " " + last_name;
    }

    public void setName(String name) {
        String[] parts = name.split(" ", 2);
        this.first_name = parts[0];
        this.last_name = parts.length > 1 ? parts[1] : "";
    }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public String getUniversity() { return university; }
    public void setUniversity(String university) { this.university = university; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getCreated_at() { return created_at; }
    public void setCreated_at(String created_at) { this.created_at = created_at; }
}