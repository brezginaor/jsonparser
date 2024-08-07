package org.example.Encoder;

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

    static {
        // Register encoders for basic and boxed types
        register(String.class, str -> "\"" + str + "\"");
        register(Character.class, ch -> "\"" + ch + "\"");

        // Covers all Number subtypes (Integer, Double, etc.)
        register(Number.class, num -> num.toString());

        // Special encoders for collections
        register(List.class, list -> {
            List<?> lst = (List<?>) list;
            return lst.stream()
                    .map(item -> encode(item))
                    .collect(Collectors.joining(", ", "[", "]"));
        });
        register(Map.class, map -> {
            Map<?, ?> mp = (Map<?, ?>) map;
            return mp.entrySet().stream()
                    .map(entry -> encode(entry.getKey()) + ": " + encode(entry.getValue()))
                    .collect(Collectors.joining(", ", "{", "}"));
        });
    }


    public static String encode(Object obj) {
        if (obj == null)
            return "null";

        // Attempt to find an exact match for the object's class in the registered encoders
        Class<?> type = obj.getClass();
        Function<Object, String> encoder = Encoders.get(type);
        if (encoder != null)
            return encoder.apply(obj);

        // Search for an assignable class encoder
        for (Map.Entry<Class<?>, Function<Object, String>> entry : Encoders.entrySet()) {
            if (entry.getKey().isAssignableFrom(type)) {
                return entry.getValue().apply(obj);
            }
        }

        // Finally using to string for patch default
        return obj.toString();
    }
}