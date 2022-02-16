package job;
import construction.BlockConstruction;
import java.util.ArrayList;

import construction.VariableConstruction;
import construction.MethodConstruction;
import static pictogramming.Main.animaOrder;
import program.Program;

//変数の宣言や代入を司る
public class VariableControl {
    //引数：分割したプログラム、探索用のメーン、所属するブロック
    public static VariableConstruction declaration(Program source,MethodConstruction main,BlockConstruction block,int depth,boolean runFor)throws Exception{
        String type = source.get();//型
        String name = source.get();//変数名
        String symbol = source.get();//初期化があれば=が、そうでなければ;が入る
        
        if(main.getVariable(name,depth) != null)throw new Exception();

        VariableConstruction var = new VariableConstruction(type,name,depth);
        animaOrder.declarationStart(var,runFor);
        
        if(symbol.equals("=")){
            String value = initialization(source,main,var,depth);
            var.setValue(value);
            animaOrder.initializeEnd(value);
        }
        animaOrder.declarationEnd();
        return var;
    }
    
    //初期化を行う。引数は分割したプログラム、探索用のメーン、初期化する変数名
    //すでに宣言済みの変数の初期化
    public static void initialization(Program source,MethodConstruction main,int depth,boolean forEnd)throws Exception{
        String name  = source.get();
        VariableConstruction var = main.getVariable(name,depth);
        if(var == null)throw new Exception();
        source.countNext();//=を削除
        animaOrder.initializeSetting(var,forEnd);
        String value = initialization(source,main,var,depth);
//        System.out.println("check : " +value);
        var.setValue(value);
        
        animaOrder.initializeEnd(value);
        animaOrder.declarationEnd();
    }
    
    //初期化
    private static String initialization(Program source,MethodConstruction main,VariableConstruction var,int depth)throws Exception{
        //";"が来るまで先頭から文字を取得し、取得後削除
        ArrayList<String> formula = new ArrayList<String>();
        while(true){
            String str = source.get();
            if(str.equals(";"))break;
            formula.add(str);
        }
        if(formula.size() == 0)throw new Exception();
        animaOrder.initializeStart(formula,main,depth);
        String value = "";
        if(var.getType().equals("boolean"))value = ConditionJudge.judgeString(formula,main,depth);
        else if(var.getType().equals("String"))value = StringCalculation.StringCalcRun(formula,main,depth);
        else if(var.getType().equals("int")||var.getType().equals("double"))value = Calculation.run(formula,var.getType(),main,depth);
        
        return value;
    }
    
    //"+="などの初期化
    public static void operatorInitialization(Program source,MethodConstruction main,int depth,boolean forEnd)throws Exception{
        String name = source.get();
        String operator = source.get(); //+= or -= or...
        source.countNext();//"="を削除
        
        VariableConstruction var = main.getVariable(name,depth);
        if(var == null)throw new Exception();
        String saveValue = var.getValue();//一度値を保存
        animaOrder.initializeSetting(var,operator,forEnd);
        
        String formulaValue = initialization(source,main,var,depth);//+=の右辺を計算して初期化
        
        //saveしていた値を計算して初期化
        ArrayList<String> formula = new ArrayList<String>();//(num += 35) → num = 2 + 35を計算
        formula.add(saveValue);
        formula.add(operator);
        formula.add(formulaValue);
        
        animaOrder.setAnimationMake(false);
        String value;
        if(var.getType().equals("String"))value = StringCalculation.StringCalcRun(formula, main, depth);
        else value = Calculation.run(formula,var.getType(), main,depth);
        var.setValue(value);
        animaOrder.setAnimationMake(true);
        
        animaOrder.operatorInitialization(value);
        animaOrder.declarationEnd();
    }
    
    //インクリメント
    public static void increment(Program source,VariableConstruction var,boolean forEnd)throws Exception{
        animaOrder.incrementStart(var,forEnd);
        increment(var);
        //System.out.println(" !! " +var.getValue());
        animaOrder.incrementEnd(var,forEnd);
        source.countNext(4);//変数名++;を削除

    }
    
    public static void increment(VariableConstruction var)throws Exception{
        String val = var.getValue();
        String type = var.getType();
        if(type.equals("int") || type.equals("double")){
            double douVal = Double.valueOf(val).doubleValue();
            douVal++;
            if(type.equals("int")){
                int intVal = (int)douVal;
                String strVal = Integer.toString(intVal);
                var.setValue(strVal);
            }else{
                String strVal = Double.toString(douVal);
                var.setValue(strVal);
            }
        }else{
        	throw new Exception();
        }
    }
    
    //デクリメント ++ --
    public static void decrement(Program source,VariableConstruction var, boolean forEnd)throws Exception{
        animaOrder.decrementStart(var,forEnd);
        decrement(var);
        //System.out.println(" !! " +var.getValue());
        animaOrder.decrementEnd(var,forEnd);
        source.countNext(4);//変数名--;を削除
    }
    
    public static void decrement(VariableConstruction var)throws Exception{
        String val = var.getValue();
        String type = var.getType();
        if(type.equals("int") || type.equals("double")){
            double douVal = Double.valueOf(val).doubleValue();
            douVal--;
            if(type.equals("int")){
                int intVal = (int)douVal;
                String strVal = Integer.toString(intVal);
                var.setValue(strVal);
            }else{
                String strVal = Double.toString(douVal);
                var.setValue(strVal);
            }
        }else{
        	throw new Exception();
        }
    }
}
