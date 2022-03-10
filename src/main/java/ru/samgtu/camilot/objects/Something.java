package ru.samgtu.camilot.objects;

import ru.samgtu.camilot.enums.EnumSomethingType;

import java.util.ArrayList;
import java.util.List;

public class Something {

    private final List<Something> list = new ArrayList<>();
    private EnumSomethingType type;

    public Something(String s) {

    }
}
