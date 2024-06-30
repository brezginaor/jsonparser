import org.example.Encoder.Encoder;
import org.example.parser.LL1Parser;
import org.example.classes.Person;
import org.example.parser.Lexer;
import org.example.parser.tokens.Token;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.List;

public class LL1ParserTest {

    // Пример JSON строки: объект с примитивными типами данных
    private String jsonPrimitive = "{\"name\": \"John\", \"age\": 30, \"city\": \"New York\"}";

    // Пример JSON строки: объект с вложенным объектом
    private String jsonNestedObject = "{\"name\": \"John\", \"age\": 30, \"address\": {\"city\": \"New York\", \"postalCode\": \"10001\"}}";

    // Пример JSON строки: массив объектов
    private String jsonArray = "[{\"name\": \"John\", \"age\": 30}, {\"name\": \"Alice\", \"age\": 25}]";

    // Пример JSON строки: объект с массивом
    private String jsonObjectWithArray = "{\"name\": \"John\", \"hobbies\": [\"reading\", \"swimming\"]}";

    // Пример JSON строки: объект, представляющий Person
    private String jsonPerson = "{\"name\": \"John\", \"age\": 30}";

    @Test
    public void testParseJsonPrimitive() {
        Lexer lexer = new Lexer(jsonPrimitive);
        List<Token> tokens = lexer.tokenize();
        LL1Parser parser = new LL1Parser(tokens);

        HashMap<String, Object> result = parser.parse();

        HashMap<String, Object> expected = new HashMap<>();
        expected.put("name", "John");
        expected.put("age", 30.0);
        expected.put("city", "New York");

        assertEquals(expected, result);
    }

    @Test
    public void testParseJsonNestedObject() {
        Lexer lexer = new Lexer(jsonNestedObject);
        List<Token> tokens = lexer.tokenize();
        LL1Parser parser = new LL1Parser(tokens);

        HashMap<String, Object> result = parser.parse();

        HashMap<String, Object> expected = new HashMap<>();
        expected.put("name", "John");
        expected.put("age", 30.0);

        HashMap<String, Object> address = new HashMap<>();
        address.put("city", "New York");
        address.put("postalCode", "10001");
        expected.put("address", address);

        assertEquals(expected, result);
    }

    @Test
    public void testParseJsonObjectWithArray() {
        Lexer lexer = new Lexer(jsonObjectWithArray);
        List<Token> tokens = lexer.tokenize();
        LL1Parser parser = new LL1Parser(tokens);

        HashMap<String, Object> result = parser.parse();

        HashMap<String, Object> expected = new HashMap<>();
        expected.put("name", "John");

        List<String> hobbies = List.of("reading", "swimming");
        expected.put("hobbies", hobbies);

        assertEquals(expected, result);
    }

    @Test
    public void testParseArray() throws Exception {
        Lexer lexer = new Lexer(jsonArray);
        List<Token> tokens = lexer.tokenize();
        LL1Parser parser = new LL1Parser(tokens);

        List<Object> result = (List<Object>) parser.parseArray();

        HashMap<String, Object> expectedElement1 = new HashMap<>();
        expectedElement1.put("name", "John");
        expectedElement1.put("age", 30.0);

        HashMap<String, Object> expectedElement2 = new HashMap<>();
        expectedElement2.put("name", "Alice");
        expectedElement2.put("age", 25.0);

        List<Object> expected = List.of(expectedElement1, expectedElement2);

        assertEquals(expected, result);
    }

    @Test
    public void testParseJsonToPerson() throws Exception {
        Lexer lexer = new Lexer(jsonPerson);
        List<Token> tokens = lexer.tokenize();
        LL1Parser parser = new LL1Parser(tokens);

        Person result = parser.parse(Person.class);

        Person expected = new Person();
        expected.setName("John");
        expected.setAge(30);

        assertEquals(expected.getName(), result.getName());
        assertEquals(expected.getAge(), result.getAge());
    }

    @Test
    public void testEncodePerson() {
        // Регистрация энкодера для Person
        Encoder.register(Person.class, person -> {
            Person p = (Person) person;
            return "{\"name\": \"" + p.getName() + "\", \"age\": " + p.getAge() + "}";
        });

        // Создание объекта Person
        Person person = new Person();
        person.setName("John");
        person.setAge(30);

        // Кодирование объекта Person
        String encodedPerson = Encoder.encode(person);

        // Ожидаемый результат
        String expected = "{\"name\": \"John\", \"age\": 30}";

        assertEquals(expected, encodedPerson);
    }
}
