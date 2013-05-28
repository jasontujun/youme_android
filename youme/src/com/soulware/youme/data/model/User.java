package com.soulware.youme.data.model;

/**
 * Created with IntelliJ IDEA.
 * User: jasontujun
 * Date: 13-5-24
 * Time: 下午2:44
 */
public class User {
    private String id;
    private String username;
    private String password;
    private long birthday;
    private String school;
    private String[] descriptions;

    public User() {
    }

    public User(String id, String username, String password, String[] descriptions, String school, long birthday) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.descriptions = descriptions;
        this.school = school;
        this.birthday = birthday;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public long getBirthday() {
        return birthday;
    }

    public void setBirthday(long birthday) {
        this.birthday = birthday;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public String[] getDescriptions() {
        return descriptions;
    }

    public void setDescriptions(String[] descriptions) {
        this.descriptions = descriptions;
    }
}
