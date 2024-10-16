package org.example.jsonp;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class JsonParserTest {

    @Test
    void parseJson_validJson_shouldMapToObject() throws Exception {
        // Подготовка данных
        String json = "{ \"name\": \"John\", \"age\": 30 }";
        JsonParser parser = new JsonParser();

        // Действие
        Person person = parser.parseJson(json, Person.class);

        // Проверка
        assertNotNull(person);
        assertEquals("John", person.getName());
        assertEquals(30, person.getAge());
    }

    @Test
    void parseJson_invalidJson_shouldThrowException() {
        String invalidJson = "{ \"name\": \"John\", \"age\": 30 "; // Пропущена закрывающая скобка
        JsonParser parser = new JsonParser();

        // Ожидаем исключение
        assertThrows(RuntimeException.class, () -> parser.parseJson(invalidJson, Person.class));
    }
}

