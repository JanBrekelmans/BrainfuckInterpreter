package org.jabre;

import java.io.*;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

public class BrainfuckInterpreter {
    private final static int MEMORY_SIZE = 1 << 16;
    private final byte[] memory = new byte[MEMORY_SIZE];
    private final Map<Integer, Integer> jumpToPosition = new HashMap<>();
    private int memoryPointer = 0;

    private final String code;
    private final ByteInputReader byteInputReader;
    private final ByteOutputWriter byteOutputWriter;

    public BrainfuckInterpreter(String code, ByteInputReader byteInputReader, ByteOutputWriter byteOutputWriter) {
        this.code = code;
        this.byteInputReader = byteInputReader;
        this.byteOutputWriter = byteOutputWriter;

        computeJumpToPositions();
    }

    public BrainfuckInterpreter(String code) {
        this(code, new ByteInputReader.ConsoleByteInputReader(), new ByteOutputWriter.ConsoleByteOutputWriter());
    }

    private void computeJumpToPositions() {
        Deque<Integer> stack = new ArrayDeque<>();
        for(int i = 0; i < code.length(); i++) {
            char c = code.charAt(i);
            switch (c) {
                case '[' -> stack.add(i);
                case ']' -> {
                    if(stack.isEmpty()) {
                        throw new RuntimeException("Unmatched \"]\" at position: " + i);
                    }
                    int jumpBack = stack.pollLast();
                    jumpToPosition.put(i, jumpBack);
                    jumpToPosition.put(jumpBack, i);
                }
                default ->{}
            }
        }

        if(!stack.isEmpty()) {
            throw new RuntimeException("Unmatched \"[\" at position: " + stack.pollLast());
        }
    }

    public void execute() {
        for (int i = 0; i < code.length(); ) {
            char c = code.charAt(i);
            switch (c) {
                case '>' -> {
                    if(memoryPointer == memory.length - 1) {
                        throw new IndexOutOfBoundsException("Memory pointer exceeded memory size.");
                    }
                    memoryPointer++;
                    i++;
                }
                case '<' -> {
                    if (memoryPointer == 0) {
                        throw new IndexOutOfBoundsException("Memory pointer went negative.");
                    }
                    memoryPointer--;
                    i++;
                }
                case '+' -> {
                    memory[memoryPointer]++;
                    i++;
                }
                case '-' -> {
                    memory[memoryPointer]--;
                    i++;
                }
                case '.' -> {
                    byteOutputWriter.write(memory[memoryPointer]);
                    i++;
                }
                case ',' -> {
                    memory[memoryPointer] = byteInputReader.read();
                    i++;
                }
                case '[' -> {
                    if (memory[memoryPointer] == 0) {
                        i = jumpToPosition.get(i);
                    } else {
                        i++;
                    }
                }
                case ']' -> {
                    if(memory[memoryPointer] == 0) {
                        i++;
                    } else {
                        i = jumpToPosition.get(i);
                    }
                }
                default -> i++;
            }
        }
    }

    public interface ByteInputReader {
        byte read();

        class ConsoleByteInputReader implements ByteInputReader {
            final BufferedInputStream inputStream = (BufferedInputStream) System.in;
            @Override
            public byte read() {
                try {
                    byte read = (byte) inputStream.read();
                    if (read == '\n') return 0;
                    return read;
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public interface ByteOutputWriter {
        void write(byte b);

        class ConsoleByteOutputWriter implements ByteOutputWriter {
            final PrintStream output = System.out;
            @Override
            public void write(byte b) {
                output.print((char) b);
            }
        }
    }
}
