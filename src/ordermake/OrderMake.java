package ordermake;

import construction.ForConstruction;
import construction.IfCondition;
import construction.WhileConstruction;
import construction.MethodConstruction;
import construction.VariableConstruction;
import java.util.ArrayList;
import view.Field;
import view.Human;
import view.MainView;

public class OrderMake { //ordermakeが本を生成し、makeorderに本を収納
    private ArrayList<Order> order = new ArrayList<>();
    private Order makeOrder;
    private boolean animationMake;//アニメをつくるかどうか
    private boolean makeNow = false;//今アニメを作っているかどうか
    private boolean forNow = false;//今for分の中にいるかどうか
    private boolean whileNow = false;
    
    private boolean doorStart = false;
    private int forLoop = 0;//forの中かどうか0なら違う
    private int whileLoop = 0;
    
    private int depth = 0;//使わないかも
    
    public void makeStart(int fieldType,int depth){
        if(this.makeNow)return;
        this.makeOrder = new Order(fieldType,doorStart,forLoop != 0,depth);
        //this.makeOrder = new Order(fieldType,doorStart,whileLoop != 0,depth);
        this.doorStart = false;
        this.animationMake = true;
        this.makeNow = true;
    }
    
    public void makeEnd(){
        if(this.makeNow == false)return;
        OrderStatus runPose = new OrderStatus(6,10);
        makeOrder.addHumanOrder(runPose);
        
        OrderStatus run = new OrderStatus(1,MainView.width-200);
        makeOrder.addHumanOrder(run);
        
        OrderStatus jump = new OrderStatus(3,MainView.height+75f);
        makeOrder.addHumanOrder(jump);
        
        this.order.add(this.makeOrder);
        this.makeNow = false;
    }
    
    public void programEnd(){
        simpleEnd();
    }
    
    public void simpleEnd(){
        if(this.makeNow == false)return;

        this.order.add(this.makeOrder);
        this.makeNow = false;
    }

    public void EndMove(){ //追加
        if(this.makeNow == false)return;

        //キャラが落ちて旗の手前でガッツポーズして終了
        OrderStatus run = new OrderStatus(1,MainView.width-650);
        makeOrder.addHumanOrder(run);

        OrderStatus gut = new OrderStatus(6,7);
        makeOrder.addHumanOrder(gut);

        OrderStatus wait1sec = new OrderStatus(8,1);
        makeOrder.addHumanOrder(wait1sec);

        this.order.add(this.makeOrder);
        this.makeNow = false;
    } //ここまで追加
    
    public ArrayList<Order> getOrder(){
        return this.order;
    }
    
    public void setAnimationMake(boolean judge){
        this.animationMake = judge;
    }
    
    //-----------------------------------------------------------------------------------------------
    //変数の宣言
    public void declarationStart(VariableConstruction var,boolean runFor){
        if(this.animationMake == false)return;
        makeOrder.addSource(var.getType());
        makeOrder.addSource(var.getName());
        
        makeOrder.setVariable(var.copy());
        
        OrderStatus run = new OrderStatus(1,170f);
        makeOrder.addHumanOrder(run);
        
        OrderStatus stand = new OrderStatus(7);
        makeOrder.addHumanOrder(stand);

        OrderStatus speak = new OrderStatus(5,var.getType() + " "+var.getName());
        makeOrder.addHumanOrder(speak);
        
        OrderStatus pose1 = new OrderStatus(6,1);
        OrderStatus declaration = new OrderStatus(2);
        makeOrder.addHumanVariableOrder(pose1,declaration);
        
        OrderStatus wait = new OrderStatus(8);
        makeOrder.addHumanOrder(wait);
        
        if(runFor)makeOrder.changeField(6); //土台が消えるタイミングで空間を変える（forなら大きい、それ以外は一つの画面）
        else makeOrder.changeField(0);
        makeOrder.addHumanOrder(stand);
    }
    
    public void declarationEnd(){
        if(this.animationMake == false)return;
        
        makeOrder.addSource(";");
        
        OrderStatus pose5 = new OrderStatus(6,5);
        OrderStatus varBoxSet = new OrderStatus(6);
        makeOrder.addHumanVariableOrder(pose5, varBoxSet);
        
        OrderStatus wait = new OrderStatus(8);
        makeOrder.addHumanOrder(wait);
        
        OrderStatus varBoxOut = new OrderStatus(3);
        makeOrder.addVariableOrder(varBoxOut);

        makeOrder.addHumanOrder(wait);
    }
    
    public void initializeStart(ArrayList<String> formula,MethodConstruction main,int depth){
        if(this.animationMake == false)return;
        
        makeOrder.addSource("=");
        makeOrder.addSource(formula);

        float placeX = makeOrder.setFormulaList(formula,main,depth,false);//placeXは先頭の変数の座標が格納される
        System.out.println("place : " +placeX);
        
        OrderStatus run1 = new OrderStatus(1,170f);
        makeOrder.addHumanOrder(run1);
        
        OrderStatus pose5 = new OrderStatus(6,5);
        OrderStatus varBoxSet = new OrderStatus(6);
        makeOrder.addHumanVariableOrder(pose5, varBoxSet);
        
        OrderStatus wait = new OrderStatus(8);
        makeOrder.addHumanOrder(wait);
        
        OrderStatus operatorView = new OrderStatus(5);
        makeOrder.addHumanFieldOrder(wait,operatorView);
        
        OrderStatus appear = new OrderStatus(1);
        makeOrder.addFormulaOrder(appear);
        
        makeOrder.addHumanOrder(wait);
        if(placeX > 0){
            OrderStatus run2 = new OrderStatus(1,placeX);
            makeOrder.addHumanOrder(run2);

            OrderStatus punch = new OrderStatus(4);
            OrderStatus varBoxOut = new OrderStatus(4);
            makeOrder.addHumanFormulaOrder(punch, varBoxOut);
        }
    }
    
    public void initializeEnd(String value){
        if(this.animationMake == false)return;
        
        OrderStatus run = new OrderStatus(1,170f);
        makeOrder.addHumanOrder(run);
        
        OrderStatus pose5 = new OrderStatus(6,5);
        makeOrder.addHumanOrder(pose5);
        
        OrderStatus wait = new OrderStatus(8);
        makeOrder.addHumanOrder(wait);
        
        OrderStatus operatorNotView = new OrderStatus(5);
        makeOrder.addHumanFieldOrder(wait,operatorNotView);
        
        OrderStatus initialize = new OrderStatus(2);
        makeOrder.addFormulaOrder(initialize);
        
        OrderStatus valueSet = new OrderStatus(7,value);
        OrderStatus notVisiualFormula = new OrderStatus(8);
        makeOrder.addVariableFormulaOrder(valueSet, notVisiualFormula);
        
        makeOrder.addHumanOrder(wait);
    }
    
    public void calculationStart(ArrayList<String> formula){
        if(this.animationMake == false)return;
        
        ArrayList<String> form =(ArrayList<String>) formula.clone();
        OrderStatus newFormulaSet = new OrderStatus(5,form);
        //OrderStatus run = new OrderStatus(1,MainView.width/2);
        makeOrder.addFormulaOrder(newFormulaSet);
        
        OrderStatus pose5 = new OrderStatus(6,4);
        makeOrder.addHumanOrder(pose5);
    }
    
    public void calculation(ArrayList formula){
        if(this.animationMake == false)return;
        
        //
        //同じ式だった場合は可視化しない  キャストなどの時
        if(this.saveFormula.size() == 0){
            this.saveFormula.addAll(formula);
        }else{
            boolean re = true;
            if(formula.size() == this.saveFormula.size()){
                for(int i = 0;i < formula.size();i++){
                    re = formula.get(i).equals(this.saveFormula.get(i));
                }
            }else re = false;
            System.out.println(re);
            if(re)return;
            else{
                this.saveFormula.clear();
                this.saveFormula.addAll(formula);
            }
        }
        //
        OrderStatus run = new OrderStatus(1,MainView.width/2);
        makeOrder.addHumanOrder(run);
        //
        OrderStatus emphasis = new OrderStatus(6);
        OrderStatus wait2sec = new OrderStatus(8,2);
        makeOrder.addHumanFormulaOrder(wait2sec, emphasis);
        
        OrderStatus pose6 = new OrderStatus(6,6);
        ArrayList<String> form =(ArrayList<String>) formula.clone();
        OrderStatus newFormulaSet = new OrderStatus(5,form);
        makeOrder.addHumanFormulaOrder(pose6, newFormulaSet);
        
        makeOrder.addHumanOrder(wait2sec);
        
        OrderStatus worry = new OrderStatus(6,4);
        OrderStatus notEmphasis = new OrderStatus(7);
        makeOrder.addHumanFormulaOrder(worry, notEmphasis);
        
        OrderStatus wait = new OrderStatus(8);
        makeOrder.addHumanOrder(wait);
    }
    
    //宣言済み変数の初期化で使用
    public void initializeSetting(VariableConstruction var,boolean forEnd){
        if(this.animationMake == false)return;
        
        makeOrder.addSource(var.getName());
        
        makeOrder.setVariable(var.copy());
        
        OrderStatus varBoxSet;
        if(forEnd)varBoxSet = new OrderStatus(6,170f+MainView.width);
        else varBoxSet = new OrderStatus(6);
        makeOrder.headOrder(2, varBoxSet);
        
        OrderStatus run = new OrderStatus(1,170f);
        makeOrder.addHumanOrder(run);
    }

    //+=でしよう
    public void initializeSetting(VariableConstruction var,String operator,boolean forEnd){
        initializeSetting(var,forEnd); //箱と前の値 計算後の値
        makeOrder.setOperator(operator); //+ or -を表示
        makeOrder.addSource(operator);
    }
    
    public void operatorInitialization(String value){
        if(this.animationMake == false)return;
        
        OrderStatus run = new OrderStatus(1,170f);
        makeOrder.addHumanOrder(run);
        
        OrderStatus pose5 = new OrderStatus(6,5);
        makeOrder.addHumanOrder(pose5);
        
        OrderStatus wait = new OrderStatus(8);
        makeOrder.addHumanOrder(wait);
        
        OrderStatus varBoxUp = new OrderStatus(4);
        OrderStatus fomulaUp = new OrderStatus(3);
        OrderStatus operatorUp = new OrderStatus(6);
        makeOrder.addAllOrder(null, varBoxUp, operatorUp, fomulaUp); //値と演算子が上に移動
        makeOrder.addVariableFormulaOrder(varBoxUp, fomulaUp);
        
        makeOrder.addHumanOrder(wait);
        
        OrderStatus initialize = new OrderStatus(2);
        makeOrder.addFormulaOrder(initialize); //加算するまでの流れ
        
        OrderStatus valueSet = new OrderStatus(7,value);
        OrderStatus notVisiualFormula = new OrderStatus(8);
        makeOrder.addVariableFormulaOrder(valueSet, notVisiualFormula); //値を可算

        OrderStatus operatorNotView = new OrderStatus(5);
        makeOrder.addHumanFieldOrder(wait,operatorNotView); //演算子を視覚的に消す
        
        OrderStatus varBoxDown = new OrderStatus(5);
        makeOrder.addVariableOrder(varBoxDown); //値が箱に入る
    }

    public void incrementStart(VariableConstruction var, boolean forEnd){
        makeOrder.setVariable(var.copy());

        OrderStatus varBoxSet;
        if(forEnd)varBoxSet = new OrderStatus(6,170f+MainView.width);
        else varBoxSet = new OrderStatus(6);
        makeOrder.headOrder(2, varBoxSet);

        OrderStatus run = new OrderStatus(1,170f);
        makeOrder.addHumanOrder(run);

        ArrayList formula = new ArrayList();
        formula.add("+");
        formula.add("+");
        makeOrder.setFormulaList(formula,null,0,false);

        OrderStatus pose5 = new OrderStatus(6,5);
        makeOrder.addHumanOrder(pose5);

        OrderStatus wait = new OrderStatus(8);
        makeOrder.addHumanOrder(wait);

        OrderStatus appear = new OrderStatus(1);
        makeOrder.addFormulaOrder(appear);

        makeOrder.addHumanOrder(wait);

        OrderStatus initialize = new OrderStatus(2);
        makeOrder.addFormulaOrder(initialize);

        OrderStatus notVisiualFormula = new OrderStatus(8);
        makeOrder.addFormulaOrder(notVisiualFormula);

    }

    public void incrementEnd(VariableConstruction var, boolean forEnd){
        OrderStatus valueSet = new OrderStatus(7,var.getValue());
        makeOrder.addVariableOrder(valueSet);

        OrderStatus varBoxOut = new OrderStatus(3);
        makeOrder.addVariableOrder(varBoxOut);
    }

    public void decrementStart(VariableConstruction var, boolean forEnd){
        makeOrder.setVariable(var.copy());

        OrderStatus varBoxSet;
        if(forEnd)varBoxSet = new OrderStatus(6,170f+MainView.width);
        else varBoxSet = new OrderStatus(6);
        makeOrder.headOrder(2, varBoxSet);

        OrderStatus run = new OrderStatus(1,170f);
        makeOrder.addHumanOrder(run);

        ArrayList formula = new ArrayList();
        formula.add("-");
        formula.add("-");
        makeOrder.setFormulaList(formula,null,0,false);

        OrderStatus pose5 = new OrderStatus(6,5);
        makeOrder.addHumanOrder(pose5);

        OrderStatus wait = new OrderStatus(8);
        makeOrder.addHumanOrder(wait);

        OrderStatus appear = new OrderStatus(1);
        makeOrder.addFormulaOrder(appear);

        makeOrder.addHumanOrder(wait);

        OrderStatus initialize = new OrderStatus(2);
        makeOrder.addFormulaOrder(initialize);

        OrderStatus notVisiualFormula = new OrderStatus(8);
        makeOrder.addFormulaOrder(notVisiualFormula);

    }
    public void decrementEnd(VariableConstruction var, boolean forEnd){
        OrderStatus valueSet = new OrderStatus(7,var.getValue());
        makeOrder.addVariableOrder(valueSet);

        OrderStatus varBoxOut = new OrderStatus(3);
        makeOrder.addVariableOrder(varBoxOut);
    }
    
    //if系統--------------------------------------------------------------------
    public void ifStart(ArrayList<IfCondition> condition,MethodConstruction main,int depth){
        ArrayList<IfCondition> cond = (ArrayList<IfCondition>)condition.clone();
        makeOrder.setCondition(cond,main,depth); //orderクラス
    }
    
    private ArrayList<String> saveFormula = new ArrayList<>();//直前の式が一緒かどうか
    
    public void setSaveFormula(ArrayList<String> formula){
        this.saveFormula.clear();
        this.saveFormula.addAll(formula);
    }
    
    public void conditionVariableOut(ArrayList<String> formula){
        if(this.animationMake == false)return;
        OrderStatus run = new OrderStatus(1,280f);
        makeOrder.addHumanOrder(run);
        
        OrderStatus pose4 = new OrderStatus(6,4);
        makeOrder.addHumanOrder(pose4);
        
        OrderStatus wait = new OrderStatus(8);
        makeOrder.addHumanOrder(wait);
        
        OrderStatus pose3 = new OrderStatus(6,3);
        OrderStatus formulaVarBoxOut = new OrderStatus(4);
        OrderStatus filedOrder = new OrderStatus(2,formulaVarBoxOut);
        makeOrder.addHumanFieldOrder(pose3, filedOrder);
        
        makeOrder.addHumanOrder(wait);
        
        ArrayList<String> form = (ArrayList<String>)formula.clone();
        OrderStatus setFormula = new OrderStatus(5,form);
        OrderStatus setFormulaOfField = new OrderStatus(2,setFormula);
        makeOrder.addFieldOrder(setFormulaOfField);
        
        this.saveFormula.clear();
        this.saveFormula.addAll(form);
    }
    
    //なやんでつっこんで新しい式に変える
    public void newFormulaSet(ArrayList<String> formula){
        if(this.animationMake == false)return;
        //同じ式だった場合は可視化しない
        if(this.saveFormula.size() == 0){
            this.saveFormula.addAll(formula);
        }else{
            boolean re = true;
            if(formula.size() == this.saveFormula.size()){
                for(int i = 0;i < formula.size();i++){
                    re = formula.get(i).equals(this.saveFormula.get(i));
                }
            }else re = false;
            if(re)return;
            else{
                this.saveFormula.clear();
                this.saveFormula.addAll(formula);
            }
        }
        
        OrderStatus run = new OrderStatus(1,280f);
        makeOrder.addHumanOrder(run);
        
        OrderStatus pose4 = new OrderStatus(6,4);
        makeOrder.addHumanOrder(pose4);
        
        OrderStatus wait = new OrderStatus(8,1);
        makeOrder.addHumanOrder(wait);
        
        OrderStatus pose3 = new OrderStatus(6,3);
        ArrayList<String> form = (ArrayList<String>)formula.clone();
        OrderStatus setFormula = new OrderStatus(5,form);
        OrderStatus setFormulaOfField = new OrderStatus(2,setFormula);
        makeOrder.addHumanFieldOrder(pose3, setFormulaOfField);
        makeOrder.addHumanOrder(wait);
    }
    
    public void nextCondition(){
        if(this.animationMake == false)return;
        OrderStatus runPose = new OrderStatus(6,10);
        OrderStatus nextField = new OrderStatus(1);
        makeOrder.addHumanFieldOrder(runPose, nextField);
    }
    
    public void goIf(boolean elseJudge){
        OrderStatus run;
        if(elseJudge == false)run = new OrderStatus(1,930f);
        else run = new OrderStatus(1,600f);
        makeOrder.addHumanOrder(run);
        
        OrderStatus runPose = new OrderStatus(6,10);
        makeOrder.addHumanOrder(runPose);
        
        OrderStatus wait = new OrderStatus(8);
        makeOrder.addHumanOrder(wait);
        
        OrderStatus skeleton = new OrderStatus(9);
        makeOrder.addHumanOrder(skeleton);
        
        makeOrder.addHumanOrder(wait);
        
        this.doorStart = true;//次回ドアからスタート
    }
    
    public void ifScope(ArrayList<VariableConstruction> varList){
        makeOrder.setDumpVar(varList);
        
        if(varList.size() != 0){
            OrderStatus run = new OrderStatus(1,170f);
            makeOrder.addHumanOrder(run);

            OrderStatus dumpPose = new OrderStatus(6,2);
            OrderStatus dumpVar = new OrderStatus(3);
            makeOrder.addHumanFieldOrder(dumpPose, dumpVar);

            OrderStatus runPose = new OrderStatus(6,10);
            makeOrder.addHumanOrder(runPose);
        }
        OrderStatus run = new OrderStatus(1,850f);
        makeOrder.addHumanOrder(run);
        
        OrderStatus wait = new OrderStatus(8);
        makeOrder.addHumanOrder(wait);
        
        OrderStatus skeleton = new OrderStatus(9);
        makeOrder.addHumanOrder(skeleton);
        
        makeOrder.addHumanOrder(wait);
    }
    
    public void ifEnd(ArrayList<IfCondition> condition,MethodConstruction main,int depth,int trueCount){
        ArrayList<IfCondition> cond = (ArrayList<IfCondition>)condition.clone();
        boolean elseJudge = false;
        ArrayList<String> formula = cond.get(trueCount).getCondition();//trueCount番目にあるifの条件式
        if(formula.size() == 0){//elseの場合
            elseJudge = true;
        }
        
        makeOrder.setCondition(cond,main,depth);
        
        OrderStatus moveFiled = new OrderStatus(4,trueCount); //truecountによりどこのifかを判断
        makeOrder.headOrder(3, moveFiled);
        OrderStatus moveHuman;
        
        
        OrderStatus startAction = new OrderStatus(10);
        if(elseJudge == false){
            ArrayList<String> form = new ArrayList();
            form.add("true");
            OrderStatus setFormula = new OrderStatus(5,form);
            OrderStatus setFormulaOfField = new OrderStatus(2,setFormula);
            makeOrder.addHumanFieldOrder(startAction, setFormulaOfField);
            moveHuman = new OrderStatus(11,930f);//trueとfalseの扉の位置が少し違う(falseは真ん中、trueは若干右寄り)
        }else{
            makeOrder.addHumanOrder(startAction);
            moveHuman = new OrderStatus(11,MainView.width/2);//(falseの位置?)
        }
        makeOrder.headOrder(1,moveHuman);
    }
    
    //for系統--------------------------------------------------------------------
    public void forStart(ForConstruction forC){
        makeOrder.setForConstruction(forC);
        
        OrderStatus runPose = new OrderStatus(6,10);
        makeOrder.addHumanOrder(runPose);
        
        OrderStatus run = new OrderStatus(1,MainView.width/2-70);
        makeOrder.addHumanOrder(run);
        
        OrderStatus jump = new OrderStatus(3,220f);
        makeOrder.addHumanOrder(jump);
        
        OrderStatus banzaiPose = new OrderStatus(6,5);
        makeOrder.addHumanOrder(banzaiPose);
        
        OrderStatus wait = new OrderStatus(8);
        makeOrder.addHumanOrder(wait);
        
        OrderStatus down = new OrderStatus(12,1);
        makeOrder.addHumanOrder(down);
        
        this.forLoop++;
    }
    
    public void forVariableNextJudgeSetting(){ //人が降りてくるときの土管表示
        OrderStatus startAction = new OrderStatus(2,1);
        makeOrder.headOrder(1,startAction);
    }
    
    public void forVariableNextJudge(IfCondition condition,MethodConstruction main,int depth,ForConstruction forC){
        makeOrder.setForConstruction(forC);
        makeOrder.setCondition(condition,main,depth);
        
        makeOrder.changeField(8);
        
        OrderStatus runPose = new OrderStatus(6,10); //フィールドが動いて人は動いていない
        OrderStatus nextField = new OrderStatus(1);
        makeOrder.addHumanFieldOrder(runPose, nextField);//右に画面が遷移する
    }
    
    public void forScope(ArrayList<VariableConstruction> varList,ForConstruction forC){
        makeOrder.setForConstruction(forC);
        makeOrder.setDumpVar(varList);
        
        if(varList.size() != 0){
            OrderStatus run = new OrderStatus(1,170f);
            makeOrder.addHumanOrder(run);

            OrderStatus dumpPose = new OrderStatus(6,2);
            OrderStatus dumpVar = new OrderStatus(3);
            makeOrder.addHumanFieldOrder(dumpPose, dumpVar);

            OrderStatus runPose = new OrderStatus(6,10);
            makeOrder.addHumanOrder(runPose);
        }
        
        OrderStatus runPose = new OrderStatus(6,10);
        OrderStatus nextField = new OrderStatus(1); //右に移動
        OrderStatus varBoxEntry = new OrderStatus(10); //画面が右に移動(同時には箱も)
        makeOrder.addAllOrder(runPose,varBoxEntry, nextField,null);
    }
    
    public void forReturn(){
        OrderStatus runPose = new OrderStatus(6,10);
        makeOrder.addHumanOrder(runPose);
        
        OrderStatus run = new OrderStatus(1,MainView.width-150);
        makeOrder.addHumanOrder(run);
        
        OrderStatus in = new OrderStatus(13,1);
        makeOrder.addHumanOrder(in);
    }
    
    public void forReJudege(IfCondition condition,MethodConstruction main,int depth,ForConstruction forC){
        makeOrder.setForConstruction(forC);
        makeOrder.setCondition(condition,main,depth);
        
        OrderStatus moveFiled = new OrderStatus(4,1);
        makeOrder.headOrder(3, moveFiled);
        OrderStatus moveHuman = new OrderStatus(11,75f,MainView.height);
        makeOrder.headOrder(1,moveHuman);
        
        OrderStatus out = new OrderStatus(13,2);
        makeOrder.addHumanOrder(out);
        
        OrderStatus jump = new OrderStatus(3,Human.ground);
        makeOrder.addHumanOrder(jump);
    }
    
    public void forEndBack(){
        OrderStatus moveFiled = new OrderStatus(1,-1);
        OrderStatus runPose = new OrderStatus(6,11);
        makeOrder.addHumanFieldOrder(runPose,moveFiled);
    }
    
    public void forLoopVarScope(VariableConstruction loopVar,ForConstruction forC){
        makeOrder.setForConstruction(forC);
        ArrayList<VariableConstruction> varList = new ArrayList<>();
        varList.add(loopVar.copy());
        
        makeOrder.setDumpVar(varList);
        
        OrderStatus run = new OrderStatus(1, 170f);
        makeOrder.addHumanOrder(run);

        makeOrder.changeField(10);
        
        OrderStatus dumpPose = new OrderStatus(6, 2);
        OrderStatus dumpVar = new OrderStatus(3);
        makeOrder.addHumanFieldOrder(dumpPose, dumpVar); //捨てる動作

        OrderStatus runPose = new OrderStatus(6, 11);
        makeOrder.addHumanOrder(runPose);
    }
    
    public void forOut(){
        OrderStatus run = new OrderStatus(1, 75f);
        makeOrder.addHumanOrder(run);
        
        OrderStatus in = new OrderStatus(13,3);
        makeOrder.addHumanOrder(in);
        
        this.forLoop--;
    }
    
    public void forEnd(ForConstruction forC){
        makeOrder.setForConstruction(forC);
        OrderStatus moveHuman = new OrderStatus(11,MainView.width/2,MainView.height);
        makeOrder.headOrder(1,moveHuman);
        
        OrderStatus out = new OrderStatus(13,4);
        makeOrder.addHumanOrder(out);
        
        OrderStatus jump = new OrderStatus(3,Human.ground);
        makeOrder.addHumanOrder(jump);
    }

    //while文
    public void whileStart(WhileConstruction whileC){
        makeOrder.setWhileConstruction(whileC);

        OrderStatus runPose = new OrderStatus(6,10);
        makeOrder.addHumanOrder(runPose);

        OrderStatus run = new OrderStatus(1,MainView.width/2-70);
        makeOrder.addHumanOrder(run);

        OrderStatus jump = new OrderStatus(3,220f);
        makeOrder.addHumanOrder(jump);

        OrderStatus banzaiPose = new OrderStatus(6,5);
        makeOrder.addHumanOrder(banzaiPose);

        OrderStatus wait = new OrderStatus(8);
        makeOrder.addHumanOrder(wait);

        OrderStatus down = new OrderStatus(12,1);
        makeOrder.addHumanOrder(down);

        this.whileLoop++;
    }

    public void whileVariableNextJudgeSetting(){
        OrderStatus startAction = new OrderStatus(2,1);
        makeOrder.headOrder(1,startAction);
    }

    public void whileVariableNextJudge(IfCondition condition,MethodConstruction main,int depth,WhileConstruction whileC){
        makeOrder.setWhileConstruction(whileC);
        makeOrder.setCondition(condition,main,depth);

        makeOrder.changeField(8);

        OrderStatus runPose = new OrderStatus(6,10);
        OrderStatus nextField = new OrderStatus(1);
        makeOrder.addHumanFieldOrder(runPose, nextField);
    }

    public void whileScope(ArrayList<VariableConstruction> varList,WhileConstruction whileC){
        makeOrder.setWhileConstruction(whileC);
        makeOrder.setDumpVar(varList);

        if(varList.size() != 0){
            OrderStatus run = new OrderStatus(1,170f);
            makeOrder.addHumanOrder(run);

            OrderStatus dumpPose = new OrderStatus(6,2);
            OrderStatus dumpVar = new OrderStatus(3);
            makeOrder.addHumanFieldOrder(dumpPose, dumpVar);

            OrderStatus runPose = new OrderStatus(6,10);
            makeOrder.addHumanOrder(runPose);
        }

        OrderStatus runPose = new OrderStatus(6,10);
        OrderStatus nextField = new OrderStatus(1);
        OrderStatus varBoxEntry = new OrderStatus(10);
        makeOrder.addAllOrder(runPose,varBoxEntry, nextField,null);
    }

    public void whileReturn(){
        OrderStatus runPose = new OrderStatus(6,10);
        makeOrder.addHumanOrder(runPose);

        OrderStatus run = new OrderStatus(1,MainView.width-150);
        makeOrder.addHumanOrder(run);

        OrderStatus in = new OrderStatus(13,1);
        makeOrder.addHumanOrder(in);
    }

    public void whileReJudege(IfCondition condition,MethodConstruction main,int depth,WhileConstruction whileC){
        makeOrder.setWhileConstruction(whileC);
        makeOrder.setCondition(condition,main,depth);

        OrderStatus moveFiled = new OrderStatus(4,1);
        makeOrder.headOrder(3, moveFiled);
        OrderStatus moveHuman = new OrderStatus(11,75f,MainView.height);
        makeOrder.headOrder(1,moveHuman);

        OrderStatus out = new OrderStatus(13,2);
        makeOrder.addHumanOrder(out);

        OrderStatus jump = new OrderStatus(3,Human.ground);
        makeOrder.addHumanOrder(jump);
    }

    public void whileEndBack(){
        OrderStatus moveFiled = new OrderStatus(1,-1);
        OrderStatus runPose = new OrderStatus(6,11);
        makeOrder.addHumanFieldOrder(runPose,moveFiled);
    }

    public void whileLoopVarScope(VariableConstruction loopVar,WhileConstruction whileC){
        makeOrder.setWhileConstruction(whileC);
        ArrayList<VariableConstruction> varList = new ArrayList<>();
        varList.add(loopVar.copy());

        makeOrder.setDumpVar(varList);

        OrderStatus run = new OrderStatus(1, 170f);
        makeOrder.addHumanOrder(run);

        makeOrder.changeField(10);

        OrderStatus dumpPose = new OrderStatus(6, 2);
        OrderStatus dumpVar = new OrderStatus(3);
        makeOrder.addHumanFieldOrder(dumpPose, dumpVar);

        OrderStatus runPose = new OrderStatus(6, 11);
        makeOrder.addHumanOrder(runPose);
    }

    public void whileOut(){
        OrderStatus run = new OrderStatus(1, 75f);
        makeOrder.addHumanOrder(run);

        OrderStatus in = new OrderStatus(13,3);
        makeOrder.addHumanOrder(in);

        this.forLoop--;
    }

    public void whileEnd(WhileConstruction whileC){
        makeOrder.setWhileConstruction(whileC);
        OrderStatus moveHuman = new OrderStatus(11,MainView.width/2,MainView.height);
        makeOrder.headOrder(1,moveHuman);

        OrderStatus out = new OrderStatus(13,4);
        makeOrder.addHumanOrder(out);

        OrderStatus jump = new OrderStatus(3,Human.ground);
        makeOrder.addHumanOrder(jump);
    }
    
    //標準出力の可視化-------------------------------------------------------------------
    public void printStart(ArrayList<String> formula,MethodConstruction main,int depth){
        makeOrder.addSource("System.out.println(");
        makeOrder.addSource(formula);
        makeOrder.addSource(")");
        
        float placeX = makeOrder.setFormulaList(formula,main,depth,true);//placeXは先頭の変数の座標が格納される
        
        OrderStatus formulaPlace = new OrderStatus(9,150f);
        makeOrder.headOrder(4, formulaPlace);
        
        if(placeX > 0){
            OrderStatus run2 = new OrderStatus(1,placeX);
            makeOrder.addHumanOrder(run2);
        
            OrderStatus punch = new OrderStatus(4);
            OrderStatus varBoxOut = new OrderStatus(4);
            makeOrder.addHumanFormulaOrder(punch, varBoxOut);
        }
    }
    
    public void printEnd(){
        OrderStatus banzai = new OrderStatus(6,5);
        OrderStatus upOut = new OrderStatus(10);
        makeOrder.addHumanFormulaOrder(banzai, upOut);
    }

    public void sample(){
        OrderStatus run = new OrderStatus(1,650f);
        makeOrder.addHumanOrder(run);
        OrderStatus wait = new OrderStatus(8);
        makeOrder.addHumanOrder(wait);
    }

    public void sample2(){
        makeOrder.changeField(6);
        OrderStatus pose = new OrderStatus(6,1);
        makeOrder.addHumanOrder(pose);

        OrderStatus wait = new OrderStatus(8);
        makeOrder.addHumanOrder(wait);
    }
}
