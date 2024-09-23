package org.example.jsonp;

import org.example.classes.Person;
import org.example.jsonp.tokens.Token;
import org.example.jsonp.tokens.TokenType;

import java.util.*;

class LL1Parser {

    private List<Token> tokens;
    private int position; // Текущая позиция в списке токенов
    private Stack<String> stack; // Стек для разбора
    private Map<String, Map<TokenType, List<String>>> lookupTable;
    private HashMap<String, Object> parsedResult;
    private JsonMapper jsonMapper;

    Person person=new Person();

    public LL1Parser(List<Token> tokens) {
        this.tokens = tokens;
        this.position = 0;
        this.stack = new Stack<>();
        this.lookupTable = new HashMap<>();
        this.jsonMapper = new JsonMapper();
        this.parsedResult = new HashMap<>();
        initializeLookupTable();
    }

    private void initializeLookupTable() {


        lookupTable.put("<json>", new HashMap<>());
        lookupTable.get("<json>").put(TokenType.BRACKET_OBJECT_LEFT, Arrays.asList("<object>"));
        lookupTable.get("<json>").put(TokenType.BRACKET_ARRAY_LEFT, Arrays.asList("<array>"));
        lookupTable.get("<json>").put(TokenType.NUMBER, Arrays.asList("<primitive>"));
        lookupTable.get("<json>").put(TokenType.STRING, Arrays.asList("<primitive>"));
        lookupTable.get("<json>").put(TokenType.TRUE, Arrays.asList("<primitive>"));
        lookupTable.get("<json>").put(TokenType.FALSE, Arrays.asList("<primitive>"));
        lookupTable.get("<json>").put(TokenType.NULL, Arrays.asList("<primitive>"));

        lookupTable.put("<primitive>", new HashMap<>());
        lookupTable.get("<primitive>").put(TokenType.NUMBER, Arrays.asList("NUMBER"));
        lookupTable.get("<primitive>").put(TokenType.STRING, Arrays.asList("STRING"));
        lookupTable.get("<primitive>").put(TokenType.TRUE, Arrays.asList("TRUE"));
        lookupTable.get("<primitive>").put(TokenType.FALSE, Arrays.asList("FALSE"));
        lookupTable.get("<primitive>").put(TokenType.NULL, Arrays.asList("NULL"));

        lookupTable.put("<container>", new HashMap<>());
        lookupTable.get("<container>").put(TokenType.BRACKET_OBJECT_LEFT, Arrays.asList("<object>"));
        lookupTable.get("<container>").put(TokenType.BRACKET_ARRAY_LEFT, Arrays.asList("<array>"));

        lookupTable.put("<array>", new HashMap<>());
        lookupTable.get("<array>").put(TokenType.BRACKET_ARRAY_LEFT, Arrays.asList("BRACKET_ARRAY_LEFT", "<elements>", "BRACKET_ARRAY_RIGHT"));

        lookupTable.put("<object>", new HashMap<>());
        lookupTable.get("<object>").put(TokenType.BRACKET_OBJECT_LEFT, Arrays.asList("BRACKET_OBJECT_LEFT", "<members>", "BRACKET_OBJECT_RIGHT"));

        lookupTable.put("<members>", new HashMap<>());
        lookupTable.get("<members>").put(TokenType.STRING, Arrays.asList("<member>", "<members_prime>"));
        lookupTable.get("<members>").put(TokenType.BRACKET_OBJECT_RIGHT, Arrays.asList("EPSILON"));

        lookupTable.put("<members_prime>", new HashMap<>());
        lookupTable.get("<members_prime>").put(TokenType.COMMA, Arrays.asList("COMMA", "<member>", "<members_prime>"));
        lookupTable.get("<members_prime>").put(TokenType.BRACKET_OBJECT_RIGHT, Arrays.asList("EPSILON"));

        lookupTable.put("<member>", new HashMap<>());
        lookupTable.get("<member>").put(TokenType.STRING, Arrays.asList("STRING", "COLON", "<json>"));

        lookupTable.put("<elements>", new HashMap<>());
        lookupTable.get("<elements>").put(TokenType.BRACKET_OBJECT_LEFT, Arrays.asList("<json>", "<elements_prime>"));
        lookupTable.get("<elements>").put(TokenType.BRACKET_ARRAY_LEFT, Arrays.asList("<json>", "<elements_prime>"));
        lookupTable.get("<elements>").put(TokenType.NUMBER, Arrays.asList("<json>", "<elements_prime>"));
        lookupTable.get("<elements>").put(TokenType.STRING, Arrays.asList("<json>", "<elements_prime>"));
        lookupTable.get("<elements>").put(TokenType.TRUE, Arrays.asList("<json>", "<elements_prime>"));
        lookupTable.get("<elements>").put(TokenType.FALSE, Arrays.asList("<json>", "<elements_prime>"));
        lookupTable.get("<elements>").put(TokenType.NULL, Arrays.asList("<json>", "<elements_prime>"));
        lookupTable.get("<elements>").put(TokenType.BRACKET_ARRAY_RIGHT, Arrays.asList("EPSILON"));

        lookupTable.put("<elements_prime>", new HashMap<>());
        lookupTable.get("<elements_prime>").put(TokenType.COMMA, Arrays.asList("COMMA", "<json>", "<elements_prime>"));
        lookupTable.get("<elements_prime>").put(TokenType.BRACKET_ARRAY_RIGHT, Arrays.asList("EPSILON"));

        lookupTable.put("<boolean>", new HashMap<>());
        lookupTable.get("<boolean>").put(TokenType.TRUE, Arrays.asList("TRUE"));
        lookupTable.get("<boolean>").put(TokenType.FALSE, Arrays.asList("FALSE"));

    }

    private boolean isNonTerminal(String symbol) {
        return symbol.startsWith("<") && symbol.endsWith(">"); // Проверяет, является ли символ нетерминалом
    }

    private boolean isTerminal(String symbol) {
        try {
            TokenType.valueOf(symbol);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    //Возвращает текущий токен, не изменяя позиции в списке токенов.
    private Token peek() {
        if (position < tokens.size()) {
            return tokens.get(position);
        }
        throw new RuntimeException("Unexpected end of input");
    }

    private Token consume(TokenType expectedType) {
        Token token = peek();  // Получает текущий токен.
        if (token.getTokenType() != expectedType) {  // Проверяет, совпадает ли тип текущего токена с ожидаемым типом.
            throw new RuntimeException("Expected " + expectedType + " but found " + token.getTokenType());  // Выбрасывает исключение, если типы не совпадают.
        }
        position++;  // Увеличивает текущую позицию.
        return token;  // Возвращает текущий токен.
    }

    public <T> T parse(Class<T> clazz) throws Exception {
        HashMap<String, Object> result = parse();
        return jsonMapper.mapToObject(result, clazz); // Используем jsonMapper для конвертации
    }

    public HashMap<String, Object> parse() {

        stack.push("EPSILON"); // Добавляет EOF в стек
        stack.push("<json>"); // Добавляет начальный нетерминал <json> в стек

        Deque<Map<String, Object>> objectStack = new ArrayDeque<>();
        objectStack.push(parsedResult);
        String remember="";
        boolean flag = false;
        List<Object> newArray = new ArrayList<>();
        HashMap<String, Object> tmp = new HashMap<>();
        String tmpremember="";
        boolean tmpflag = false;
        int dec=-1;

        boolean first_left_bracket = true;

        while (!stack.isEmpty()) {
            String top = stack.pop();
            Map<String, Object> currentObject = objectStack.peek();

            if (top.equals("EPSILON")) {
                continue; // Пропускаем EPSILON (пустую строку)
            }

            if (isNonTerminal(top)) {
                Token token = peek();
                List<String> rule = lookupTable.getOrDefault(top, new HashMap<>()).get(token.getTokenType());
                if (rule != null) {
                    for (int i = rule.size() - 1; i >= 0; i--) {
                        stack.push(rule.get(i)); // Добавляет элементы правила в стек в обратном порядке
                    }
                } else {
                    throw new RuntimeException("Unexpected token: " + token.getLexeme()); // Ошибка, если правило не найдено
                }
            } else if (isTerminal(top)) {
                Token token = consume(TokenType.valueOf(top)); // Сравнивает и удаляет терминал из стека



                switch (top) {
                    case "STRING":
                        //String lexeme = token.getLexeme().replaceAll("^\"|\"$", ""); // Удаление лишних кавычек, если они есть
                            if(flag==true) {
                                if(dec<0) {
                                    String lexeme = remember.replaceAll("^\"|\"$", "");
                                    parsedResult.put(lexeme, token.getLexeme().replaceAll("^\"|\"$", ""));
                                    flag = false;
                                }
                                else if(dec==1){
                                    newArray.add(token.getLexeme().replaceAll("^\"|\"$", ""));
                                }
                                else{
                                    if(tmpflag==true) {
                                        String lexeme = tmpremember.replaceAll("^\"|\"$", "");
                                        tmp.put(lexeme, token.getLexeme().replaceAll("^\"|\"$", ""));
                                        tmpflag = false;
                                    }
                                    else{
                                        tmpremember=token.getLexeme();
                                        tmpflag=true;
                                    }
                                }

                            }
                            else{
                                if(dec<0) {
                                    remember = token.getLexeme();
                                }
                                else{
                                    tmpremember=token.getLexeme();
                                }
                            }
                        break;
                    case "NUMBER":
                        if(flag==true) {
                            if(dec<0) {
                                String lexeme = remember.replaceAll("^\"|\"$", "");
                                parsedResult.put(lexeme, Double.parseDouble(token.getLexeme()));
                                flag = false;
                            }
                            else if(dec==1){
                                newArray.add(token.getLexeme());
                            }
                            else{
                                String lexeme = tmpremember.replaceAll("^\"|\"$", "");
                                tmp.put(lexeme, Double.parseDouble(token.getLexeme()));
                                tmpflag=false;
                            }

                        }
                        else{
                            if(dec<0) {
                                remember = token.getLexeme();
                            }
                            else{
                                tmpremember=token.getLexeme();
                            }
                        }
                        break;
                    case "TRUE":
                    case "FALSE":
                        if(flag==true) {
                            if(dec<0) {
                                String lexeme = remember.replaceAll("^\"|\"$", "");
                                parsedResult.put(lexeme, Boolean.parseBoolean(token.getLexeme()));
                                flag = false;
                            }
                            else if(dec==1){
                                newArray.add(token.getLexeme());
                            }
                            else{
                                String lexeme = tmpremember.replaceAll("^\"|\"$", "");
                                tmp.put(lexeme, Boolean.parseBoolean(token.getLexeme()));
                                tmpflag=false;
                            }

                        }
                        else{
                            if(dec<0) {
                                remember = token.getLexeme();
                            }
                            else{
                                tmpremember=token.getLexeme();
                            }
                        }
                        break;
                    case "NULL":
                        if(flag==true) {
                            if(dec<0) {
                                String lexeme = remember.replaceAll("^\"|\"$", "");
                                parsedResult.put(lexeme, null);
                                flag = false;
                            }
                            else if(dec==1){
                                newArray.add(token.getLexeme());
                            }
                            else{
                                String lexeme = tmpremember.replaceAll("^\"|\"$", "");
                                tmp.put(lexeme, null);
                                tmpflag=false;
                            }

                        }
                        else{
                            if(dec<0) {
                                remember = token.getLexeme();
                            }
                            else{
                                tmpremember=token.getLexeme();
                            }
                        }
                        break;
                    case "BRACKET_OBJECT_LEFT":
                        if(!first_left_bracket) {
                            dec = 2;
                        }
                        else{
                            first_left_bracket=false;
                        }
                        break;
                    case "COLON":
                        flag=true;
                        break;
                    case "BRACKET_OBJECT_RIGHT":
                        if(!tmp.isEmpty()) {
                            dec = -1;
                            String lexeme = remember.replaceAll("^\"|\"$", "");
                            parsedResult.put(lexeme, new HashMap<String, Object >(tmp));
                            tmp.clear();
                            flag = false;
                            tmpflag = false;
                            //first_left_bracket=true;
                        }
                        break;
                    case "BRACKET_ARRAY_LEFT":
                        dec=1;
                        break;
                    case "BRACKET_ARRAY_RIGHT":
                        dec=-1;
                        String lexeme = remember.replaceAll("^\"|\"$", "");
                        parsedResult.put(lexeme, new ArrayList<Object>(newArray));
                        newArray.clear();
                        flag=false;
                        break;
                    case "COMMA":
                        break;
                    default:
                        throw new RuntimeException("Unexpected terminal: " + top);}

            } else {
                throw new RuntimeException("Unexpected symbol on stack: " + top); // Ошибка, если в стеке неожиданный символ
            }
        }

        return parsedResult;
    }

    public Object parseArray() throws Exception {
        List<Object> newArray = new ArrayList<>();

        // Парсинг массива начинается с ожидания левой скобки массива
        Token leftBracketToken = consume(TokenType.BRACKET_ARRAY_LEFT);

        // Обработка элементов массива и закрытие массива
        while (peek().getTokenType() != TokenType.BRACKET_ARRAY_RIGHT) {
            // Парсим элементы массива как <json>
            HashMap<String, Object> element = parse();

            // Добавляем разобранный элемент в список newArray
            newArray.add(new HashMap<String, Object>(element));

            // Если после элемента идет запятая, то обрабатываем ее
            if (peek().getTokenType() == TokenType.COMMA) {
                consume(TokenType.COMMA); // Пропускаем запятую
            }
        }

        // После обработки всех элементов, ожидаем правую скобку массива
        Token rightBracketToken = consume(TokenType.BRACKET_ARRAY_RIGHT);

        return newArray;
    }

}
