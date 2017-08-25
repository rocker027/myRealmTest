package com.dayoo.testhandler;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by rocker on 2017/8/25.
 */

public class User extends RealmObject {
    @PrimaryKey
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    private int age;
}
