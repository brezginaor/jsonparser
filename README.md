# Описание классов библиотеки

## Пакеты:
- **org.example.jsonp.tokens**: Содержит классы для работы с токенами.
- **org.example.jsonp**: Содержит основные классы для обработки JSON.

## Классы:

### 1. **Token** (`org.example.jsonp.tokens.Token`)
Представляет отдельный токен в JSON-строке, включая его тип (например, число, строка, символ) и фактическое значение.

---

### 2. **TokenType** (`org.example.jsonp.tokens.TokenType`)
Перечисление (enum), описывающее типы токенов, такие как числа, строки, булевы значения, символы объектов и массивов, и разделители (двоеточие, запятая).

---

### 3. **Lexer** (`org.example.jsonp.Lexer`)
Класс, который разбивает строку JSON на последовательность токенов, используя регулярные выражения для анализа текста.

---

### 4. **LL1Parser** (`org.example.jsonp.LL1Parser`)
Класс для синтаксического разбора (парсинга) JSON, который на основе токенов строит структуру данных, представляющую JSON, используя метод LL(1).

---

### 5. **JsonParser** (`org.example.jsonp.JsonParser`)
Класс, который позволяет парсить JSON-строки и преобразовывать их в объекты Java. Он соединяет работу лексера и парсера, предоставляя удобный интерфейс для работы с JSON.

---

### 6. **JsonMapper** (`org.example.jsonp.JsonMapper`)
Класс, который сопоставляет разобранные данные JSON (в виде карты) с объектами Java. Использует рефлексию для автоматического преобразования полей JSON в поля объектов Java.

---

## Грамматика

- **<json>** → `<object>` | `<array>` | `<primitive>`
- **<primitive>** → `NUMBER` | `STRING` | `TRUE` | `FALSE` | `NULL`
- **<object>** → `{` `<members>` `}`
- **<members>** → `<member>` `<members_prime>` | `EPSILON`
- **<members_prime>** → `,` `<member>` `<members_prime>` | `EPSILON`
- **<member>** → `STRING` `:` `<json>`
- **<array>** → `[` `<elements>` `]`
- **<elements>** → `<json>` `<elements_prime>` | `EPSILON`
- **<elements_prime>** → `,` `<json>` `<elements_prime>` | `EPSILON`

Эта грамматика описывает синтаксис JSON-структур, которые могут быть объектами, массивами или примитивами (числа, строки, булевы значения и null). Правила грамматики определяют, как составлять JSON-объекты, массивы, члены объектов и элементы массивов.

---

## Lookup Table (Таблица переходов)

`lookupTable` — это таблица, которая используется парсером для принятия решений о том, какие правила грамматики применять в зависимости от текущего токена.

Пример содержимого `lookupTable`:

- **<json>**:
    - `BRACKET_OBJECT_LEFT` → `<object>`
    - `BRACKET_ARRAY_LEFT` → `<array>`
    - `NUMBER` → `<primitive>`
    - `STRING` → `<primitive>`
    - `TRUE` → `<primitive>`
    - `FALSE` → `<primitive>`
    - `NULL` → `<primitive>`

- **<primitive>**:
    - `NUMBER` → `NUMBER`
    - `STRING` → `STRING`
    - `TRUE` → `TRUE`
    - `FALSE` → `FALSE`
    - `NULL` → `NULL`

- **<object>**:
    - `BRACKET_OBJECT_LEFT` → `{` `<members>` `}`

- **<members>**:
    - `STRING` → `<member>` `<members_prime>`
    - `BRACKET_OBJECT_RIGHT` → `EPSILON`

- **<members_prime>**:
    - `COMMA` → `,` `<member>` `<members_prime>`
    - `BRACKET_OBJECT_RIGHT` → `EPSILON`

- **<member>**:
    - `STRING` → `STRING` `:` `<json>`

- **<array>**:
    - `BRACKET_ARRAY_LEFT` → `[` `<elements>` `]`

- **<elements>**:
    - `BRACKET_OBJECT_LEFT` → `<json>` `<elements_prime>`
    - `BRACKET_ARRAY_LEFT` → `<json>` `<elements_prime>`
    - `NUMBER` → `<json>` `<elements_prime>`
    - `STRING` → `<json>` `<elements_prime>`
    - `TRUE` → `<json>` `<elements_prime>`
    - `FALSE` → `<json>` `<elements_prime>`
    - `NULL` → `<json>` `<elements_prime>`
    - `BRACKET_ARRAY_RIGHT` → `EPSILON`

- **<elements_prime>**:
    - `COMMA` → `,` `<json>` `<elements_prime>`
    - `BRACKET_ARRAY_RIGHT` → `EPSILON`

Эта таблица определяет правила переходов для LL(1) парсера на основе текущего символа (токена). Парсер использует эту таблицу для выбора правильных продукций грамматики, основываясь на текущем токене.

---

## Примечание

Класс `LL1Parser` использует `lookupTable` для разбора токенов в соответствии с правилами грамматики JSON, которые определяют, как элементы JSON (массивы, объекты и примитивы) должны быть обработаны. Таблица переходов помогает LL(1) парсеру принять решение, какое правило применить, основываясь на текущем токене.


    