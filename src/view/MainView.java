package view;

import static pictogramming.Main.animaOrder;
import java.util.ArrayList;
import ordermake.Order;
import processing.core.*;

public class MainView extends PApplet{
    public static final float width = 1200;
    public static final float height = 400;
    
    ArrayList<Order> order;
    private int orderMax;
    private int animaCount = 0;
    
    private boolean loop = true;
    public void settings(){
        size((int)this.width,(int)this.height);
    }
    
    public void setup(){
        surface.setTitle("ピクトグラミング");
        PImage img = loadImage("gutspose.png"); //左上のアイコン
        surface.setIcon(img);
        
        frameRate(45);
        colorMode(RGB,1.0f);
        imageMode(CENTER);
        this.order = animaOrder.getOrder();
        this.orderMax = this.order.size();

    }
    
    public void draw(){
        if(animaCount>=this.order.size())System.exit(0); //勝手にウィンドウが閉じる(全ての可視化が終了次第)
            
        Order nowOrder = this.order.get(animaCount); //1行分の可視化を取得
        if(nowOrder.getPlay() == false && nowOrder.getEnd() == false)nowOrder.start(this);
        //実行していないかつ終了していないならスタート
        nowOrder.setOrder();
        if(nowOrder.getEnd()) animaCount++; //終了したら終了(0、1巻等をプラス)
        else nowOrder.draw();
    }
    
    public void finishDraw(){
        
    }
    
    public void mousePressed(){
        System.out.println(mouseX+" , "+mouseY);
    }
    
    public void keyPressed(){
        //if(h.getMove() != 0)return;
        if(keyCode == DOWN || key == 'd'){
            if(animaCount + 1 == this.orderMax)return;
            animaCount++;
            this.order.get(animaCount).reset();
            this.order.get(animaCount-1).reset();
        }else if(keyCode == LEFT|| key == 's'){
            this.order.get(animaCount).reset();
        }else if(keyCode == UP || key == 'a'){
            if(animaCount -1 == -1)return;
            animaCount--;
            this.order.get(animaCount).reset();
            this.order.get(animaCount+1).reset();
        }
        if(keyCode == ENTER){
            if(this.loop){
                noLoop();
                this.loop = false;
                fill(0f);
                textSize(30);
                text("stop",width - 100,100);
            }else{
                loop();
                this.loop = true;
            }
        }
    }
}
