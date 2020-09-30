package com.q256.skyblockImproved.config;

public class Setting<T> {
    private T value;
    private final String displayName;
    private final String description;
    private final String category;

    public Setting(String displayName, T defaultValue, String description){
        this(displayName, defaultValue, description, "General");
    }

    public Setting(String displayName, T defaultValue, String description, String category){
        this.displayName = displayName;
        this.value = defaultValue;
        this.description = description;
        this.category = category;
    }

    public T getValue(){
        return value;
    }

    public void setValue(T value) throws ClassCastException{
        if(value.getClass() == this.value.getClass()) this.value = value;
        else throw new ClassCastException(value.toString() +"("+ value.getClass().getName() + ") can not be cast to " + this.value.getClass());
    }

    public String getDisplayName(){
        return displayName;
    }

    public String getDescription(){
        return description;
    }

    public String getCategory(){
        return category;
    }

    public Class<T> getType(){
        return (Class<T>)value.getClass();
    }
}
