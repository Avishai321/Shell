package shell.lexer;

import java.util.ArrayList;
import java.util.List;

public class Lexer {
    public enum TokenType {
        STRING,
        REDIRECT_OUT,
        APPEND_REDIRECT_OUT,
        REDIRECT_IN,
        PIP,
        SEND_TO_BACKGROUND,
        OR,
        AND
    }

    public record Token(TokenType type, String val) {
    }

    private enum STATE {
        NORMAL,
        SINGLE_QUOTE,
        DOUBLE_QUOTE,
        ESCAPE
    }

    public static List<Token> tokenize(String input) {
        List<Token> tokens = new ArrayList<>();

        STATE state = STATE.NORMAL;
        STATE prevState = STATE.NORMAL;
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);

            switch (state) {
                case NORMAL -> {
                    switch (c) {
                        case ' ' -> {
                            if (!sb.isEmpty()) tokens.add(createStringToken(sb));
                        }
                        case '<' -> {
                            if (!sb.isEmpty()) tokens.add(createStringToken(sb));
                            tokens.add(new Token(TokenType.REDIRECT_IN, "<"));
                        }
                        case '|' -> i = processOperator(
                                input, i,
                                '|', TokenType.PIP, TokenType.OR,
                                sb, tokens
                        );
                        case '&' -> i = processOperator(
                                input, i,
                                '&', TokenType.SEND_TO_BACKGROUND, TokenType.AND,
                                sb, tokens
                        );
                        case '>' -> i = processOperator(
                                input, i,
                                '>', TokenType.REDIRECT_OUT, TokenType.APPEND_REDIRECT_OUT,
                                sb, tokens
                        );
                        case '"' -> state = STATE.DOUBLE_QUOTE;
                        case '\'' -> state = STATE.SINGLE_QUOTE;
                        case '\\' -> {
                            prevState = state;
                            state = STATE.ESCAPE;
                        }
                        default -> sb.append(c);
                    }
                }
                case DOUBLE_QUOTE -> {
                    if (c == '\\') {
                        prevState = state;
                        state = STATE.ESCAPE;
                    } else if (c == '"') state = STATE.NORMAL;
                    else sb.append(c);
                }
                case SINGLE_QUOTE -> {
                    if (c == '\'') state = STATE.NORMAL;
                    else sb.append(c);
                }
                case ESCAPE -> {
                    sb.append(c);
                    state = prevState;
                }
            }
        }

        if (!sb.toString().isBlank()) {
            tokens.add(createStringToken(sb));
        }

        return tokens;
    }

    private static Token createStringToken(StringBuilder sb) {
        String val = sb.toString();
        sb.setLength(0);
        return new Token(TokenType.STRING, val);
    }

    private static int processOperator(
            String input,
            int index,
            char opChar,
            TokenType singleType,
            TokenType doubleType,
            StringBuilder sb,
            List<Token> tokens
    ) {
        if (!sb.isEmpty()) tokens.add(createStringToken(sb));

        if (index + 1 < input.length() && input.charAt(index + 1) == opChar) {
            tokens.add(new Token(doubleType, String.valueOf(opChar) + opChar));
            return index + 1;
        }

        tokens.add(new Token(singleType, String.valueOf(opChar)));
        return index;
    }
}
