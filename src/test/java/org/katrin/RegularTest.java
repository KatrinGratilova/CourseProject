package org.katrin;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class RegularTest {
    @Test
    public void testFindMethods_singleMethod() {
        String input = "public class Test { public void method1() {} }";
        String expected = "public class Test {  }";
        String actual = Regular.findMethods(input);

        assertEquals(expected, actual);
        List<String> methods = Regular.meth;
        assertEquals(1, methods.size());
        assertEquals("public void method1() {}", methods.get(0));
    }

    @Test
    public void testFindMethods_multipleMethods() {
        String input = "public class Test { public void method1() {} private int method2() { return 0; } }";
        String expected = "public class Test {   }";
        String actual = Regular.findMethods(input);

        assertEquals(expected, actual);
        List<String> methods = Regular.meth;
        assertEquals(2, methods.size());
        assertTrue(methods.contains("public void method1() {}"));
        assertTrue(methods.contains("private int method2() { return 0; }"));
    }

    @Test
    public void testFindMethods_noMethods() {
        String input = "public class Test { int x; }";
        String expected = "public class Test { int x; }";
        String actual = Regular.findMethods(input);

        assertEquals(expected, actual);
        List<String> methods = Regular.meth;
        assertEquals(0, methods.size());
    }

    @Test
    public void testFindAttributes() {
        String input = "public class Test { private int x; public String name = \"test\"; }";
        String expected = "public class Test {   }";
        String actual = Regular.findAttributes(input);

        assertEquals(expected, actual);
        List<String> attributes = Regular.attr;
        List<String> attributeNames = Regular.attrName;
        assertEquals(2, attributes.size());
        assertEquals(2, attributeNames.size());
        assertTrue(attributes.contains("private int x;"));
        assertTrue(attributes.contains("public String name = \"test\";"));
        assertTrue(attributeNames.contains("x"));
        assertTrue(attributeNames.contains("name"));
    }

    @Test
    public void testFindConstructors() {
        String input = "public class Test { public Test() {} private Test(int x) {} }";
        Regular.findConstructors(input);

        List<String> constructors = Regular.constr;
        assertEquals(2, constructors.size());
        assertTrue(constructors.contains("public Test() {}"));
        assertTrue(constructors.contains("private Test(int x) {}"));
    }
}
