package ru.samgtu.camilot.objects;

public class BooleanPackage {

    private volatile Bot bot;
    private volatile boolean[] booleans;

    public BooleanPackage() {
    }

    public BooleanPackage(boolean[] booleans) {
        this.booleans = booleans;
    }

    public void setBot(Bot bot) {
        this.bot = bot;
    }

    public void checkBot() {
        if (bot != null) setBooleans(bot.checkDirections());
    }

    public void removeBot() {
        this.bot = null;
    }

    public boolean[] getBooleans() {
        return booleans;
    }

    public boolean getBoolean(int index) {
        return booleans[index];
    }

    public synchronized void setBooleans(boolean[] booleans) {
        this.booleans = booleans;
    }
}
