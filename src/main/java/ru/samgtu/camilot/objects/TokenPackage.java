package ru.samgtu.camilot.objects;

import ru.samgtu.camilot.enums.EnumCalculateType;

public class TokenPackage {

    private Bot bot;
    private final EnumCalculateType type;

    public TokenPackage(EnumCalculateType type) {
        this.type = type;
    }

    public void setBot(Bot bot) {
        this.bot = bot;
    }

    public void removeBot() {
        this.bot = null;
    }

    public boolean addToken(Token token) throws Exception {
        switch (type) {
            case BOT:
                if (bot == null) throw new Exception("Не найден бот при попытке обработать токен.");
                if (!token.getIndex().equals("н") && !token.getIndex().equals("к")) {
                    bot.executeCommand(token);
                    return true;
                }
                return false;
            default: return false;
        }
    }
}
