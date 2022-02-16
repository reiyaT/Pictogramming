package job;

import construction.*;
import static pictogramming.Main.animaOrder;
import java.util.ArrayList;

public class StringCalculation {
    public static String StringCalcRun(ArrayList<String> formula,MethodConstruction main,int depth)throws Exception{
        ArrayList<String> form = new ArrayList<String>();
        form.addAll(formula);
        
        
        //変数は中身を取り出す      
        for(int i = 0;i < form.size();i++){
            String str = form.get(i);
            VariableConstruction var = main.getVariable(str, depth);
            if(var == null)continue;
            form.set(i,var.getValue());
        }
        animaOrder.calculationStart(form);
        animaOrder.setSaveFormula(form);
        System.out.println(form);
        String result = join(form);
        //↓animation用
        form.clear();
        form.add(result);
        System.out.println(form);
        animaOrder.calculation(form);
        
        return result;
    }
    
    public static String pirntCalcRun(ArrayList<String> formula,MethodConstruction main,int depth)throws Exception{
        animaOrder.printStart(formula, main, depth);
        
        ArrayList result = new ArrayList<>();
        ArrayList<String> animaFormula = new ArrayList<>();
        //変数を探す
        boolean quotation = false;//["]の有無
        for(int i= 0;i < formula.size();i++){
            String str = formula.get(i);
            if(str.equals("\""))quotation = !quotation;
            if(quotation){//["]ないときだけ変数化チェックし取り出す
                result.add(str);
                animaFormula.add(str);
            }else{
                VariableConstruction var = main.getVariable(str, depth);
                if(var == null){
                    result.add(str);
                    animaFormula.add(str);
                }
                else {
                    result.add(var);
                    animaFormula.add(var.getValue());
                }
            }
        }
        
        animaOrder.calculationStart(animaFormula);
        
        animaOrder.setAnimationMake(false);
        String ans = calculation(result, main, depth);
        animaOrder.setAnimationMake(true);
        
        animaFormula.clear();
        animaFormula.add(ans);
        animaOrder.calculation(animaFormula);
        animaOrder.printEnd();
        return ans;
    }
    
    private static String calculation(ArrayList formula,MethodConstruction main,int depth)throws Exception{
        int index = 0;
        while(index < formula.size()){
            ArrayList inFormula = new ArrayList<>();
            int brackets = 0;
            boolean flag = false;
            while(index < formula.size()){
                Object obj = formula.get(index);
                if(obj instanceof String){
                    String str = (String)formula.get(index);
                    if(str.equals("(")){
                        flag = true;
                        brackets++;
                    }else if(str.equals(")")){
                        brackets--;
                    }
                }
                
                if(flag){
                    inFormula.add(obj);
                    formula.remove(index);
                    if(brackets == 0)break;
                }else{
                    index++;
                }
            }
            if(flag){
                inFormula.remove(0);//頭の（を外す
                inFormula.remove(inFormula.size()-1);//後ろの）を外す
                String result = calculation(inFormula,main,depth);

                formula.add(index,result);
                animaOrder.calculation(formula);
            }
        }
        
        //さらに式をString型くるまでの式にしぼる。なぜならString型の前後で計算が違うから
        ArrayList<String> calcFormula = new ArrayList<>();//CalculationクラスもConditionJudgeクラスもArrayListがString型のため、Stringに戻す。コンストラクタを追加してそのまま送っても良い。
        //中身が何か考える。計算式なのか、条件式なのか、文字結合なのか
        int type = -1;//0なら計算式(double)、1なら計算式(int)、2なら条件式(boolean)
        while(formula.size() > 0){
            Object obj = formula.get(0);
            if(obj instanceof String){
                String str = (String)obj;
                if(isNumber(str)){
                    if(isDouble(str)){
                        if(type < 0)type = 0;
                    }
                    else{
                        if(type < 1)type = 1;
                    }
                }else{
                    if(str.equals("\""))break;
                    if(str.equals("=") || str.equals(">") || str.equals("<") || str.equals("!") || str.equals("&") || str.equals("|") || str.equals("true")|| str.equals("false"))type = 2;
                }
                calcFormula.add(str);
            }else if(obj instanceof VariableConstruction){
                VariableConstruction var = (VariableConstruction) obj;
                if(var.getType().equals("double")){
                    if(type < 0)type = 0;
                }else if(var.getType().equals("int")){
                    if(type < 1)type = 1;
                }else if(var.getType().equals("boolean")){
                    type = 2;
                }else if(var.getType().equals("String"))break;
                calcFormula.add(var.getName());
            }
            formula.remove(0);
        }
        
        if(type > -1){
            String result;
            if(type == 0){
                result = Calculation.run(calcFormula,"double", main, depth);
            }else if(type == 1){
                result = Calculation.run(calcFormula,"int", main, depth);
            }else{
                result = ConditionJudge.judgeString(calcFormula, main, depth);
            }
            formula.add(0,result);
        }
        
        String result = join(formula);
        
        return result;
    }
    
    private static boolean isNumber(String str)throws Exception{
        try{
            Integer.parseInt(str);
            return true;
        }catch(NumberFormatException e){
            return false;
        }
    }
    
    private static boolean isDouble(String num)throws Exception{
        for(int i = 0;i < num.length();i++){
            String s = num.substring(i,i+1);
            if(s.equals("."))return true;
        }
        return false;
    }
    
    private static String join(ArrayList formula)throws Exception{
        String result = "";
        boolean quotation = false;
        for(Object obj : formula){
            if(obj instanceof String){
                String str = (String)obj;
                if(str.equals("\"")){
                    quotation = !quotation;
                    continue;
                }
                if(quotation == false && str.equals("+"))continue;
                result += str;
            }else{
                VariableConstruction var = (VariableConstruction)obj;
                result += var.getValue();
            }
        }
        return result;
    }
}
