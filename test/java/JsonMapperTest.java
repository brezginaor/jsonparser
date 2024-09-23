package org.example.jsonp;

import org.example.classes.Person;
import org.junit.jupiter.api.Test;
import java.util.HashMap;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class JsonMapperTest {

    @Test
    void mapToObject_validMap_shouldMapToObject() throws Exception {
        // Подготовка данных
        Map<String, Object> map = new HashMap<>();
        map.put("name", "Alice");
        map.put("age", 25);

        JsonMapper mapper = new JsonMapper();

        // Действие
        Person person = mapper.mapToObject(map, Person.class);

        // Проверка
        assertNotNull(person);
        assertEquals("Alice", person.getName());
        assertEquals(25, person.getAge());
    }

    @Test
    void mapToObject_invalidFieldType_shouldThrowException() {
        Map<String, Object> map = new HashMap<>();
        map.put("name", "Alice");
        map.put("age", "not_a_number"); // Ошибочный тип

        JsonMapper mapper = new JsonMapper();

        // Ожидаем исключение
        assertThrows(IllegalArgumentException.class, () -> mapper.mapToObject(map, Person.class));
    }
}
