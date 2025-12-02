package com.cocos.game;

public enum NativeCallType {
    CLOSESPLASH(0, "关闭闪图");

    private final int id;
    private final String description;

    NativeCallType(int id, String description) {
        this.id = id;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public static NativeCallType fromId(int id) {
        for (NativeCallType type : NativeCallType.values()) {
            if (type.id == id) {
                return type;
            }
        }
        return null;
    }
}