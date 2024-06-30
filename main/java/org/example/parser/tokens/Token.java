package org.example.parser.tokens;

public class Token {
    private TokenType tokenType;
    private String lexeme;

    public Token(TokenType tokenType, String lexeme) {
        this.tokenType = tokenType;
        this.lexeme = lexeme;
    }

    public TokenType getTokenType() {
        return tokenType;
    }

    public String getLexeme() {
        return lexeme;
    }

    public String toString() {
        return "Token(" + tokenType + ", " + lexeme + ")";
    }
}
