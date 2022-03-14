package ru.samgtu.camilot.objects;

import ru.samgtu.camilot.enums.EnumTokenType;

import java.util.Objects;

public class Token {

    private final EnumTokenType type;
    private String index;

    public Token(EnumTokenType type, String index) {
        this.type = type;
        if (type != EnumTokenType.W) this.index = index;
    }

    public Token(String s) throws Exception {
        if (s.length() < 2) throw new Exception();

        this.type = EnumTokenType.getEnumByChar(s.charAt(0));
        this.index = s.substring(1);
    }

    public EnumTokenType getType() {
        return type;
    }

    public String getIndex() {
        return index;
    }

    @Override
    public String toString() {
        return type.getChar() + index;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Token token = (Token) o;
        return type == token.type && Objects.equals(index, token.index);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, index);
    }
}
