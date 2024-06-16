package org.katrin;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RegularTest1 {
    private String classText;
    @BeforeEach
    public void setUp() {
        classText = """
                public class TestClass {
                    private String name;
                    private int age;
                    
                    public TestClass() {
                        this.age = 0;
                    }
                    
                    public void setName(String name) {
                        this.name = name;
                    }
                }
                """;
    }

    @Test
    void testShowPattern() {
        String actual = Regular.showPattern(classText);
        String expected = """
                АТРИБУТИ:
                private String name;
                private int age;
                
                КОНСТРУКТОРИ:
                public TestClass() {
                        this.age = 0;
                    }
                
                МЕТОДИ:
                public void setName(String name) {
                        this.name = name;
                    }
                """;
        // Перевірка, чи правильно відображено атрибути, методи та конструктори
        assertEquals(expected.trim(), actual.trim());

        // Перевірка, чи правильно збережено в списки атрибути та їх імена, методи, конструктори
        assertTrue(Regular.attr.contains("private String name;"));
        assertTrue(Regular.attr.contains("private int age;"));
        assertTrue(Regular.attrName.contains("name"));
        assertTrue(Regular.attrName.contains("age"));
        assertTrue(Regular.meth.contains("public void setName(String name) {\n" +
                "        this.name = name;\n" +
                "    }"));
        assertTrue(Regular.constr.contains("public TestClass() {\n" +
                "        this.age = 0;\n" +
                "    }"));
    }

    @Test
    void testFindMethods() {
        String remainingText = Regular.findMethods(classText);

        // Перевірка, чи правильно знайдено методи
        assertEquals(1, Regular.meth.size());
        assertEquals("""
                public void setName(String name) {
                        this.name = name;
                    }""", Regular.meth.get(0));

        // Перевірка, чи правильно видалено методи з рядка
        assertFalse(remainingText.contains(
                """
                public void setName(String name) {
                         this.name = name;
                     }"""));
    }

    @Test
    void testFindAttributes() {
        String remainingText = Regular.findAttributes(classText);

        // Перевірка, чи правильно знайдено атрибути
        assertEquals(2, Regular.attr.size());
        assertEquals("private String name;", Regular.attr.get(0));
        assertEquals("private int age;", Regular.attr.get(1));

        // Перевірка, чи правильно видалено атрибути з рядка
        assertFalse(remainingText.contains("private String name;"));
        assertFalse(remainingText.contains("private int field;"));
    }

    @Test
    void testFindConstructors() {
        String input = "public class TestClass { private int field; public TestClass() {} }";
        Regular.findConstructors(input);

        // Перевірка, чи правильно знайдено конструктори
        assertEquals(1, Regular.constr.size());
        assertEquals("public TestClass() {}", Regular.constr.get(0));
    }
}
