package job;

import java.util.ArrayList;
import construction.*;
import static pictogramming.Main.animaOrder;

public class Calculation {

    public static String run(ArrayList<String> formula, String type, MethodConstruction main, int depth) throws Exception {
        //変数を数値に変える
        for (int i = 0; i < formula.size(); i++) {
            String str = formula.get(i);
            VariableConstruction var = main.getVariable(str, depth);
            if (var == null)continue;
            
            String value = var.getValue();
            formula.set(i, value);
        }

        minusCheck(formula, type);

        if (formula.size() == 1)return formula.get(0);
        
        animaOrder.calculationStart(formula); //箱をパンチして近づけるまで
        return brackets(formula, type);
    }

    private static void minusCheck(ArrayList<String> formula, String type) throws Exception {
        boolean minus = true;
        for (int i = 0; i < formula.size(); i++) {
            String str1 = formula.get(i);
            if (minus) {
                if (str1.equals("-")) {
                    String str2 = formula.get(i + 1);
                    if (str2.equals("-") == false) {
                        formula.remove(i + 1);

                        double value = Double.valueOf(str2);
                        double result = value * -1;
                        String resultString;
                        if (type.equals("int")) {
                            int intCalc = (int) result;
                            resultString = Integer.toString(intCalc);
                        } else {//double型
                            resultString = Double.toString(result);
                        }
                        formula.set(i, resultString);
                    }
                }
                minus = false;

            } else{
                if (str1.equals("+"))minus = true;
                else if (str1.equals("-"))minus = true;
                else if (str1.equals("*"))minus = true;
                else if (str1.equals("/"))minus = true;
                else if (str1.equals("%"))minus = true;
            }
        }
    }

    private static String brackets(ArrayList<String> formula, String type) throws Exception {
        return brackets(formula, type, 0);
    }

    private static String brackets(ArrayList<String> formula, String type, int depth) throws Exception {
        int index = 0;
        while (index < formula.size()) {
            ArrayList<String> inFormula = new ArrayList<>(); //どこからどこまでのかっこか
            int brackets = 0;//(かっこの数)
            boolean flag = false;
            while (index < formula.size()) {
                String str = formula.get(index);
                if (str.equals("(")) {
                    flag = true;
                    brackets++;
                } else if (str.equals(")")) {
                    brackets--;
                }

                if (flag) {
                    inFormula.add(str);
                    formula.remove(index);
                    if (brackets == 0) { //(かっこ全てを取り出せた)
                        break;
                    }
                } else {
                    index++;
                }
            }
            if (flag) {
                inFormula.remove(0);//頭の（を外す
                inFormula.remove(inFormula.size() - 1);//後ろの）を外す
                if (inFormula.size() == 1) {
                    formula.add(index, inFormula.get(0));
                } else {
                    String result = brackets(inFormula, type, depth + 1);
                    formula.add(index, result);
                    animaOrder.calculation(formula);
                }
            }
        }
        String result = calculation(formula, type);
        if (depth == 0) {//animation用
            ArrayList<String> form = new ArrayList<>();
            form.add(result);
            animaOrder.calculation(form);
        }
        return result;
    }

    private static String calculation(ArrayList<String> formula, String type) throws Exception {
        if (formula.size() == 1) {
            return formula.get(0);
        }

        //キャストの計算
        for (int i = 0; i < formula.size(); i++) {
            String str = formula.get(i);
            if (str.equals("int")) { //数字の前にintがあるか確認
                String value = formula.get(i + 1);
                double doubleCalc = Double.valueOf(value);
                int intCalc = (int) doubleCalc;
                value = Integer.toString(intCalc);
                formula.set(i + 1, value);
                formula.remove(i);
                i--;
            } else if (str.equals("double")) { //数字の前にdoubleがあるか確認
                String value = formula.get(i + 1);
                double doubleCalc = Double.valueOf(value);
                value = Double.toString(doubleCalc);
                formula.set(i + 1, value);
                formula.remove(i);
                i--;
            }
        }

        while (formula.size() > 2) {
            int joinIndex = 0;//合体させる演算子を見つける
            int weight = 0;//合体させる演算子の重要度
            for (int i = 0; i < formula.size(); i++) {
                String str = formula.get(i);
                if (weight < 1) {
                    if (str.equals("+")) {
                        joinIndex = i;
                        weight = 1;
                    } else if (str.equals("-")) {
                        joinIndex = i;
                        weight = 1;
                    }
                }
                if (weight < 2) {
                    if (str.equals("*")) {
                        joinIndex = i;
                        weight = 2;
                    } else if (str.equals("/")) {
                        joinIndex = i;
                        weight = 2;
                    } else if (str.equals("%")) {
                        joinIndex = i;
                        weight = 2;
                    }
                }
            }
            //演算子と隣り合う数値と演算子を抽出
            String value1 = formula.get(joinIndex - 1);
            String value2 = formula.get(joinIndex + 1);
            String operator = formula.get(joinIndex);
            String result = join(value1, value2, operator);

            formula.set(joinIndex, result);//演算子の部分に新しい数値を入れる
            formula.remove(joinIndex + 1);//使用された数値は削除（上から）
            formula.remove(joinIndex - 1);
        }
        String calc = formula.get(0);

        String result;
        if (type.equals("int")) {
            double doubleCalc = Double.valueOf(calc);
            int intCalc = (int) doubleCalc;
            result = Integer.toString(intCalc);
        } else {//double型
            double doubleCalc = Double.valueOf(calc);
            result = Double.toString(doubleCalc);
        }
        return result;
    }

    private static String join(String val1, String val2, String operator) throws Exception {
        double value1 = Double.valueOf(val1);//計算する１つ目の値
        double value2 = Double.valueOf(val2);//計算する２つ目の値

        double calc;
        if (operator.equals("%"))calc = value1 % value2;
        else if (operator.equals("+"))calc = value1 + value2;
        else if (operator.equals("-"))calc = value1 - value2;
        else if (operator.equals("*"))calc = value1 * value2;
        else calc = value1 / value2;//"/"のとき
        
        return Double.toString(calc);
    }
}
