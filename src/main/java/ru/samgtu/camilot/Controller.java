package ru.samgtu.camilot;

import ru.samgtu.camilot.objects.Token;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class Controller {

    private static final Map<String, Token> tokenMap = new HashMap<>();

    public static Token getToken(String key) {
        return tokenMap.get(key);
    }

    public static void addToken(String key, Token token) {
        tokenMap.put(key, token);
    }

    public static boolean hasToken(String key) {
        return tokenMap.get(key) == null;
    }
}
