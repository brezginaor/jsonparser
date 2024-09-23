package org.example.jsonp;

import org.example.jsonp.tokens.Token;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class LL1ParserTest {

    @Test
    void parse_validTokens_shouldReturnCorrectMap() throws Exception {
        // Подготовка данных
        String json = "{ \"name\": \"John\", \"age\": 30 }";
        Lexer lexer = new Lexer(json);
        List<Token> tokens = lexer.tokenize();
        LL1Parser parser = new LL1Parser(tokens);

        // Действие
        Map<String, Object> result = parser.parse();

        // Проверка
        assertNotNull(result);
        assertEquals("John", result.get("name"));
        assertEquals(30.0, result.get("age")); // Число в JSON парсится как double
    }

    @Test
    void parse_emptyObject_shouldReturnEmptyMap() throws Exception {
        String json = "{}";
        Lexer lexer = new Lexer(json);
        List<Token> tokens = lexer.tokenize();
        LL1Parser parser = new LL1Parser(tokens);

        Map<String, Object> result = parser.parse();

        assertTrue(result.isEmpty());
    }

    @Test
    void parse_invalidTokens_shouldThrowException() {
        String invalidJson = "{ \"name\": \"John\", \"age\": "; // Неполный JSON
        Lexer lexer = new Lexer(invalidJson);
        List<Token> tokens = lexer.tokenize();
        LL1Parser parser = new LL1Parser(tokens);

        assertThrows(RuntimeException.class, parser::parse);
    }
}
