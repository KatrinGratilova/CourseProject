package org.katrin;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
//import java.util.SortedMap;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;

public class Analysis {
    static List<String> attr; // потрібні атрибути
    static List<String> attrName; // назви потрібних атрибутів
    static List<String> redundantAttrName; // назви зайвих атрибутів

    static List<String> meth; // потрібні методи - використовують лише потрібні атрибути
    static List<String> redundantMeth; // зайві методи - використовують лише зайві атрибути
    static List<String> editMeth; // методи для редагування - використовують потрібні та зайві атрибути

    static List<String> constr; // потрібні конструктори - використовують лише потрібні атрибути
    static List<String> redundantConstr; // зайві конструктори - використовують лише зайві атрибути
    static List<String> editConstr; // конструктори для редагування - використовують потрібні та зайві атрибути

//    static List<String> methName;
//    static List<String> redundantMethName;
//    static List<String> editMethName;


    public static String analyzePattern(ArrayList<Integer> selectedAttributeNumbers, JLabel label2){
        analyzeAttr(selectedAttributeNumbers);
        analyzeMeth();
        analyzeConstr();

        int count = 1;
        count = edit(count, editMeth, meth, label2);
        edit(count, editConstr, constr, label2);

        StringBuilder pattern = new StringBuilder();

        for(String a: attr) {
            pattern.append(a).append("\n");
        }
        pattern.append("\n");
        for(String c: constr) {
            pattern.append(c).append("\n");
        }
        pattern.append("\n");
        for(String m: meth) {
            pattern.append(m).append("\n");
        }
        return pattern.toString();
    }

    private static int edit(int count, List<String> editMeth, List<String> meth, JLabel label2) {
        for (String m : editMeth) {
            JLabel label1 = new JLabel("Метод для редагування №" + count);
            String labelText = "<html><pre>" + m + "</pre></html>";

            label2.setText(labelText);
            JLabel label3 = new JLabel("Введіть новий код даного методу:");
            JTextArea textArea = new JTextArea(10, 30);
            JScrollPane scrollPane = new JScrollPane(textArea);
            textArea.setLineWrap(true);
            textArea.setWrapStyleWord(true);
            textArea.setBackground(new Color(254, 253, 223));

            Object[] message = {label1, label2, label3, scrollPane};

            int option = JOptionPane.showOptionDialog(
                    null,
                    message,
                    "Composition Converter",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    new String[]{"OK"},
                    "OK"
            );

            if (option == JOptionPane.OK_OPTION)
                meth.add(textArea.getText());

            count++;
        }
        return count;
    }

    private static void analyzeAttr(ArrayList<Integer> selectedAttributeNumbers) {
        attr = new ArrayList<>();
        attrName = new ArrayList<>();
        redundantAttrName = new ArrayList<>();

        // Додати вибрані атрибути до нового масиву
        for (int index : selectedAttributeNumbers) {
            if (index >= 0 && index < Regular.attr.size()) {
                attr.add(Regular.attr.get(index));
                attrName.add(Regular.attrName.get(index));
            }
        }

        // Додати ненужні атрибути до списку redundantAttr
        for (int i = 0; i < Regular.attr.size(); i++) {
            if (!attr.contains(Regular.attr.get(i))) {
                redundantAttrName.add(Regular.attrName.get(i));
            }
        }
    }

    private static void analyzeMeth() {
        meth = new ArrayList<>(Regular.meth);
        redundantMeth = new ArrayList<>();
        editMeth = new ArrayList<>();

//        methName = new ArrayList<>(Regular.methName);
//        redundantMethName = new ArrayList<>();
//        editMethName = new ArrayList<>();


        for (String m : meth) {
            for (String a : redundantAttrName) {
                if (m.contains(a)) {
                    redundantMeth.add(m);

//                    int i = meth.indexOf(m);
//                    redundantMethName.add(methName.get(i));

                    break; // Вихід з циклу, якщо знайдено співпадіння
                }
            }
        }
        meth.removeAll(redundantMeth);
        //methName.removeAll(redundantMethName);

        for (String m : redundantMeth) {
            for (String a : attrName) {
                if (m.contains(a)) {
                    editMeth.add(m);

                    //int i = redundantMeth.indexOf(m);
                    //editMethName.add(methName.get(i));

                    break; // Вихід з циклу, якщо знайдено співпадіння
                }
            }
        }
        redundantMeth.removeAll(editMeth);
        //redundantMethName.removeAll(editMethName);//


//        List<String> methNames = new ArrayList<>(redundantMethName);
//        methNames.addAll(editMethName);
//        for (String m : meth) {
//            for (String n : methNames) {
//
//                Pattern pt = Pattern.compile(n + "\\([^{}]*\\);");
//                Matcher mt = pt.matcher(m);
//
//                if (mt.find()) {
//                    System.out.println(mt.group());
//                    editMeth.add(m);
//                    meth.remove(m);
//                    break;
//                }
//            }
//        }

    }

    private static void analyzeConstr() {
        constr = new ArrayList<>(Regular.constr);
        redundantConstr = new ArrayList<>();
        editConstr = new ArrayList<>();

        for (String c : constr) {
            for (String a : redundantAttrName) {
                if (c.contains(a)) {
                    redundantConstr.add(c);
                    break; // Вихід з циклу, якщо знайдено співпадіння
                }
            }
        }
        constr.removeAll(redundantConstr);

        for (String m : redundantConstr) {
            for (String a : attrName) {
                if (m.contains(a)) {
                    editConstr.add(m);
                    break; // Вихід з циклу, якщо знайдено співпадіння
                }
            }
        }
        redundantConstr.removeAll(editConstr);
    }
}