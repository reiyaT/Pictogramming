package job;

import construction.MethodConstruction;
import construction.VariableConstruction;
import static pictogramming.Main.animaOrder;
import java.util.ArrayList;
import job.Calculation;

public class ConditionJudge { //条件式の計算
    public static String judgeString(ArrayList<String> condition,MethodConstruction main,int depth)throws Exception{
        boolean result = judge(condition,main,depth,false);
        if(result)return "true";
        else return "false";
    }
    
    public static boolean judge(ArrayList<String> condition,MethodConstruction main,int depth,boolean animaIf)throws Exception{
        ArrayList<String> con = new ArrayList<String>();
        con.addAll(condition);
        animaOrder.setSaveFormula(con); //一時保存

        String type = "int";//計算するときの型
        
        //変数は中身を取り出す
        boolean varIsThere = false;//animation用
        
        for(int i = 0;i < con.size();i++){
            String str = con.get(i);
            VariableConstruction var = main.getVariable(str, depth);
            if(var == null){
                if(isDouble(str))type = "double";
                continue;
            }
            con.set(i,var.getValue());
            varIsThere = true;
            
            if(var.getType().equals("double"))type = "double";
        }
        if(varIsThere && animaIf)animaOrder.conditionVariableOut(con);
        if(animaIf == false)animaOrder.calculationStart(con);
        
        if(con.size() == 1){
            String str = con.get(0);
            if(str.equals("true"))return true;
            else if(str.equals("false"))return false;
        }
        //ステップ１：式は計算
        animaOrder.setAnimationMake(false);
        
        int index = 0;
        while(index < con.size()){
            //計算式を探す
            while(index < con.size()){
                String str = con.get(index);
                if(str.equals("=") || str.equals(">") || str.equals("<")||str.equals("&")||str.equals("|")||str.equals("!")||str.equals(")")||str.equals("true")||str.equals("false")){
                    index++;
                }else{
                    break;
                }
            }
            //演算子があるところまで受け取る
            ArrayList<String> formula = new ArrayList<String>();
            for(int i = index;i < con.size();i++){
                String str = con.get(i);
                if(str.equals("=") || str.equals(">") || str.equals("<")||str.equals("&")||str.equals("|")||str.equals("!")||str.equals("true")||str.equals("false"))break;
                formula.add(str);
            }
            //余分についたカッコを取り外す
            int brackets = 0;
            for(String f : formula){
                if(f.equals("("))brackets++;
                if(f.equals(")"))brackets--;
            }
            while(brackets != 0){
                if(brackets < 0){
                    formula.remove(formula.size()-1);
                    brackets++;
                }else if(brackets > 0){
                    formula.remove(0);
                    brackets--;
                    index++;
                }
            }
            //式として受け取った部分だけを削除する
            for(String f : formula){
                for(int i = index;i < con.size();i++){
                    if(f.equals(con.get(i))){
                        con.remove(i);
                        break;
                    }
                }
            }
            //計算して格納しなおす
            if(formula.size() > 0){
                String result = Calculation.run(formula,type,main, depth);
                con.add(index,result);
                index++;
            }
        }
        animaOrder.setAnimationMake(true);
        if(animaIf)animaOrder.newFormulaSet(con);
        else animaOrder.calculation(con);
        
        String result = calculation(con,animaIf);
        return Boolean.valueOf(result);
    }
    
    private static boolean isDouble(String num)throws Exception{
        for(int i = 0;i < num.length();i++){
            String s = num.substring(i,i+1);
            if(s.equals("."))return true;
        }
        return false;
    }
    
    private static String calculation(ArrayList<String> con,boolean animaIf)throws Exception{
        int index = 0;
        while(index < con.size()){
            ArrayList<String> formula = new ArrayList<>();
            int brackets = 0;
            boolean flag = false;
            while(index < con.size()){
                String str = con.get(index);
                if(str.equals("(")){
                    flag = true;
                    brackets++;
                }else if(str.equals(")")){
                    brackets--;
                }
                if(flag){
                    formula.add(str);
                    con.remove(index);
                    if(brackets == 0)break;
                }else{
                    index++;
                }
            }
            if(flag){
                formula.remove(0);//頭の（を外す
                formula.remove(formula.size()-1);//後ろの）を外す
                String result = calculation(formula,animaIf);
                con.add(index,result);
            }
        }
        if(animaIf)animaOrder.newFormulaSet(con);
        else animaOrder.calculation(con);
        //()が外せた後中身を結合して１つにする
        //まずtrueかfalseに変える
        index = 0;
        while(index < con.size()){
            //式を探す
            while(index < con.size()){
                String str = con.get(index);
                if(str.equals("&")||str.equals("|")||str.equals("true")||str.equals("false")||str.equals("!")||str.equals("="))index++;
                else break;
            }
            //式を取り出す
            ArrayList<String> formula = new ArrayList<>();
            int numCount = 0;
            while(index < con.size()){
                String str = con.get(index);
                if((str.equals("<")||str.equals(">")||str.equals("=")||str.equals("!"))==false)numCount++;//演算子以外（数字）が来たらcountをあげる
                con.remove(index);
                formula.add(str);
                if(numCount == 2)break;
            }
            if(formula.size() > 0){
                String result = trueOrFalse(formula);
                con.add(index,result);
            }
            index++;
        }
        if(animaIf)animaOrder.newFormulaSet(con);
        else animaOrder.calculation(con);
        //次にtrueとfalseの計算をする
        //否定演算子を外す
        index = 0;
        while(index < con.size()-1){
            while(index < con.size()-1){
                String str = con.get(index);
                String str2 = con.get(index+1);
                if(str.equals("!") && (str2.equals("true")||str2.equals("false")))break;
                index++;
            }
            if(index < con.size()-1){
                con.remove(index);//否定演算子�?�削除
                String str = con.get(index);
                if(str.equals("true"))con.set(index,"false");
                else con.set(index, "true");
                
                index++;
            }
        }
        if(animaIf)animaOrder.newFormulaSet(con);
        else animaOrder.calculation(con);
        //==と!=の計算
        index = 0;
        while(index < con.size()){
            //式を探�す
            while(index < con.size()){
                String str = con.get(index);
                if(str.equals("=")||str.equals("!"))break;
                index++;
            }
            
            if(index < con.size()){
                String operator = con.get(index);
                String value1 = con.get(index-1);
                String value2 = con.get(index+2);
                if(operator.equals("=")){
                    if(value1.equals(value2))con.set(index-1, "true");
                    else con.set(index-1, "false");
                }else{
                    if(value1.equals(value2))con.set(index-1, "false");
                    else con.set(index-1, "true");
                }
                con.remove(index);//演算子１つめを削除
                con.remove(index);//演算子２つめを削除
                con.remove(index);//２つめの値を削除
            }
        }
        if(animaIf)animaOrder.newFormulaSet(con);
        else animaOrder.calculation(con);
        //||と&&の計算
        while(con.size() > 1){
            index = 0;
            //式を探す
            while(index < con.size()){
                String str = con.get(index);
                if(str.equals("&")||str.equals("|"))break;
                index++;
            }

            if(index < con.size()){
                String operator = con.get(index);
                String value1 = con.get(index-1);
                String value2 = con.get(index+2);
                if(operator.equals("&")){
                    if(value1.equals("true")&&value2.equals("true"))con.set(index-1, "true");
                    else con.set(index-1, "false");
                }else{
                    if(value1.equals("true")||value2.equals("true"))con.set(index-1, "true");
                    else con.set(index-1, "false");
                }
                con.remove(index);//演算子１つめを削除
                con.remove(index);//演算子２つめを削除
                con.remove(index);//２つめの値を削除
                index++;
            }
        }
        if(animaIf)animaOrder.newFormulaSet(con);
        else animaOrder.calculation(con);
        
        return con.get(0);
    }
    
        
    private static String trueOrFalse(ArrayList<String> formula)throws Exception{
        String result;
        //式を構成する数によって「数（演算子）（演算子）数」か「数（演算子）数」か判断
        if(formula.size() == 3){
            double value1 = Double.valueOf(formula.get(0));
            double value2 = Double.valueOf(formula.get(2));
            String operator = formula.get(1);
                
            if(operator.equals("<")){
                if(value1 < value2)result = "true";
                else result = "false";
            }else{//>だったら
                if(value1 > value2)result = "true";
                else result = "false";
            }
                
        }else if(formula.size() == 4){
            double value1 = Double.valueOf(formula.get(0));
            double value2 = Double.valueOf(formula.get(3));
            String operator = formula.get(1);
                
            if(operator.equals("<")){
                if(value1 <= value2)result = "true";
                else result = "false";
            }else if(operator.equals(">")){
                if(value1 >= value2)result = "true";
                else result = "false";
            }else if(operator.equals("!")){
                if(value1 != value2)result = "true";
                else result = "false";
            }else{//=だったら
                if(value1 == value2)result = "true";
                else result = "false";
            }
        }else result = formula.get(0);
        
        return result;
    }
    
    private static String join(String value1,String value2,String operator)throws Exception{
        boolean val1 = Boolean.valueOf(value1);
        boolean val2 = Boolean.valueOf(value2);
        
        String result;
        if(operator.equals("&&")){
            if(val1 && val2)result = "true";
            else result = "false";
        }else{
            if(val1 || val2)result = "true";
            else result = "false";
        }
        return result;
    }
}
