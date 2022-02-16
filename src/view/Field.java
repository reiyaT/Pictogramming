package view;

import construction.ForConstruction;
import construction.IfCondition;
import construction.VariableConstruction;
import java.util.ArrayList;

import construction.WhileConstruction;
import ordermake.OrderStatus;
import processing.core.PApplet;

public class Field extends Processing{
    private int fieldType;
    public static final float groundY = MainView.height/8;//地面の高さを示す
    private boolean door;//fieldにドアをつけるかどうか
    private boolean forLoop; //天井をつけるか
    private int depth; //for文やifがどれだけ深いか、深さにより色が変わる
    
    //台
    public static final float standX = 100f;
    public static final float standY = 30f;
    
    //ドア
    public final float doorW = 100;
    public final float doorH = 150;
    
    //ifやforで使う変数
    private ArrayList<Formula> condition = new ArrayList<>();
    private double conditionNumber = 0;//elseなら＋0.5
    private int conditionCount = 0;
    private float moveTrans = 0;
    private float goalX;
    private boolean move = false;
    private VariableBox loopVar;//ループ変数用
    
    //演算子表示
    private boolean equal = false;//=を表示する
    private String operator;
    private int operatorY = 180;
    private boolean upOperator = false;
    
    //プログラム表示に使う変数
    private String source = "";
    private ArrayList<String> conditionSource = new ArrayList<>();
    
    //変数を捨てるところで使う変数
    private ArrayList<VariableBox> varBoxList = new ArrayList<>();
    private boolean varDump = false;
    
    public Field(PApplet p,ArrayList<String> source,boolean door,boolean forLoop,int depth,String operator){
        super(p);//アニメーション生成にはPを必ず継承する(Processingクラス)
        for(String str : source){
            this.source += str + " ";
        }
        this.door = door;
        this.forLoop = forLoop;
        this.depth = depth;
        this.operator = operator;
    }
    
    public Field(PApplet p,ArrayList<String> source,ArrayList<ArrayList> condition,ArrayList<VariableConstruction> varList,ForConstruction forC,boolean door,boolean forLoop,int depth,String operator){
        this(p,source,door,forLoop,depth,operator);
        
        if(condition.size() != 0){//条件式があれば、もらう
            for(int i = 0;i < condition.size();i++){
                ArrayList formula = condition.get(i);
                if(formula.size() != 0){
                    this.condition.add(new Formula(p,formula));
                    this.conditionNumber++;
                    this.conditionSource.add(makeSource(formula,i == 0));//i=0は先頭
                }else{
                    this.conditionNumber+=0.5;
                    this.conditionSource.add("else");
                }
            }
        }
        if (varList.size() != 0) {//スコープで捨てる変数があればもらう
            for (int i = 0; i < varList.size(); i++) {
                VariableConstruction var = varList.get(i);
                VariableBox varB;
                if(var.getType().equals("int"))varB = new IntegerBox(p, var, true,var.getDepth());
                else if(var.getType().equals("double"))varB = new DoubleBox(p, var, true,var.getDepth());
                else if(var.getType().equals("boolean"))varB = new BooleanBox(p, var, true,var.getDepth());
                else varB = new StringBox(p, var, true,var.getDepth());

                varB.set(100 * i + 200, (float) (p.height * 0.45));
                this.varBoxList.add(varB);
            }
        }
        if(forC != null){
            this.source = makeSource(forC);
        }
    }
    
    public void drawField(){
        p.rectMode(p.CORNER);
        move();
        upOperator();
        if(fieldType == 0)normalField(false);
        if(fieldType == 1)endField(); //endの旗
        if(fieldType == 2)declarationField(false); //宣言の台
        if(fieldType == 3)ifField();
        if(fieldType == 4)scopeIfField(); //ifの終わり
        if(fieldType == 5)forStartField();
        if(fieldType == 6)normalField(true);
        if(fieldType == 7)declarationField(true); //天井ありバージョン
        if(fieldType == 8)forVariableJudgeField(); //forの条件式を確認する
        if(fieldType == 9)forScopeCalculationField(false); //for終わった後
        if(fieldType == 10)forScopeCalculationField(true);
        drawSource();
        drawDoor();
        varDump();
    }
    
    public void normalField(boolean forLoop){
        p.background(255f);
        p.rectMode(p.CORNER);
        p.noStroke();
        ColorSet.fillColor(p, depth);
        p.rect(0-this.moveTrans,p.height-this.groundY,p.width,this.groundY);
        p.rect(0-this.moveTrans,0,15,p.height);
        if(forLoop == false)p.rect(p.width-15-this.moveTrans,0,15,p.height);
        else p.rect(0,0,p.width,15);
        p.fill(255f);
        if(forLoop == false)p.rect(p.width-150-this.moveTrans,p.height-this.groundY,135,this.groundY);
        
        if(forLoop)forUpPipe(this.p,75-this.moveTrans,depth);
        if(this.forLoop){
            p.rectMode(p.CORNER);
            p.noStroke();
            ColorSet.fillColor(p, depth);
            p.rect(150-this.moveTrans,0,p.width-150,15);
        }
        
        if(this.equal)drawOperator();
    }
    
    public void endField(){
        float stickH = p.height * 3/5;
        float stickW = 15;
        float flagH = stickH/3;
        float flagW = stickH/2;
        
        p.background(255f);
        p.noStroke();
        
        //地面
        p.fill(0f);
        ColorSet.fillColor(p, depth);
        p.rect(0,p.height-this.groundY,p.width,this.groundY);
        p.rect(0,0,15,p.height);
        p.rect(p.width-15,0,15,p.height);
        //旗
        p.rect(p.width/2-stickW/2,p.height - stickH,stickW,stickH);
        p.rect(p.width/2+stickW/2,p.height - stickH,flagW,flagH);
        p.fill(255f);
        p.textAlign(p.CENTER,p.CENTER);
        p.textSize(40);
        p.text("END",p.width/2+stickW/2,p.height - stickH,flagW,flagH);
    }
    
    public void declarationField(boolean forLoop){
        p.background(255f);
        p.noStroke();
        //p.fill(0f);
        ColorSet.fillColor(p, depth);
        p.rect(0,p.height*7/8,p.width,p.height/8);
        p.rect(0,0,15,p.height);
        if(forLoop == false)p.rect(p.width-15,0,15,p.height);
        else p.rect(0,0,p.width,15);
        p.fill(255f);
        if(forLoop == false)p.rect(p.width-150,p.height*7/8,135,p.height/8);
        //宣言の台
        ColorSet.fillColor(p, depth);
        p.rect(120,p.height-this.groundY-standY,standX,standY);
        
        if(forLoop)forUpPipe(this.p,75-this.moveTrans,depth);
        if(this.forLoop){
            p.rectMode(p.CORNER);
            p.noStroke();
            ColorSet.fillColor(p, depth);
            p.rect(150-this.moveTrans,0,p.width-150,15);
        }
    }
    
    public void ifField(){
        p.background(255f);
        p.noStroke();
        ColorSet.fillColor(p, depth);
        p.rectMode(p.CORNER);
        p.rect(0-moveTrans,0,15,p.height);
        float trans = 0;
        for(int i = 0;i < (int)this.conditionNumber;i++){
            Formula formula = this.condition.get(i); //
            
            trans = p.width * i;
            
            p.noStroke();
            ColorSet.fillColor(p, depth);
            p.rect(trans-moveTrans,p.height-this.groundY,p.width,this.groundY);

            float x = 250;
            float yWidth = 75;
            float xWidth = 350;
            float height = (p.height - this.groundY)/2;

            float stickWidth = 50;
            float stickHeight = 250;
            p.rect((float)((x + xWidth*0.75)-(stickWidth/2)) + trans - moveTrans,p.height-stickHeight,stickWidth,stickHeight);

            ColorSet.strokeColor(p, depth);
            p.strokeWeight(5);
            p.fill(255f);
            p.beginShape();
                p.vertex(x+trans-moveTrans,height-yWidth);
                p.vertex(x+trans-moveTrans,height+yWidth);
                p.vertex(x+xWidth+trans-moveTrans,height+yWidth);
                p.vertex(x+xWidth+trans-moveTrans,height+yWidth*1.8f);
                p.vertex(x+xWidth*1.5f+trans-moveTrans,height);
                p.vertex(x+xWidth+trans-moveTrans,height-yWidth*1.8f);
                p.vertex(x+xWidth+trans-moveTrans,height-yWidth);
                p.vertex(x+trans-moveTrans,height-yWidth);
            p.endShape();
            
            formula.setPlace(300+trans-moveTrans);
            formula.drawFormula(); //
            
            p.strokeWeight(3);
            ColorSet.doorColor(p, depth+1);
            p.rect(x+xWidth*1.8f+trans-moveTrans,p.height - this.groundY-this.doorH,this.doorW,this.doorH);
            p.fill(255f);
            p.rect(x+xWidth*1.8f+trans-moveTrans,p.height-this.groundY-this.doorH-50,this.doorW,50);
            p.fill(0f);
            p.textAlign(p.CENTER, p.CENTER);
            p.text("↓true↓", x+xWidth*1.8f+trans-moveTrans,p.height-this.groundY-this.doorH-50,this.doorW,50);
            
            if(this.forLoop){
                p.rectMode(p.CORNER);
                p.noStroke();
                ColorSet.fillColor(p, depth);
                if(i == 0)p.rect(150-this.moveTrans,0,p.width-150,15);
                else p.rect(trans + this.moveTrans,0,p.width,15);
            }
            
            if(i + 1.5 == this.conditionNumber){
                trans += p.width;
                
                p.noStroke();
                ColorSet.fillColor(p, depth);
                p.rect(trans-moveTrans,p.height-this.groundY,p.width,this.groundY);
                
                p.stroke(0f);
                p.strokeWeight(3);
                ColorSet.doorColor(p, depth+1);
                p.rect(x+300+trans-moveTrans,p.height - this.groundY-this.doorH,this.doorW,this.doorH);
                p.fill(255f);
                p.rect(x+300+trans-moveTrans,p.height-this.groundY-this.doorH-50,this.doorW,50);
                p.fill(0f);
                p.textAlign(p.CENTER, p.CENTER);
                p.text("↓else↓", x+300+trans-moveTrans,p.height-this.groundY-this.doorH-50,this.doorW,50);
                if(this.forLoop){
                    p.rectMode(p.CORNER);
                    p.noStroke();
                    ColorSet.fillColor(p, depth);
                    if(i == 1)p.rect(150-this.moveTrans,0,p.width-150,15);
                    else p.rect(trans + this.moveTrans,0,p.width,15);
                }
            }
        }
        
        trans += p.width;
        p.noStroke();
        ColorSet.fillColor(p, depth);
        p.rect(trans-moveTrans,p.height-this.groundY,p.width,this.groundY);
        p.rect(p.width-15 + trans-moveTrans,0,15,p.height);
        p.fill(255f);
        p.rect(p.width-150 + trans-moveTrans,p.height-this.groundY,135,this.groundY);
        
        if(this.forLoop){
            p.rectMode(p.CORNER);
            p.noStroke();
            ColorSet.fillColor(p, depth);
            p.rect(trans - this.moveTrans, 0, p.width, 15);
        }
    }
    
    public void scopeIfField(){
        normalField(false);
        ColorSet.fillColor(p, depth);
        p.rectMode(p.CORNER);
        p.rect(0,p.height-this.groundY,p.width,this.groundY);
        
        float x = 800;
        p.strokeWeight(3);
        p.stroke(0f);
        ColorSet.doorColor(p, depth-1);
        p.rect(x,p.height - this.groundY-this.doorH,this.doorW,this.doorH);
        p.fill(1f);
        p.rect(x,p.height-this.groundY-this.doorH-50,this.doorW,50);
        p.fill(0f);
        p.textAlign(p.CENTER, p.CENTER);
        p.text("↓exit↓", x,p.height-this.groundY-this.doorH-50,this.doorW,50);
        
        float varSize = VariableBox.size;
        for (int i = 0; i < varBoxList.size(); i++) {
            VariableBox varB = varBoxList.get(i);
            varB.drawBox();
        }
    }
    
    public void forStartField(){
        normalField(false);
        
        forDownPipe(this.p,p.width/2,this.depth+1);
    }
    
    public void forVariableJudgeField(){
        normalField(true);
        
        p.noStroke();
        ColorSet.fillColor(p, depth);
        p.rect(0-moveTrans,0,p.width*2,15);
        
        //ここから条件判定のフィールドへ
        Formula formula = this.condition.get(0);

        float trans = p.width;

        p.noStroke();
        ColorSet.fillColor(p, depth);
        p.rectMode(p.CORNER);
        p.rect(trans - moveTrans, p.height - this.groundY, p.width, this.groundY);//床

        float x = 250;
        float yWidth = 75;
        float xWidth = 350;
        float height = (p.height - this.groundY) / 2;

        float stickWidth = 50;
        float stickHeight = 250;
        p.rect((float) ((x + xWidth * 0.75) - (stickWidth / 2)) + trans - moveTrans, p.height - stickHeight, stickWidth, stickHeight);

        ColorSet.strokeColor(p, depth);
        p.strokeWeight(5);
        p.fill(255f);
        p.beginShape();
            p.vertex(x + trans - moveTrans, height - yWidth);
            p.vertex(x + trans - moveTrans, height + yWidth);
            p.vertex(x + xWidth + trans - moveTrans, height + yWidth);
            p.vertex(x + xWidth + trans - moveTrans, height + yWidth * 1.8f);
            p.vertex(x + xWidth * 1.5f + trans - moveTrans, height);
            p.vertex(x + xWidth + trans - moveTrans, height - yWidth * 1.8f);
            p.vertex(x + xWidth + trans - moveTrans, height - yWidth);
            p.vertex(x + trans - moveTrans, height - yWidth);
        p.endShape();

        formula.setPlace(300 + trans - moveTrans);
        formula.drawFormula();
        
        p.rectMode(p.CORNER);
        ColorSet.fillColor(p, depth);
        p.noStroke();
        p.rect(p.width-15 + trans-moveTrans,0,15,p.height);
        
        p.fill(255f);
        p.rect(trans + p.width-150-this.moveTrans,p.height-this.groundY,135,this.groundY);
        
        forDownPipe(this.p,trans + 75 - this.moveTrans,this.depth);
    }
    
    public void forScopeCalculationField(boolean forEnd){
        p.background(255f);
        p.rectMode(p.CORNER);
        p.noStroke();
        ColorSet.fillColor(p, depth);
        p.rect(0-this.moveTrans,p.height-this.groundY,p.width*2,this.groundY);
        p.rect(0-this.moveTrans,0,15,p.height);
        p.rect(p.width*2-15-this.moveTrans,0,15,p.height);
        p.rect(150-this.moveTrans,0,p.width*2-150,15);
        
        float varSize = VariableBox.size;
        for (int i = 0; i < varBoxList.size(); i++) {
            VariableBox varB = varBoxList.get(i);
            varB.drawBox();
        }
        
        if(forEnd){
            ColorSet.fillColor(p, depth);
            p.rect(0-this.moveTrans,0,p.width*2,15);
            forUpPipe(this.p,75-this.moveTrans,depth);
        }
        else forUpPipe(this.p,p.width*2-150-this.moveTrans,depth);
        
        if(this.equal)drawOperator();
    }
    
    public static void forDownPipe(PApplet p,float x,int depth){
        ColorSet.fillColor(p, depth);
        ColorSet.strokeColor(p, depth);
        p.strokeWeight(20);
        float pipeX = 75;
        float pipeY = 100;
        p.rectMode(p.CENTER);
        p.rect(x,p.height-groundY,pipeX,pipeY);
        float pipeEntranceX = 100;
        float pipeEntranceY = 30;
        p.strokeJoin(p.ROUND);
        p.rect(x,p.height-groundY-50*0.95f,pipeEntranceX,pipeEntranceY);
        p.strokeJoin(p.MITER);
    }
    
    public static void forUpPipe(PApplet p,float x,int depth){
        ColorSet.fillColor(p, depth);
        ColorSet.strokeColor(p, depth);
        p.strokeWeight(20);
        float pipeX = 75;
        float pipeY = 200;
        p.rectMode(p.CENTER);
        p.rect(x,0,pipeX,pipeY*2);
        float pipeEntranceX = 100;
        float pipeEntranceY = 30;
        p.strokeJoin(p.ROUND);
        p.rect(x,pipeY*0.95f,pipeEntranceX,pipeEntranceY);
        p.strokeJoin(p.MITER);
    }
    
    public void drawDoor(){
        if(this.door == false)return;
        p.strokeWeight(3);
        ColorSet.doorColor(p, depth-1);
        p.rect(17,p.height - this.groundY-this.doorH,this.doorW,this.doorH);
    }
    
    public void moveStart(int v){
        this.move = true;
        this.goalX = p.width * v;
        if(this.fieldType == 3)this.conditionCount++;
    }
    
    public void move(){
        if(this.move == false)return;
        float spead = 20;
        if(this.goalX > 0){
            this.moveTrans+=spead;
            this.goalX-=spead;
        }else{
            this.moveTrans-=spead;
            this.goalX+=spead;
        }
        if(Math.abs(this.goalX) <= spead){
            this.moveTrans += this.goalX;
            this.move = false;
        }
    }
    
    //あらかじめ進める　引数は画面分　１なら１画面分進める
    public void setMoveTrans(int num){
        this.moveTrans = p.width * num;
        if(this.fieldType == 3)this.conditionCount = num;
    }
    
    public void varDumpStart(){
        this.varDump = true;
        
        for(VariableBox varB : this.varBoxList)varB.dumpStart();
    }
    
    public void varDump(){
        if(this.varDump == false)return;
        
        boolean end = true;
        for(VariableBox varB : this.varBoxList){
            if(varB.getMove() != 0)end = false;
        }
        if(end)this.varDump = false;
    }
    
    public void upOperatorStart(){
        this.upOperator = true;
    }
    
    public void upOperator(){
        if(this.upOperator == false)return;
        
        this.operatorY -= 7;
        if(this.operatorY < p.height/5){
            this.operatorY = p.height/5;
            this.upOperator = false;
        }
    }
    
    public void drawOperator(){
        p.textAlign(p.LEFT,p.CENTER);
        p.fill(0f);
        p.textSize(36);
        p.text("=", 270,180);
        if(this.operator.equals("") == false)p.text(this.operator, 240,this.operatorY);
    }
    
    public void setOperator(String operator){
        this.operator = operator;
    }
    
    public void drawSource(){//汚い！直す
        p.textAlign(p.LEFT,p.CENTER);
        p.fill(0f);
        p.textSize(24);
        if(this.fieldType == 3){
            if(this.conditionCount >= this.conditionSource.size())return;
            p.text(this.conditionSource.get(this.conditionCount),300,30);
        }else p.text(this.source,300,30);
    }
    
    //ifの条件式をプログラムに戻す
    private String makeSource(ArrayList source,boolean head){
        String result;
        if(head){
            result = "if( ";
        }else{
            result = "else if( ";
        }
        for(int i = 0;i < source.size();i++){
            Object obj = source.get(i);
            if(obj instanceof String)result += (String)obj + " ";
            else{
                VariableConstruction var = (VariableConstruction) obj;
                result += var.getName() + " ";
            }
        }
        result += ")";
        return result;
    }
    
    private String makeSource(ForConstruction forC){
        String result = "for( ";
        ArrayList<String> strList =(ArrayList<String>) forC.getLoopVariable().clone();
        for(String str : strList){
            result += str+" ";
        }
        strList =(ArrayList<String>) forC.getCondition().clone();
        for(String str : strList){
            result += str+" ";
        }
        result += "; ";
        strList =(ArrayList<String>) forC.getFormula().clone();
        for(String str : strList){
            if(str.equals(";"))continue;
            result += str+" ";
        }
        result+=")";
        System.out.println(result);
        return result;
    }
    /*
    private String makeSource(WhileConstruction whileC){
        String result = "while( ";
        ArrayList<String> strList =(ArrayList<String>) whileC.getLoopVariable().clone();
        strList =(ArrayList<String>) whileC.getCondition().clone();
        for(String str : strList){
            result += str+" ";
        }
        result+=")";
        System.out.println(result);
        return result;
    }
    */
    public void setFormulaMove(OrderStatus formulaOrder){
        Formula formula = this.condition.get(this.conditionCount);
        formula.setMove(formulaOrder);
    }
    
    public int getFieldType(){
        return this.fieldType;
    }
    
    public void setFieldType(int type){
        this.fieldType = type;
    }
    
    public void setMove(OrderStatus order){
        int orderType = order.getOrderType();
        ArrayList argu = order.getArgument();
        if(orderType == 1){
            if(argu.size() == 0)moveStart(1);
            else moveStart((int)argu.get(0));
        }
        if(orderType == 2)setFormulaMove((OrderStatus)argu.get(0));
        if(orderType == 3)varDumpStart();
        if(orderType == 4)setMoveTrans((int)argu.get(0));
        if(orderType == 5)this.equal = !this.equal;
        if(orderType == 6)upOperatorStart();
    }
    
    public int getMove(){
        if(this.move)return 1;
        if(this.condition.size() != 0 && this.conditionCount < this.condition.size()){
            if(this.condition.get(this.conditionCount).getMove() != 0)return 2;
        }
        if(this.varDump)return 3;
        return 0;
    }
}
