package com.alphathur.menu;

public class MenuNode<T> {
    private String code;
    private String name;
    private T data;

    public MenuNode() {
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public T getData() {
        return data;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setData(T data) {
        this.data = data;
    }
}
