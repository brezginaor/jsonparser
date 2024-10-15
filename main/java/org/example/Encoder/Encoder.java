package org.example.Encoder;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Encoder {


    private static Map<Class<?>, Function<Object, String>> Encoders = new HashMap<>();

    public static void register(Class<?> type, Function<? super Object, String> encoder) {
        Encoder.Encoders.put(type, encoder);
    }



    public static String encode(Object obj) {
        if (obj == null)
            return "null";

        Class<?> type = obj.getClass();

        // Сначала пытаемся найти точное совпадение типа в зарегистрированных энкодерах
        Function<Object, String> encoder = Encoders.get(type);
        if (encoder != null) {
            return encoder.apply(obj);
        }

        // Теперь ищем энкодеры для типов, которые могут быть присваиваемыми (например, Map, List)
        for (Map.Entry<Class<?>, Function<Object, String>> entry : Encoders.entrySet()) {
            if (entry.getKey().isAssignableFrom(type) && entry.getKey() != Object.class) {
                return entry.getValue().apply(obj);
            }
        }

        // Используем энкодер для произвольных объектов (Object) в самом конце
        return Encoders.get(Object.class).apply(obj);
    }

    // Регистрация энкодеров
    static {
        // Регистрация для строк и чисел
        register(String.class, str -> "\"" + str + "\"");
        register(Character.class, ch -> "\"" + ch + "\"");
        register(Number.class, num -> num.toString());

        // Специальные энкодеры для коллекций
        register(List.class, list -> {
            List<?> lst = (List<?>) list;
            return lst.stream()
                    .map(Encoder::encode)
                    .collect(Collectors.joining(", ", "[", "]"));
        });

        register(Map.class, map -> {
            Map<?, ?> mp = (Map<?, ?>) map;
            return mp.entrySet().stream()
                    .map(entry -> {
                        String encodedKey = (entry.getKey() instanceof String) ? "\"" + entry.getKey() + "\"" : encode(entry.getKey());
                        String encodedValue = encode(entry.getValue());
                        return encodedKey + ": " + encodedValue;
                    })
                    .collect(Collectors.joining(", ", "{", "}"));
        });

        // Энкодер для произвольных объектов (Object) должен регистрироваться последним
        register(Object.class, obj -> {
            // Пропускаем системные классы (например, java.util.*)
            if (obj.getClass().getPackageName().startsWith("java.")) {
                return obj.toString();
            }

            StringBuilder result = new StringBuilder("{");
            Field[] fields = obj.getClass().getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                try {
                    result.append("\"").append(field.getName()).append("\": ")
                            .append(encode(field.get(obj)))
                            .append(", ");
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
            if (fields.length > 0) {
                result.setLength(result.length() - 2);  // Удаляем последнюю запятую
            }
            result.append("}");
            return result.toString();
        });
    }
}