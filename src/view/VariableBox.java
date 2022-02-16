package view;

import construction.VariableConstruction;
import java.util.ArrayList;
import ordermake.OrderStatus;
import processing.core.PApplet;

public abstract class VariableBox extends Processing{
    protected String name;
    protected String value;
    
    protected float boxX;
    protected float boxY;
    protected float valueX;
    protected float valueY;
    protected float goalValueY;
    protected static float size = 70;
    protected float alpha = 1f;//透明度
    
    //以下アニメーション系
    protected boolean drop = false; //箱をアッパーして落として変数のみを残す
    protected float dropSpead = -8f;
    protected boolean declaration = false;
    protected boolean seeValue = false; //あらかじめ箱に入っている値を見えるようにする
    protected boolean boxOut = false;
    protected boolean moveValue = false;
    protected boolean dump = false;
    
    protected boolean move = false;
    protected float moveTrans;
    protected float goalX;
    
    protected int depth;
    
    //式用
    public VariableBox(PApplet p,VariableConstruction var,float x,float y,int depth){
        super(p);
        this.name = var.getName();
        if(var.getValue() != null)this.value = var.getValue();
        
        this.boxX = this.valueX = x;
        this.boxY = this.valueY = y;
        
        this.seeValue = true;
        this.depth = depth;
    }
    
    //宣言用
    protected VariableBox(PApplet p,VariableConstruction var,boolean seeValue,int depth){
        this(p,var,p.width + 100,270,depth);
        this.seeValue = true;//←この変数いらないかも！
    }
    
    public void drawBox(){
        drop();
        move();
        declaration();
        boxOut();
        valueMove();
        draw();
    }
    
    protected abstract void draw();
    
    public void dropStart(){
        if(getMove() != 0)return;
        this.drop = true;
    }
    
    protected void drop(){
        if(this.drop == false)return;
        
        this.boxY += this.dropSpead;
        if(this.dump){
            this.valueY += this.dropSpead;
            this.alpha -= 0.05;
        }
        
        if(this.boxY > p.height+100){
            this.boxY = p.height+100;
            this.drop = false;
            this.dump = false;
        }else this.dropSpead += 1f;
    }
    
    public void declarationStart(){
        if(getMove() != 0)return;
        this.declaration = true;
    }
    
    public void declaration(){
        if(this.declaration == false)return;
        
        float spead = 30;
        this.boxX -= spead;
        this.valueX -= spead;
        if(this.boxX < 250){
            this.boxX = this.valueX= 250;
            this.declaration = false;
        }
    }
    
    public void boxOutStart(){
        if(getMove() != 0)return;
        this.boxOut = true;
    }
    
    public void boxOut(){
        if(this.boxOut == false)return;
        float spead = 10;
        this.boxY -= spead;
        this.valueY -= spead;
        if(this.boxY < -size){
            this.boxOut = false;
        }
    }
    
    public void valueUpStart(){
        if(getMove() != 0)return;
        this.moveValue = true;
        this.goalValueY = p.height/5;
    }
    
    public void valueDownStart(){
        if(getMove() != 0)return;
        this.moveValue = true;
        this.goalValueY = p.height*0.45f;
    }
    
    public void valueMove(){
        if(this.moveValue == false)return;
        
        float spead = 7;
        if(this.valueY < this.goalValueY){
            this.valueY+=spead;
        }else{
            this.valueY-=spead;
        }
        
        if(Math.abs(this.valueY - this.goalValueY) < spead){
            this.valueY = this.goalValueY;
            this.moveValue = false;
        }
    }
    
    public void dumpStart(){
        if(getMove() != 0)return;
        this.dump = true;
        this.drop = true;
    }
    
    public void moveStart(){
        this.move = true;
        this.goalX = p.width;
    }
    
    public void move(){
        if(this.move == false)return;
        float spead = 20;
        this.boxX -=spead;
        this.valueX -=spead;
        this.goalX-=spead;
        if(this.goalX <= 0){
            this.move = false;
        }
    }
    
    public static float getSize(){
        return size;
    }
    
    public int getMove(){//動いているか
        if(this.drop)return 1;
        if(this.declaration)return 2;
        if(this.boxOut)return 3;
        if(this.moveValue)return 4;
        if(this.dump)return 5;
        if(this.move)return 6;
        return 0;
    }
    
    public void setMove(OrderStatus order){
        int orderType = order.getOrderType();
        ArrayList argument = order.getArgument();
        if(orderType == 1)dropStart();
        else if(orderType == 2)declarationStart();
        else if(orderType == 3)boxOutStart();
        else if(orderType == 4)valueUpStart();
        else if(orderType == 5)valueDownStart();
        else if(orderType == 6){
            if(argument.size() == 0)set(170f,(float)(p.height*0.45));
            else set((float)argument.get(0),(float)(p.height*0.45));
        }else if(orderType == 7)setValue((String) argument.get(0));
        else if(orderType == 8)seeVariableSwitch();
        else if(orderType == 9)dumpStart();
        else if(orderType == 10)moveStart();
    }
    
    public void setValue(String value){
        this.value = value;
    }
    
    public void set(float x,float y){
        this.boxX = this.valueX = x;
        this.boxY = this.valueY = y;
    }
    
    public void set(float x){
        this.boxX = this.valueX = x;
    }
    
    public void seeVariableSwitch(){
        this.seeValue = !this.seeValue;
    }
}
