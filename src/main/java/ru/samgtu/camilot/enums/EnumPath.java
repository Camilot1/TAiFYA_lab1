package ru.samgtu.camilot.enums;

public enum EnumPath {
    LSA("data\\lsa\\"),
    FIELDS("data\\fields\\");

    private final String path;
    EnumPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
