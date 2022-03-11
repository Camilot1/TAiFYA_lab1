package ru.samgtu.camilot;

import ru.samgtu.camilot.enums.EnumCalculateType;
import ru.samgtu.camilot.enums.EnumTokenType;
import ru.samgtu.camilot.exceptions.ModellerVarCountException;
import ru.samgtu.camilot.exceptions.TokenCheckerException;
import ru.samgtu.camilot.exceptions.TokenIndexException;
import ru.samgtu.camilot.gui.Bot;
import ru.samgtu.camilot.gui.MainScene;
import ru.samgtu.camilot.objects.Token;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Modeller {

    private static int tokenIndex = 0;
    private static int step = 0;
    public static boolean waitForValue = false;
    public static boolean hasValue;
    public static boolean inputValue;

    /**
     * Метод моделирования работы ЛСА
     * @param tokens список токенов
     * @param type тип моделирования
     * @throws ModellerVarCountException ошибка при моделировании
     */
    public static void model(List<Token> tokens, EnumCalculateType type) throws Exception {
        MainScene mainScene = Main.getMainScene();
        int varCount = Parser.getVarsCount(tokens);

        mainScene.outputTF.setText("");
        mainScene.updateStatus("");
        if (varCount == 0) throw new ModellerVarCountException("Некорректные данные. Причина: для алгоритма требуется 0 переменных.");
        String input = mainScene.inputTF.getText();


        switch (type) {
            case COMMON:
                if (input.length() != varCount) throw new ModellerVarCountException("Некорректные входные данные. Вы ввели " + input.length() + " значений, а требуется " + varCount + ".");

                boolean[] xs = new boolean[varCount];
                for (int i = 0; i < varCount; i++) xs[i] = Validator.parseBoolean(input.charAt(i));
                mainScene.outputTF.setText(common(tokens, xs));
                break;
            case STEP_BY_STEP:
                Main.getMainScene().setNextStepFunc(e -> {
                    if (waitForValue) {
                        try {
                            inputValue = Validator.parseBoolean(mainScene.inputTF.getText());
                            waitForValue = false;
                            stepByStep(tokens, Main.getMainScene());
                        } catch (TokenCheckerException e1) {
                            mainScene.updateStatus(e1.getMessage());
                        }
                    }
                    stepByStep(tokens, mainScene);
                    mainScene.inputTF.setText("");
                });
                break;
            case ALL:
                break;
        }

        /*

        if (type == EnumCalculateType.COMMON) {
            //Modeller.common(tokens, mainScene);
        } else if (type == EnumCalculateType.STEP_BY_STEP) {
            Main.getMainScene().setNextStepFunc(e -> {
                if (waitForValue) {
                    try {
                        boolean boolVal = Validator.parseBoolean(mainScene.inputTF.getText());
                        waitForValue = false;
                    } catch (TokenCheckerException e1) {
                        mainScene.updateStatus(e1.getMessage());
                    }
                }
                stepByStep(tokens, mainScene);
                mainScene.inputTF.setText("");
            });
            mainScene.updateStatus("Шаг: " + step + "; Текущий токен: " + tokens.get(tokenIndex).toString());
        } else if (type == EnumCalculateType.ALL) {
            Modeller.all(tokens, mainScene);
        }

         */
    }

    public static String modelBot(List<Token> tokens, Bot bot) throws TokenIndexException {
        int waitedIndex; //Индекс логического значения в массиве
        boolean waitForUpArrow = false; //должна ли сработать следующая UP-стрелка
        boolean[] xs = bot.checkDirections(bot.getField().getTiles());

        tokenIndex = 0;
        step = 0;

        while (true) {
            Token token = tokens.get(tokenIndex); //Получаю текущий токен
            switch (token.getType()) {
                case Y:
                    //Если это Yn токен, то записываю его в строку и перехожу к слежующему токену
                    if (token.getIndex().equals("н")) break;
                    if (token.getIndex().equals("к")) return "ЗАКОНЧИЛ";
                    try {
                        bot.executeCommand(token);
                        xs = bot.checkDirections(bot.getField().getTiles());
                    } catch (Exception e) {
                        Main.getMainScene().updateStatus(e.getMessage());
                    }
                    break;
                case X:
                    waitedIndex = Integer.parseInt(token.getIndex()); //Получаю индекс проверяемого значения в массиве xs

                    //Если размер массива меньше или равен ожидаемому индексу, то выкидываю ошибку
                    if (waitedIndex > xs.length)
                        throw new TokenIndexException("Ошибка. Выход индекса токена за пределы набора переменных");

                    if (xs[waitedIndex - 1])
                        waitForUpArrow = true; //Если логическая переменная верная, то включаю ожидание UP-стрелки
                    break;
                case UP:
                    if (waitForUpArrow) {
                        tokenIndex = getDownTokenIndex(tokens, token.getIndex());
                        waitForUpArrow = false;
                    }
                    break;
                case W:
                    waitForUpArrow = true;
                    break;
            }

            tokenIndex++;
            step++;
        }
    }

    private static String common(List<Token> tokens, boolean[] xs) {
        int waitedIndex; //Индекс логического значения в массиве
        boolean waitForUpArrow = false; //должна ли сработать следующая UP-стрелка
        Set<Integer> x = new HashSet<>();

        StringBuilder result = new StringBuilder();

        tokenIndex = 0;
        step = 0;

        while (true) {
            Token token = tokens.get(tokenIndex); //Получаю текущий токен
            switch (token.getType()) {
                case Y:
                    //Если это Yn токен, то записываю его в строку и перехожу к слежующему токену
                    result.append(token.toString());
                    if (token.getIndex().equals("к")) return result.toString();
                    break;
                case X:
                    waitedIndex = Integer.parseInt(token.getIndex()); //Получаю индекс проверяемого значения в массиве xs

                    if (xs[waitedIndex])
                        waitForUpArrow = true; //Если логическая переменная верная, то включаю ожидание UP-стрелки
                    break;
                case UP:
                    if (waitForUpArrow) {
                        tokenIndex = getDownTokenIndex(tokens, token.getIndex());
                        waitForUpArrow = false;
                    }
                    break;
                case W:
                    waitForUpArrow = true;
                    break;
            }

            tokenIndex++;
            step++;
        }
    }

    private static Token stepByStep(List<Token> tokens, MainScene mainScene) {
        boolean waitForUpArrow = false; //должна ли сработать следующая UP-стрелка
        hasValue = false;
        waitForValue = false;


        Token token = tokens.get(tokenIndex); //Получаю текущий токен
        switch (token.getType()) {
            case Y:
                //Если это Yn токен, то записываю его в строку и перехожу к слежующему токену
                return token;
            case X:
                if (!waitForValue && !hasValue) waitForValue = true;

                if (waitForValue) {
                    mainScene.updateStatus("Шаг: " + step + "; Введите логическое значение для " + tokens.get(tokenIndex).toString());
                }
                else {
                    if (inputValue) {
                        waitForUpArrow = true; //Если логическая переменная верная, то включаю ожидание UP-стрелки
                        hasValue = false;
                    }
                }
                break;
            case UP:
                if (waitForUpArrow) {
                    tokenIndex = getDownTokenIndex(tokens, token.getIndex());
                    waitForUpArrow = false;
                }
                break;
            case W:
                waitForUpArrow = true;
                break;
        }

        if (!waitForValue) tokenIndex++;

        step++;
        return null;
    }

    /**
     * Метод получения индекса DOWN-токена в массиве по его строчному индексу
     * @param tokens список токенов
     * @param index строчный индекс токена
     * @return индекс токена в массиве
     */
    private static int getDownTokenIndex(List<Token> tokens, String index) {
        for (int i = 0; i < tokens.size(); i++) {
            if (tokens.get(i).getType() == EnumTokenType.DOWN && tokens.get(i).getIndex().equals(index)) {
                return i;
            }
        }
        return -1;
    }

    private static void all(List<Token> tokens, MainScene mainScene) {

    }
}
