package org.example.jsonp;

import org.example.jsonp.tokens.Token;
import org.example.jsonp.tokens.TokenType;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class LexerTest {

    @Test
    void tokenize_validJson_shouldReturnCorrectTokens() {
        // Подготовка данных
        String json = "{ \"name\": \"John\", \"age\": 30 }";
        Lexer lexer = new Lexer(json);

        // Действие
        List<Token> tokens = lexer.tokenize();

        // Проверка
        assertEquals(9, tokens.size()); // Ожидаем 9 токенов
        assertEquals(TokenType.BRACKET_OBJECT_LEFT, tokens.get(0).getTokenType());
        assertEquals(TokenType.STRING, tokens.get(1).getTokenType());
        assertEquals(TokenType.COLON, tokens.get(2).getTokenType());
        assertEquals(TokenType.STRING, tokens.get(3).getTokenType());
        assertEquals(TokenType.COMMA, tokens.get(4).getTokenType());
        assertEquals(TokenType.STRING, tokens.get(5).getTokenType());
        assertEquals(TokenType.COLON, tokens.get(6).getTokenType());
        assertEquals(TokenType.NUMBER, tokens.get(7).getTokenType());
        assertEquals(TokenType.BRACKET_OBJECT_RIGHT, tokens.get(8).getTokenType());
    }
}

