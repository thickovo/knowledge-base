package com.gao.knowledgebase.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDateTime;

@TableName("user")  //告诉 MyBatis-Plus 这个实体类对应数据库里的user表
public class User {
    private Long id;
    private String username;
    private String password;
    private String nickname;
    private String email;
    private LocalDateTime createTime;  //LocalDateTime 线程安全
    //MyBatis-Plus 默认开启了驼峰转下划线映射 createTime能对应上create_time表

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


}
