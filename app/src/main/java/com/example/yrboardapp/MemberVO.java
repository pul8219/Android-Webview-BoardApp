package com.example.yrboardapp;

// 수정 필요
public class MemberVO {
    private String id;
    private String pw;
    private String gender;
    private String name;
    private String email;
    private String birth;
    private String cpnum;
    private String regdate;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPw() {
        return pw;
    }

    public void setPw(String pw) {
        this.pw = pw;
    }

    public String getGender() { return gender; }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBirth() {
        return birth;
    }

    public void setBirth(String birth) {
        this.birth = birth;
    }

    public String getCpnum() {
        return cpnum;
    }

    public void setCpnum(String telnum) {
        this.cpnum = cpnum;
    }

    public String getRegdate() {
        return regdate;
    }

    public void setRegdate(String regdate) {
        this.regdate = regdate;
    }



}
