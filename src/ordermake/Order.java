package ordermake;

import construction.*;

import java.util.ArrayList;
import processing.core.PApplet;
import view.*;

public class Order { //アニメーションを描画
    //各オブジェクトの行動を順番に記録
    private ArrayList<OrderStatus> humanOrder = new ArrayList<>();
    private ArrayList<OrderStatus> fieldOrder = new ArrayList<>();
    private ArrayList<OrderStatus> variableOrder = new ArrayList<>(); //変数の箱
    private ArrayList<OrderStatus> formulaOrder = new ArrayList<>();
    //フィールドのタイプを選択
    private ArrayList<Integer> fieldType = new ArrayList<>();
    private int setFieldType; //値が変化しなければfieldは変化しない
    //VariableOrderで最初にsetする変数を格納
    private VariableConstruction var;
    //formulaOrderで最初にsetする式を格納
    private ArrayList formulaList = new ArrayList<>();
    //条件を格納
    private ArrayList<ArrayList> condition = new ArrayList<>();
    //スコープで捨てる変数を格納
    private ArrayList<VariableConstruction> dumpVar = new ArrayList<>();
    
    //それぞれのインスタンスを格納
    private Human human;
    private Field field;
    private VariableBox varBox;
    private Formula formula;
    //Orderの現在実行する場所を格納
    private int animaCount;
    private boolean play = false;
    private boolean end = false;
    
    private boolean door = false;//最初にドアをつけるか
    private boolean forLoop = false;
    
    private int depth;
    
    private String operator = "";//+=などの+が入る
    
    //可視化中のプログラムを格納
    private ArrayList<String> source = new ArrayList<>();
    private ForConstruction forConstruction;//forの（）内を取得するために入れておく
    private WhileConstruction whileConstruction;//whileの（）内を取得するために入れておく
    public Order(int setFieldType,boolean door,boolean forLoop,int depth){
        this.door = door;
        this.forLoop = forLoop;
        this.setFieldType = setFieldType;
        
        OrderStatus startAction;
        if(door) startAction = new OrderStatus(10); //ドアから出てくる
        else startAction = new OrderStatus(2);
        addHumanOrder(startAction);
        
        this.depth = depth;
    }
    //アニメプレイ-----------------------------------------------------------------------------------------------------------------
    public void draw(){
        this.field.drawField();
        this.human.drawHuman();//背景で挟むことで、違和感がないように
        if(this.varBox != null)this.varBox.drawBox(); //変数表示
        if(this.formula != null)this.formula.drawFormula(); //式表示
    }
    
    public void setOrder(){
        if(this.play == false)return;
        if(move())return;
        
        if(animaCount>=this.humanOrder.size()){//全てのアニメが終了
            this.play = false;
            this.end = true;
        }else{//でなければ、次のアニメの指示を出す
            //フィールドタイプの指定
            this.field.setFieldType(this.fieldType.get(animaCount));
            
            //ピクト君の動きを指示
            if(this.humanOrder.get(animaCount) != null)human.setMove(this.humanOrder.get(animaCount));
            //変数の動きを指示
            if(this.varBox != null){
                if(this.variableOrder.get(animaCount) != null)varBox.setMove(this.variableOrder.get(animaCount));
            }
            //フィールドの動きを指示
            if(this.fieldOrder.get(animaCount) != null)field.setMove(this.fieldOrder.get(animaCount));
            
            if(this.formula != null){
                //式の動きを指示
                if(this.formulaOrder.get(animaCount) != null)this.formula.setMove(this.formulaOrder.get(animaCount));
            }

            animaCount++;
        }
    }
    
    public boolean move(){//何かしらが動いている
        if(human.getMove() != 0)return true;
        if(this.varBox != null){
            if(varBox.getMove() != 0)return true;
        }
        if(this.field.getMove() != 0)return true;
        if(this.formula != null){
            if(this.formula.getMove() != 0)return true;
        }
        return false;
    }
    
    public void start(PApplet p){
        animaCount = 0;
        this.human = new Human(p,70,-50,this.depth);
        
        if(this.condition.size() != 0 || this.dumpVar.size() != 0 || this.forConstruction != null){
            this.field = new Field(p,this.source,this.condition,this.dumpVar,this.forConstruction,this.door,forLoop,this.depth,this.operator);
        }else{
            this.field = new Field(p,this.source,this.door,forLoop,this.depth,this.operator);
        }


        if(this.var != null){
            if(this.var.getType().equals("int"))this.varBox = new IntegerBox(p,var,false,var.getDepth());
            else if(this.var.getType().equals("double"))this.varBox = new DoubleBox(p,var,false,var.getDepth());
            else if(this.var.getType().equals("boolean"))this.varBox = new BooleanBox(p,var,false,var.getDepth());
            else this.varBox = new StringBox(p,var,false,var.getDepth());
        }
        
        if(this.formulaList.size() != 0){
            this.formula = new Formula(p,this.formulaList);
        }
        
        this.play = true;
        setOrder();
    }
    
    public boolean getPlay(){
        return this.play;
    }
    
    public boolean getEnd(){
        return this.end;
    }
    
    public void reset(){
        this.play = false;
        this.end = false;
        animaCount = 0;
    }
    
    //アニメセット-----------------------------------------------------------------------------------------------------------------
    public void headOrder(int type, OrderStatus order){//1がhuman 2がvariableBox 3がfiled 4がformula
        if(type == 1)this.humanOrder.set(0, order);
        else if(type == 2)this.variableOrder.set(0,order);
        else if(type == 3)this.fieldOrder.set(0,order);
        else if(type == 4)this.formulaOrder.set(0,order);
    }
    
    public void addHumanOrder(OrderStatus humanOrder){
        addAllOrder(humanOrder,null,null,null);
    }
    
    public void addVariableOrder(OrderStatus variableOrder){
        addAllOrder(null,variableOrder,null,null);
    }
    
    public void addFormulaOrder(OrderStatus formulaOrder){
        addAllOrder(null,null,null,formulaOrder);
    }
    
    public void addFieldOrder(OrderStatus fieldOrder){
        addAllOrder(null,null,fieldOrder,null);
    }
    
    public void addHumanVariableOrder(OrderStatus humanOrder,OrderStatus variableOrder){
        addAllOrder(humanOrder,variableOrder,null,null);
    }
    
    public void addHumanFormulaOrder(OrderStatus humanOrder,OrderStatus formulaOrder){
        addAllOrder(humanOrder,null,null,formulaOrder);
    }
    
    public void addHumanFieldOrder(OrderStatus humanOrder,OrderStatus fieldOrder){
        addAllOrder(humanOrder,null,fieldOrder,null);
    }
    
    public void addVariableFormulaOrder(OrderStatus variableOrder,OrderStatus formulaOrder){
        addAllOrder(null, variableOrder,null, formulaOrder);
    }
    
    public void addFieldFormulaOrder(OrderStatus fieldOrde,OrderStatus formulaOrder){
        addAllOrder(null,null,fieldOrde,formulaOrder);
    }
    
    public void addAllOrder(OrderStatus humanOrder,OrderStatus variableOrder, OrderStatus fieldOrder,OrderStatus formulaOrder){
        this.humanOrder.add(humanOrder);
        this.variableOrder.add(variableOrder);
        this.fieldOrder.add(fieldOrder);
        this.formulaOrder.add(formulaOrder);
        
        this.fieldType.add(this.setFieldType);
    }
    
    //フィールドのタイプを変える場合
    public void changeField(int setFieldType){
        this.setFieldType = setFieldType;
    }
    
    public void setVariable(VariableConstruction var){
        this.var = var;
    }
    
    //式を格納　ついでに変数はVaribaleCosntractionクラスに変換
    public float setFormulaList(ArrayList<String> form,MethodConstruction main,int depth,boolean print){
        //変数はVariableConstrucitonに変換
        for(int i = 0; i < form.size();i++){
            String str = form.get(i);
            VariableConstruction var = null;
            if(main != null) var = main.getVariable(str, depth); //変数なのか数字なのか
            if(var == null)this.formulaList.add(str);
            else this.formulaList.add(var.copy());
        }

        return Formula.getPlaceVariableX(this.formulaList,print); //ヒューマンがパンチする位置を調整
    }
    
    public void addSource(String source){
        this.source.add(source);
    }
    public void addSource(ArrayList<String> source){
        this.source.addAll(source);
    }
    
    public void setCondition(ArrayList<IfCondition> condition,MethodConstruction main,int depth){
        for(IfCondition ifcon : condition){
            ArrayList<String> cond = ifcon.getCondition();
            ArrayList result = new ArrayList<>();
            
            for(int i = 0; i < cond.size();i++){
                String str = cond.get(i);
                VariableConstruction var = main.getVariable(str, depth);
                if(var == null)result.add(str);
                else result.add(var.copy());
            }
            
            this.condition.add(result);
        }
    }
    
    public void setCondition(IfCondition condition,MethodConstruction main,int depth){
        ArrayList<String> cond = new ArrayList<>();
        cond.addAll(condition.getCondition());
        ArrayList result = new ArrayList<>();
        
        for(int i = 0; i < cond.size();i++){
            String str = cond.get(i);
            VariableConstruction var = main.getVariable(str, depth);
            if(var == null)result.add(str);
            else result.add(var.copy());
        }
            
        this.condition.add(result);
    }
    
    public void setForConstruction (ForConstruction forC){

        this.forConstruction = forC;
    }
    
    public void setDumpVar(ArrayList<VariableConstruction> varList){

        this.dumpVar.addAll(varList);
    }
    
    public void setOperator(String operator){

        this.operator = operator;
    }

    public void setWhileConstruction(WhileConstruction whileC) {

        this.whileConstruction = whileC;
    }
}
