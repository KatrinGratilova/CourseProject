package org.katrin;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import javax.swing.*;
import java.util.ArrayList;

class AnalysisTest {
    @Test
    void testAnalyzePattern() {
        // Підготовка тестових даних
        String classText = """
                public class TestClass {
                    private int field;
                    private int redundantField;
                 
                    public TestClass(int field) {}
                    public TestClass(int redundantField) {}

                    public void usefulMethod() {field = 0;}

                    private void redundantMethod() {redundantField = 0;}
                }""";

        // Виклик методів класу Regular для аналізу класу
        Regular.findAttributes(classText);
        Regular.findMethods(classText);
        Regular.findConstructors(classText);

        // Проведення аналізу за допомогою класу Analysis
        ArrayList<Integer> selectedAttributes = new ArrayList<>();
        selectedAttributes.add(0); // Вибір атрибуту field

        JLabel label2 = new JLabel();
        String result = Analysis.analyzePattern(selectedAttributes, label2);

        // Перевірка результатів аналізу
        assertTrue(result.contains("private int field;"));
        assertTrue(result.contains("public TestClass(int field) {}"));
        assertTrue(result.contains("public void usefulMethod() {field = 0;}"));
        assertFalse(result.contains("private int redundantField;"));
        assertFalse(result.contains("public TestClass(int redundantField) {}"));
        assertFalse(result.contains("private void redundantMethod() {redundantField = 0;}"));
    }
}