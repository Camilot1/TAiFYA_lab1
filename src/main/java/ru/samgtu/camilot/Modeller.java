package ru.samgtu.camilot;

import ru.samgtu.camilot.enums.EnumCalculateType;
import ru.samgtu.camilot.enums.EnumTokenType;
import ru.samgtu.camilot.exceptions.ModellerVarCountException;
import ru.samgtu.camilot.exceptions.TokenCheckerException;
import ru.samgtu.camilot.gui.MainScene;
import ru.samgtu.camilot.objects.Token;

import java.util.List;

public class Modeller {

    private static int tokenIndex = 0;
    private static boolean waitForValue;

    public static void model(List<Token> tokens, EnumCalculateType type) throws ModellerVarCountException {
        MainScene mainScene = Main.getMainScene();
        int varCount = Parser.getVarsCount(tokens);

        mainScene.outputTF.setText("");
        mainScene.updateStatus("");
        if (varCount == 0) throw new ModellerVarCountException("Некорректные данные. Причина: для алгоритма требуется 0 переменных.");
        String input = mainScene.inputTF.getText();


        if (type == EnumCalculateType.COMMON) {
            if (input.length() != varCount) throw new ModellerVarCountException("Некорректные входные данные. Вы ввели " + input.length() + " значений, а требуется " + varCount + ".");
            Modeller.common(tokens, mainScene);
        } else if (type == EnumCalculateType.STEP_BY_STEP) {
            Main.getMainScene().setNextStepFunc(e -> {
                try {
                    stepByStep(tokens, mainScene);
                } catch (TokenCheckerException ex) {
                    mainScene.updateStatus(ex.getMessage());
                }
            });
            mainScene.updateStatus("Введите следующее значение.");
            //Modeller.stepByStep(tokens, mainScene);
        } else if (type == EnumCalculateType.ALL) {
            Modeller.all(tokens, mainScene);
        }
    }

    private static void common(List<Token> tokens, MainScene mainScene) {
        String input = mainScene.inputTF.getText();
        int index;

        for (Token token: tokens) {
            if (token.getType() == EnumTokenType.X) {

            } else if (token.getType() == EnumTokenType.Y) {
                index = Integer.parseInt(token.getIndex());
                mainScene.outputTF.appendText(String.valueOf(input.charAt(index-1)));
            }
        }
    }

    private static void stepByStep(List<Token> tokens, MainScene mainScene) throws TokenCheckerException {
        Token tempToken = tokens.get(tokenIndex);
        EnumTokenType type = tempToken.getType();

        if (type == EnumTokenType.X) {
            if (waitForValue) {
                if (Validator.parseBoolean(mainScene.inputTF.getText())) {
                    tokenIndex += 2;
                } else {
                    tokenIndex += 1;
                }
                waitForValue = false;
            }
            else mainScene.updateStatus("Введите следующее значение.");
        } else if (type == EnumTokenType.Y) {
            mainScene.outputTF.appendText(tempToken.toString());
        } else if (type == EnumTokenType.UP) {

        } else if (type == EnumTokenType.DOWN) {

        } else if (type == EnumTokenType.W) {

        }
    }

    private static void all(List<Token> tokens, MainScene mainScene) {

    }
}
