package com.alphathur.menu;

/**
 * Basic node used to build up a menu tree, in fact its not a node, it just looks like a node.
 *
 * @param <T>
 */
public class MenuNode<T> {
    private String code; //basic index for menu node, its strictly organized, and it cannot be null or duplicated
    private String name; //a name for menu node, it can be null or duplicated
    private T data; //a body for menu node, you can set anything you want

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
