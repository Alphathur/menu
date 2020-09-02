package com.alphathur.menu;

import java.util.List;

/**
 * Basic menu struct, used to view for front-end application
 *
 * @param <T>
 */
public class Menu<T> {
    private String code;
    private String name;
    private T data;
    private List<Menu> children;

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public T getData() {
        return data;
    }

    public List<Menu> getChildren() {
        return children;
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

    public void setChildren(List<Menu> children) {
        this.children = children;
    }

    @Override
    public String toString() {
        return "Menu{" +
                "code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", data=" + data +
                ", children=" + children +
                '}';
    }
}
