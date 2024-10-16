package org.example.jsonp;

import org.example.Encoder.Encoder;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class EncoderTest {

    @Test
    void testEncodeString() {
        String result = Encoder.encode("Hello");
        assertEquals("\"Hello\"", result, "String encoding failed.");
    }

    @Test
    void testEncodeCharacter() {
        String result = Encoder.encode('A');
        assertEquals("\"A\"", result, "Character encoding failed.");
    }

    @Test
    void testEncodeNumber() {
        String result = Encoder.encode(123);
        assertEquals("123", result, "Number encoding failed.");
    }

    @Test
    void testEncodeList() {
        List<String> list = Arrays.asList("apple", "banana", "cherry");
        String result = Encoder.encode(list);
        assertEquals("[\"apple\", \"banana\", \"cherry\"]", result, "List encoding failed.");
    }

    @Test
    void testEncodeMap() {
        Map<String, Integer> map = new HashMap<>();
        map.put("one", 1);
        map.put("two", 2);
        String result = Encoder.encode(map);
        assertEquals("{\"one\": 1, \"two\": 2}", result, "Map encoding failed.");
    }

    @Test
    void testEncodeNull() {
        String result = Encoder.encode(null);
        assertEquals("null", result, "Null encoding failed.");
    }

    @Test
    void testEncodeSubclassOfMap() {
        HashMap<String, Integer> hashMap = new LinkedHashMap<>();
        hashMap.put("apple", 5);
        hashMap.put("orange", 10);
        String result = Encoder.encode(hashMap);
        assertEquals("{\"apple\": 5, \"orange\": 10}", result, "HashMap encoding failed.");
    }

    @Test
    void testEncodeMixedTypes() {
        Map<Object, Object> mixedMap = new LinkedHashMap<>(); // Изменено на LinkedHashMap
        mixedMap.put("age", 25);
        mixedMap.put('A', "Apple");
        mixedMap.put("list", Arrays.asList(1, 2, 3));

        String result = Encoder.encode(mixedMap);
        assertEquals("{\"age\": 25, \"A\": \"Apple\", \"list\": [1, 2, 3]}", result, "Mixed map encoding failed.");
    }
}