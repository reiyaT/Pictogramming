package view;

import construction.VariableConstruction;
import java.util.ArrayList;
import ordermake.OrderStatus;
import processing.core.PApplet;

public class Formula extends Processing{
    private ArrayList formula = new ArrayList<>();
    private float x;
    private float y;
    
    private float goalX;
    private float goalY;
    
    private static final float charSize = 15f;//1文字のサイズ
    
    //アニメーションフラグ
    private boolean moveLeft = false;
    private boolean moveUp = false;
    private boolean varOut  = false;
    private boolean visualization= true;
    private boolean emphasis = false; //式の色を変化 (4+6)*10などのとき
    
    private float spead = 15f;
    private int emphasisIndex = -1;//1個所だけ強調した時に入れる
    private int varOutWaitTime;
    public Formula(PApplet p, ArrayList formula) {
        super(p);
        this.x = p.width+50;
        this.y = p.height*0.40f;
        
        for(int i = 0;i < formula.size();i++){
            Object obj = formula.get(i);
//            System.out.println(formula.get(i).getClass().getSimpleName());
            if(obj instanceof String)this.formula.add((String)obj);
            else{
                VariableConstruction var = (VariableConstruction)obj;
                String type = var.getType();
                VariableBox varBox;
                if(type.equals("int"))varBox = new IntegerBox(p,var,true,var.getDepth());
                else if(type.equals("double"))varBox = new DoubleBox(p,var,false,var.getDepth());
                else if(type.equals("boolean"))varBox = new BooleanBox(p,var,false,var.getDepth());
                else varBox = new StringBox(p,var,false,var.getDepth());
                varBox.set(this.x, this.y+20);
                this.formula.add(varBox);
            }
        }
    }
    
    public void drawFormula(){
        moveLeft();
        moveUp();
        varOut();
        varOutWait();
        if(this.visualization){
            if(this.emphasis)drawEmphasis();
            else draw();
        }
    }
    
    private void draw(){
        float x = this.x;
        float variableSize = VariableBox.getSize();
        
        p.textAlign(p.CENTER,p.CENTER);
        p.fill(0f);
        for(int i = 0;i < formula.size();i++){
            Object obj = formula.get(i);
            if(obj instanceof String){
                String str = (String)obj;
                float sizeWidth = str.length() * charSize + 20; //可視化する式の幅を大きめにする
                p.textSize(30);
                p.text(str, x, this.y,sizeWidth,50);
                x += sizeWidth;
            }else{
                x += variableSize*0.6;
                VariableBox varBox = (VariableBox)obj;
                varBox.set(x);
                varBox.drawBox(); //
                x += variableSize*0.6;
            }
        }
    }
    
    //一番()が深い所だけ赤くする
    //または指定したindexの部分（emphasisIndex）を赤くする
    private void drawEmphasis(){
        if(this.emphasisIndex != -1){
            drawEmphasis(this.emphasisIndex);
            return;
        }
        
        int brackets = 0;
        for(int i = 0;i < formula.size();i++){
            String str = (String)formula.get(i);
            if(str.equals("("))brackets++;
        }
        
        float x = this.x;
        
        p.textAlign(p.CENTER,p.CENTER);
        p.textSize(20);
        p.fill(0f);
        
        int emp = 0;
        for(int i = 0;i < formula.size();i++){
            String str = (String)formula.get(i);
            if(str.equals("("))emp++;
            
            if(emp == brackets)p.fill(255f,0f,0f);
            else p.fill(0f);
            float sizeWidth = str.length() * charSize + 20;
            p.text(str, x, this.y,sizeWidth,50);
            x += sizeWidth;
            
            if(str.equals(")"))emp--;
        }
    }
    
    private void drawEmphasis(int index){
        float x = this.x;
        
        p.textAlign(p.CENTER,p.CENTER);
        p.textSize(20);
        p.fill(0f);
        
        for(int i = 0;i < formula.size();i++){
            String str = (String)formula.get(i);
            
            if(i == index)p.fill(255f,0f,0f);
            else p.fill(0f);
            float sizeWidth = str.length() * charSize + 20;
            p.text(str, x, this.y,sizeWidth,50);
            x += sizeWidth;
        }
    }
    
    public void appearStart(){
        if(getMove() != 0)return;
        this.moveLeft = true;
        this.goalX = 300;
        this.spead = 25;
    }
    
    public void initializeStart(){
        if(getMove() != 0)return;
        this.moveLeft = true;
        this.goalX = 170;
        this.spead = 5;
    }
    
    private void moveLeft(){
        if(this.moveLeft== false)return;
  
        this.x -= spead;
        if(this.x < this.goalX){
            this.x = this.goalX;
            this.moveLeft = false;
        }
    }
    
    public void upStart(){
        if(getMove() != 0)return;
        this.moveUp = true;
        this.goalY = p.height/5-25;
        this.spead = 7;
        
        for (int i = 0; i < this.formula.size(); i++) {
            Object obj = this.formula.get(i);
            if (obj instanceof VariableBox) {
                VariableBox varBox = (VariableBox) obj;
                varBox.valueUpStart(); //変数の箱も上にあげる
            }
        }
    }
    
    public void upOutStart(){
        if(getMove() != 0)return;
        this.moveUp = true;
        this.goalY = -50;
        this.spead = 10;
        
        for (int i = 0; i < this.formula.size(); i++) {
            Object obj = this.formula.get(i);
            if (obj instanceof VariableBox) {
                VariableBox varBox = (VariableBox) obj;
                varBox.valueUpStart();
            }
        }
    }
    
    private void moveUp(){
        if(this.moveUp == false)return;
        
        this.y -= spead;
        
        boolean formulaEnd = false;
        boolean varBoxEnd = true;
        
        if(this.y <= this.goalY){
            this.y = this.goalY;
            formulaEnd = true;
        }
        for (int i = 0; i < this.formula.size(); i++) {
            Object obj = this.formula.get(i);
            if (obj instanceof VariableBox) {
                VariableBox varBox = (VariableBox) obj;
                int move = varBox.getMove();
                if(move != 0)varBoxEnd = false;
            }
        }
        if(formulaEnd && varBoxEnd)this.moveUp = false;
    }
    
    public void varOutStart(){
        if(getMove() != 0)return;
        //少しだけ待つ
        this.varOutWaitTime = 5;
        this.varOut = true;
    }
    
    public void varOutWait(){
        if(this.varOut == false)return;
        if(this.varOutWaitTime == -1)return;
        
        if (this.varOutWaitTime == 0) {
            for (int i = 0; i < this.formula.size(); i++) {
                Object obj = this.formula.get(i);
                if (obj instanceof VariableBox) {
                    VariableBox varBox = (VariableBox) obj;
                    varBox.dropStart();
                }
            }
        }
        this.varOutWaitTime--;
    }
    
    public void varOut(){
        if(this.varOut == false)return;
        if(this.varOutWaitTime > -1)return;
        
        boolean end = true;//全てのアニメが終わったか
        for(int i = 0;i < this.formula.size();i++){
            Object obj = this.formula.get(i);
            if(obj instanceof VariableBox){
                VariableBox varBox = (VariableBox) obj;
                int move = varBox.getMove();
                if(move != 0)end = false;
            }
        }
        if(end)this.varOut = false;
    }
    
    public void setFormula(ArrayList<String> formula){
        //変化前の式で赤くなっている部分の先頭を抽出
        //すると赤くなった場所が変わったとき、どこが変わったのかわかる
        if(this.emphasis){
            this.emphasisIndex = 0;
            for(int i = 0;i < this.formula.size();i++){
                String str =(String) this.formula.get(i);
                if(str.equals("("))this.emphasisIndex = i;
            }
        }
        this.formula.clear(); //(4+6)*10などの式で最初のかっこ部分の計算を行って消す
        for(int i = 0;i < formula.size();i++){
            String str = formula.get(i);
            this.formula.add(str);
        }
    }
    
    public void notVisualization(){
        this.visualization = false;
    }
    
    public void setPlace(float x){
        this.x = x;
    }
    
    public int getMove(){
        if(this.moveLeft)return 1;
        if(this.moveUp)return 2;
        if(this.varOut || this.varOutWaitTime > 0)return 3;
        return 0;
    }
    
    public void setMove(OrderStatus order){
        int orderType = order.getOrderType();
        ArrayList argument = order.getArgument();
        if(orderType == 1)appearStart();//登場
        else if(orderType == 2)initializeStart();//代入
        else if(orderType == 3)upStart();//上に移動
        else if(orderType == 4)varOutStart();
        else if(orderType == 5)setFormula(argument);//新しい式に変更
        else if(orderType == 8)notVisualization(); 
        else if(orderType == 9)setPlace((float)argument.get(0));
        else if(orderType == 10)upOutStart();
    }
    
    public static float getPlaceVariableX(ArrayList formula,boolean print){//先頭にある変数の位置を返す 40*(num+num)など
        float x = 300;
        if(print)x = 150;
        float variableSize = VariableBox.getSize();
        
        for(int i = 0;i < formula.size();i++){
            Object obj = formula.get(i);
            if(obj instanceof String){
                String str = (String)obj;
                float sizeWidth = str.length() * charSize + 20;
                x += sizeWidth;
            }else{
                x += variableSize*0.6;
                return x;
            }
        }
        return -1f;
    }
}
