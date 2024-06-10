package org.katrin;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Regular {
    static List<String> attr; // полний опис атрибутів
    static List<String> meth; // методи
    static List<String> constr; // конструктори
    static List<String> attrName; // назви всіх атрибутів
    //static List<String> methName; // назви всіх методів
    static String className;

    public static String showPattern(String classText) {
        StringBuilder result = new StringBuilder();

        String stWithoutMeth = Regular.findMethods(String.valueOf(classText));
        String stWithoutMethAndAttr = Regular.findAttributes(stWithoutMeth);
        Regular.findConstructors(stWithoutMethAndAttr);

        Pattern pt = Pattern.compile("(private\\s+|public\\s+|protected\\s+)?class\\s+(\\w+)\\s*\\{");
        Matcher mt = pt.matcher(classText);

        while (mt.find()) {
            CompositionConverterApplication.innerClassName = className = mt.group(2);
        }
        
        result.append("АТРИБУТИ:\n");
        for (String element : Regular.attr)
            result.append(element).append("\n");

        result.append("\nКОНСТРУКТОРИ:\n");
        for (String element : Regular.constr)
            result.append(element).append("\n");

        result.append("\nМЕТОДИ:\n");
        for (String element : Regular.meth)
            result.append(element).append("\n");

        return result.toString();
    }

    public static String findMethods(String patternClass) {
        meth = new ArrayList<>();
        //methName = new ArrayList<>();


        Pattern methodStartPattern = Pattern.compile("(private\\s+|public\\s+|protected\\s+)?" +
                "((static\\s+)?(final\\s+)?|(final\\s+)?(static\\s*)?)" +
                "(String|int|long|byte|short|char|boolean|double|float|void)(\\[])?\\s+(\\w+)\\([^{}]*\\)\\s*");

        Matcher methodStartMatcher = methodStartPattern.matcher(patternClass);

        while (methodStartMatcher.find()) {
            String methodStart = methodStartMatcher.group();
            String method = findMethodBody(patternClass, methodStart);
            meth.add(method);


            //String methodName = methodStartMatcher.group(9);
            //methName.add(methodName);

        }

        for(String method: meth) { // Видаляємо знайдений метод із рядка
            patternClass = patternClass.replace(method, "");
        }

        return patternClass;
    }

    // Пошук та повернення всієї конструкції методу з початком
    private static String findMethodBody(String st, String methodStart) {
        int startIndex = st.indexOf(methodStart);
        int braceCount = 0;
        boolean inMethod = false;
        int endIndex = startIndex;

        for (int i = startIndex; i < st.length(); i++) {
            char c = st.charAt(i);
            if (c == '{') {
                if (!inMethod) {
                    inMethod = true;
                }
                braceCount++;
            } else if (c == '}') {
                braceCount--;
                if (braceCount == 0) {
                    endIndex = i;
                    break;
                }
            }
        }

        return st.substring(startIndex, endIndex + 1);
    }

    private static String findAttributes(String patternClass) {
        attr = new ArrayList<>();
        attrName = new ArrayList<>();

        Pattern pt = Pattern.compile("(private\\s+|public\\s+|protected\\s+)?" +
                "((static\\s+)?(final\\s+)?|(final\\s+)?(static\\s+)?)" +
                "(String|int|long|byte|short|char|boolean|double|float)(\\[])?\\s*(\\w+)\\s*(=\\s*[^;]+)?\\s*;");
        Matcher mt = pt.matcher(patternClass);

        while (mt.find()) {
            String attribute = mt.group();
            attr.add(attribute);
            String attributeName = mt.group(9); // Отримуємо 9-у групу регулярного виразу, яка містить ім'я атрибуту
            attrName.add(attributeName);
        }

        return mt.replaceAll("");
    }

    private static void findConstructors(String patternClass) {
        constr = new ArrayList<>();

        Pattern pt = Pattern.compile("(private\\s+|public\\s+|protected\\s+)?\\w+\\([^{}]*\\)\\s*\\{");
        Matcher mt = pt.matcher(patternClass);

        while (mt.find()) {
            String methodStart = mt.group();
            String method = findMethodBody(patternClass, methodStart);
            constr.add(method);
        }
    }
}