package ru.ap1kkk;

import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;

public class Main {
    private static final int ROUND_CYCLES = 16;
    private static final int BLOCK_SIZE = 8;
    private static final int SUBKEY_SIZE = BLOCK_SIZE / 2;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        try (scanner) {
            System.out.print("Введите сообщение: ");
            String message = scanner.nextLine();
            System.out.print("Введите ключ: ");
            String key = scanner.nextLine();
            String encryptedMessage = encryptMessage(message, key);
            String decryptedMessage = decryptMessage(encryptedMessage, key);

            System.out.printf("Оригинальное сообщение: %s\n", message);
            System.out.printf("Зашифрованное сообщение сообщение: %s\n", encryptedMessage);
            System.out.printf("Расшифрованное сообщение: %s\n", decryptedMessage);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

    // Метод для разбиения строки на блоки заданного размера с добавлением пробелов, если необходимо
    public static String[] splitStringIntoBlocks(String input, int blockSize) {
        int length = input.length();
        int numBlocks = (int) Math.ceil((double) length / blockSize);
        String[] blocks = new String[numBlocks];

        int index = 0;
        for (int i = 0; i < numBlocks; i++) {
            int endIndex = Math.min(index + blockSize, length);
            String block = input.substring(index, endIndex);
            // Если длина блока меньше заданного размера, добавляем пробелы в конце блока
            while (block.length() < blockSize) {
                block += " ";
            }
            blocks[i] = block;
            index += blockSize;
        }
        return blocks;
    }

    public static String encryptMessage(String message, String key)
    {
        String[] inputBlocks = splitStringIntoBlocks(message, BLOCK_SIZE);

        String result = "";

        for (String block: inputBlocks) {
            result += encryptBlock(block, key);
        }

        return result;
    }

    public static String encryptBlock(String block, String key) {
        String[] subKeys = splitStringIntoBlocks(key, SUBKEY_SIZE);

        for (int i = 0; i < ROUND_CYCLES; i++) {
            block = feistelRound(block, subKeys[i % subKeys.length]);
        }

        return block;
    }

    public static String decryptMessage(String message, String key) {
        String[] inputBlocks = splitStringIntoBlocks(message, BLOCK_SIZE);

        String result = "";

        for (String block: inputBlocks) {
            result += decryptBlock(block, key);
        }

        return result;
    }

    public static String decryptBlock(String block, String key) {
        String[] subKeys = splitStringIntoBlocks(key, SUBKEY_SIZE);

        for (int i = ROUND_CYCLES - 1; i >= 0; i--) {
            block = feistelRoundBack(block, subKeys[i % subKeys.length]);
        }

        return block;
    }

    // Функция для выполнения операции XOR для двух строк
    public static String xorStrings(String s1, String s2) {
        if (s1.length() != s2.length()) {
            throw new IllegalArgumentException("Длины строк должны быть одинаковыми");
        }

        StringBuilder result = new StringBuilder();
        // Выполняем операцию XOR для символов строк
        for (int i = 0; i < s1.length(); i++) {
            char char1 = s1.charAt(i);
            char char2 = s2.charAt(i);
            int xorResult = char1 ^ char2;
            result.append((char) xorResult);
        }
        return result.toString();
    }

    public static String feistelRound(String block, String subKey) {
        if(block.length() != BLOCK_SIZE)
            throw new IllegalArgumentException("Длина строки должна быть ровно 8");

        if(subKey.length() != SUBKEY_SIZE)
            throw new IllegalArgumentException("Длина подключа должна быть ровно 4");

        String leftSubBlock = block.substring(0, 4);
        String rightSubBlock = block.substring(4, 8);

        return rightSubBlock +
                xorStrings(
                        leftSubBlock,
                        xorStrings(rightSubBlock, subKey)
                );
    }

    public static String feistelRoundBack(String block, String subKey) {
        if(block.length() != BLOCK_SIZE)
            throw new IllegalArgumentException("Длина строки должна быть ровно 8");

        if(subKey.length() != SUBKEY_SIZE)
            throw new IllegalArgumentException("Длина подключа должна быть ровно 4");

        String leftSubBlock = block.substring(0, 4);
        String rightSubBlock = block.substring(4, 8);

        return xorStrings(
                    xorStrings(leftSubBlock, subKey),
                    rightSubBlock
                )
                + leftSubBlock;
    }
}