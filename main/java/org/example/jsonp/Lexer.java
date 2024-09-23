package org.example.jsonp;

import org.example.jsonp.tokens.TokenType;
import org.example.jsonp.tokens.Token;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Lexer {
    private String jsonString;
    private List<Token> tokens;

    public Lexer(String jsonString) {
        this.jsonString = jsonString;
        this.tokens = new ArrayList<>();
    }

    public List<Token> tokenize() {
        String regex = "\\s+|\"(?:\\\\[\"\\\\/]|\\\\u[a-fA-F0-9]{4}|[^\"\\\\])*\"|[+-]?(?:\\d+\\.?\\d*|\\.\\d+)(?:[eE][+-]?\\d+)?|true|false|null|\\{|\\}|\\[|\\]|:|,";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(jsonString);

        while (matcher.find()) {
            String token = matcher.group();
            TokenType type;

            if (token.equals("{")) {
                type = TokenType.BRACKET_OBJECT_LEFT;
            } else if (token.equals("}")) {
                type = TokenType.BRACKET_OBJECT_RIGHT;
            } else if (token.equals("[")) {
                type = TokenType.BRACKET_ARRAY_LEFT;
            } else if (token.equals("]")) {
                type = TokenType.BRACKET_ARRAY_RIGHT;
            } else if (token.equals(":")) {
                type = TokenType.COLON;
            } else if (token.equals(",")) {
                type = TokenType.COMMA;
            } else if (token.matches("\\d+\\.?\\d*|\\.\\d+")) {
                type = TokenType.NUMBER;
            } else if (token.matches("\"(?:\\\\[\"\\\\/]|\\\\u[a-fA-F0-9]{4}|[^\"\\\\])*\"")) {
                type = TokenType.STRING;
            } else if (token.equals("true")) {
                type = TokenType.TRUE;
            } else if (token.equals("false")) {
                type = TokenType.FALSE;
            } else if (token.equals("null")) {
                type = TokenType.NULL;
            } else if (token.matches("\\s+")) {
                //type = TokenType.SPACE;
                continue; // пропускаем пробелы
            } else {
                throw new RuntimeException("Unexpected token: " + token);
            }

            tokens.add(new Token(type, token));
        }

        return tokens;
    }

}
