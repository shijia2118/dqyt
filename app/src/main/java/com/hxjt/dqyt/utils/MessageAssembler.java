package com.hxjt.dqyt.utils;

import java.util.TreeMap;
import java.util.Map;

public class MessageAssembler {

    private static final String MESSAGE_PREFIX = "[djjshz_";
    private static final String MESSAGE_SUFFIX = "]";
    private static final String END_MESSAGE_PREFIX = "[djjshz_10]";

    private Map<Integer, String> messageParts = new TreeMap<>();
    public boolean isComplete = false;

    // This method processes each incoming message
    public void processMessage(String message) {
        if (isComplete) {
            return; // 如果消息已经完成，忽略后续的消息
        }

        if (message.startsWith(MESSAGE_PREFIX)) {
            int suffixIndex = message.indexOf(MESSAGE_SUFFIX);
            if (suffixIndex > 0) {
                String indexStr = message.substring(MESSAGE_PREFIX.length(), suffixIndex);
                String messageContent = message.substring(suffixIndex + MESSAGE_SUFFIX.length()).trim();
                try {
                    int index = Integer.parseInt(indexStr);

                    // 添加消息内容到map中，无论是不是第10部分
                    messageParts.put(index, messageContent);

                    if (index == 10) {
                        isComplete = true; // 标记为完成
                    }
                } catch (NumberFormatException e) {
                    // 处理 indexStr 不是有效数字的情况
                    e.printStackTrace();
                }
            }
        }
    }


    // This method assembles the message parts into the complete message
    public String getCompleteMessage() {
        if (!isComplete) {
            return null; // If not yet complete, return null or some other indicator
        }

        StringBuilder completeMessage = new StringBuilder();
        for (String part : messageParts.values()) {
            completeMessage.append(part);
        }
        return completeMessage.toString();
    }

    public void reset() {
        messageParts.clear();
        isComplete = false;
    }

    public static void main(String[] args) {
        MessageAssembler assembler = new MessageAssembler();

        // Simulating receiving messages
        assembler.processMessage("[djjshz_1]Hello ");
        assembler.processMessage("[djjshz_2]world!");
        assembler.processMessage("[djjshz_3] How ");
        assembler.processMessage("[djjshz_4]are you?");
        assembler.processMessage("[djjshz_10]"); // Indicating the end of the message

        String completeMessage = assembler.getCompleteMessage();
        System.out.println("Complete message: " + completeMessage);
    }
}

