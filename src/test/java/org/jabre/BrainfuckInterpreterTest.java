package org.jabre;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests taken from <a href="https://brainfuck.org/">brainfuck.org</a>.
 */
public class BrainfuckInterpreterTest {
    @Test
    public void inputToOutputCopier() {
        String code = ",[.[-],]";
        String input = "123\0";
        String expectedOutput = "123";

        testInterpreter(code, input, expectedOutput);
    }

    @Test
    public void sortBytes() {
        String code = ">>,[>>,]<<[[-<+<]>[>[>>]<[.[-]<[[>>+<<-]<]>>]>]<<]";
        String input = "3214\0";
        String expectedOutput = "1234";

        testInterpreter(code, input, expectedOutput);
    }

    @Test
    public void helloWorld() {
        String code = "++++++++[>++++[>++>+++>+++>+<<<<-]>+>->+>>+[<]<-]>>.>\n" +
                ">---.+++++++..+++.>.<<-.>.+++.------.--------.>+.>++.";
        String input = "";
        String expectedOutput = "Hello World!\n";

        testInterpreter(code, input, expectedOutput);
    }

    private static void testInterpreter(String code, String input, String expectedOutput) {
        TestByteInputReader testByteInputReader = new TestByteInputReader(input);
        TestByteOutputWriter testByteOutputWriter = new TestByteOutputWriter();

        BrainfuckInterpreter interpreter = new BrainfuckInterpreter(code, testByteInputReader, testByteOutputWriter);
        interpreter.execute();

        assertEquals(expectedOutput, testByteOutputWriter.stringBuilder.toString());
    }

    static class TestByteInputReader implements BrainfuckInterpreter.ByteInputReader {
        final String inputText;
        int index = 0;

        TestByteInputReader(String inputText) {
            this.inputText = inputText;
        }
        @Override
        public byte read() {
            if(index < inputText.length()) {
                return (byte) inputText.charAt(index++);
            }
            return 0;
        }
    }
    static class TestByteOutputWriter implements BrainfuckInterpreter.ByteOutputWriter {
        final StringBuilder stringBuilder = new StringBuilder();

        @Override
        public void write(byte b) {
            stringBuilder.append((char) b);
        }
    }
}
