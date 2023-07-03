package com.dam.tfg.model;

import java.io.Serializable;

public class UserData implements Serializable {
    private String userId;
    private String token;
    private String matricula;

    public UserData(String userId, String token, String matricula) {
        this.userId = userId;
        this.token = token;
        this.matricula = matricula;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }
}
