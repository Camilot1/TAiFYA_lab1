package ru.samgtu.camilot.enums;

import java.util.ArrayList;
import java.util.List;

public enum EnumCalculateType {
    COMMON("Обычный"),
    STEP_BY_STEP("Пошаговый"),
    ALL("Перебор");

    private String type;
    EnumCalculateType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public static String getFirstType() {
        return values()[0].getType();
    }

    public static EnumCalculateType getEnumByType(String type) {
        for (EnumCalculateType tokenType: EnumCalculateType.values()) if (tokenType.type.equals(type)) return tokenType;
        return null;
    }

    public static List<String> getValuesAsList() {
        List<String> list = new ArrayList<>();
        for (EnumCalculateType tokenType: EnumCalculateType.values()) list.add(tokenType.getType());
        return list;
    }
}
