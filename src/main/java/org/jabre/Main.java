package org.jabre;

public class Main {
    static String code = "++++++++[>++++[>++>+++>+++>+<<<<-]>+>->+>>+[<]<-]>>.>\n" +
            ">---.+++++++..+++.>.<<-.>.+++.------.--------.>+.>++.";
    public static void main(String[] args) {
        new BrainfuckInterpreter(code).execute();
    }
}