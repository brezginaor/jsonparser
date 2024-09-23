package org.example.jsonp;

import org.example.jsonp.tokens.Token;

import java.util.List;
import java.util.Map;


public class JsonParser {
    private JsonMapper jsonMapper = new JsonMapper();

    public <T> T parseJson(String json, Class<T> clazz) throws Exception {
        Lexer lexer = new Lexer(json);
        List<Token> tokens = lexer.tokenize();
        LL1Parser parser = new LL1Parser(tokens);
        Map<String, Object> parsedMap = parser.parse();
        return jsonMapper.mapToObject(parsedMap, clazz);
    }
}
