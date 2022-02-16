package view;
import java.util.ArrayList;
import ordermake.OrderStatus;
import processing.core.*;

public class Human extends Processing{
    //人間の基本情報
    PImage[] photo;
    private float x;
    private float y;
    
    //１個１個の動作
    private boolean run = false;//今走っているかが格納される
    private boolean drop = false;//今落ちているかが格納される
    private boolean jump = false;
    private boolean punch = false;
    private boolean speak = false;
    private boolean skeleton = false;
    private boolean door = false;
    private boolean otherPose = false;//1受け取る。2捨てる、3つっこむ、4なやむ、5ばんざい、6ひらめき
    private boolean downMove = false;
    private boolean upMove = false;
    
    private float goalX = 0;
    private float goalY = 0;
    private float jumpY = 0;
    private float jumpX = 0;
    public static final float ground = MainView.height * 2/3 + 40;//地面の位置
    private int dashCount;//走る動作の表示切替
    private final int dashRate = 20;
    
    private String speakContent;//喋る内容
    private int speakWait;//喋る待つ時間
    
    private int otherPoseType = 0;//0なし、1受け取る、２捨てる、３つっこむ、４なやむ
    private float alpha = 1f;//透明化
    
    private int waitTime = 0;
    
    //台に立っているか
    private boolean stand = false;
    
    private int pipeType=0;
    
    private int depth;
    
    public Human(PApplet p,float x,float y,int depth){
        super(p);
        
        this.x = x;
        this.y = y;
        this.depth = depth;
        
        this.photo = new PImage[13];
        this.photo[0] = p.loadImage("run1.png");
        this.photo[1] = p.loadImage("run2.png");
        this.photo[2] = p.loadImage("gutspose.png");
        this.photo[3] = p.loadImage("drop.png");
        this.photo[4] = p.loadImage("jump.png");
        this.photo[5] = p.loadImage("punch.png");
        this.photo[6] = p.loadImage("catch.png");
        this.photo[7] = p.loadImage("dump.png");
        this.photo[8] = p.loadImage("tsukkomi.png");
        this.photo[9] = p.loadImage("worry.png");
        this.photo[10] = p.loadImage("banzai.png");
        this.photo[11] = p.loadImage("inspiration.png");
        this.photo[12] = p.loadImage("winner.png");
    }
    
    public void drawHuman(){
        skeleton();
        door();
        
        int m = getMove();    
        if(this.stand)stand();
        if(m == 1)run();
        else if(m == 2)drop();
        else if(m == 3)jump();
        else if(m == 4)jump();//動作が似ているから使いまわし
        else if(m == 5)speak();
        else if(m == 9)downMove();
        else if(m == 10)upMove();
        else{
            if(this.otherPoseType != 0)otherPose();
            else gutspose();
        }
    }
    
    public void setRun(float x){
        if(getMove() > 0 && getMove() < 6)return;
        this.goalX = x;
        this.run = true;
        this.dashCount = 0;
    }
    
    private void run(){
        int vector = 0; //走る向き右か左か
        int speed = 8;
        if(this.goalX - this.x > 0)vector = 1; //this.xは現在いる場所
        else if(this.goalX - this.x < 0)vector = -1;
        else { //終了の動作
            this.run=false;
            drawHuman();
            return;
        }
        
        this.x += vector*speed;
        if(Math.abs(this.goalX-this.x) < speed)this.x = this.goalX;
        
        p.pushMatrix();
            p.scale(vector,1);//表示をひっくり返す
            if(this.dashCount%this.dashRate < this.dashRate/2)p.image(this.photo[0],x*vector,y);//0~9
            else p.image(this.photo[1],x*vector,y); //10~19
        p.popMatrix();
        this.dashCount++;
    }
    
    public void setDrop(){
        if(getMove() > 0 && getMove() < 6)return;
        this.y = -50;
        this.goalY = this.ground;
        this.drop = true;
    }
    
    public void setDrop(int pipeType){
        setDrop();
        this.pipeType = pipeType;
    }
    
    private void drop(){
        int speed = 10;
        if(Math.abs(this.goalY-this.y)<speed){
            this.y = this.goalY;
            this.drop = false;
            return;
        }else this.y += speed;
        p.image(this.photo[3],x,y);
        
        if(this.pipeType != 0)Field.forUpPipe(p, 75, depth);
    }
    
    public void setJump(float x,float y){//後で設定し直す！！
        if(getMove() > 0 && getMove() < 6)return;
        this.goalX = x;
        this.goalY = y;
        this.jump = true;
    }
    
    public void setJump(float y){
        if(getMove() > 0 && getMove() < 6)return;
        this.goalY = y;
        this.jumpY = -10;
        this.jumpX = 2;
        this.jump = true;
    }
    
    public void setPunch(){
        if(getMove() > 0 && getMove() < 6)return;
        this.goalY = y;
        this.jumpY = -10;
        this.jumpX = 0;
        this.punch = true;
    }
    
    private void jump(){//ジャンプは42フレームで１回
        this.y += this.jumpY;
        this.x += this.jumpX;
        
        if(this.jump)p.image(this.photo[4],x,y);
        else if(this.punch)p.image(this.photo[5],x,y);
        
        if(this.y > this.goalY && this.jumpY > 0){
            this.y = this.goalY;
            this.jump = false;
            this.punch = false;
        }else{
            this.jumpY += 0.5;
        }
    }
    
    public void setSpeak(String content){
        if(getMove() > 0 && getMove() < 6)return;
        this.speakContent = content;
        this.speakWait = 30;
        this.speak = true;
    }
    
    public void speak(){
        gutspose();
        if(speakWait > 0){
            speakWait--;
        }else if(speakWait > -30){
            p.fill(0f);
            p.textAlign(p.CENTER,p.CENTER);
            p.textSize(32);
            p.text(this.speakContent, x+75, y-75);
            speakWait--;
        }else this.speak = false;
    }
    
    public void setOtherPose(int num){
        if(getMove() > 0 && getMove() < 6)return;

        this.dashCount = 0;
        this.otherPoseType = num;
        
        if(num == 0)this.otherPose = false;
        else this.otherPose = true;
    }
    
    public void downMoveStart(int pipeType){
         if(getMove() > 0 && getMove() < 6)return;
         this.downMove = true;
         this.goalY = p.height;
         this.pipeType = pipeType;
    }
    
    public void downMove(){
        float spead = 5f;
        this.y += spead;
        if(this.y > this.goalY){
            this.downMove = false;
            return;
        }
        p.image(this.photo[10],x,y);
        
        if(this.pipeType == 1)Field.forDownPipe(this.p,p.width/2,depth+1);
    }
    
    public void upMoveStart(int pipeType){
         if(getMove() > 0 && getMove() < 6)return;
         this.upMove = true;
         this.pipeType = pipeType;
         
         if(pipeType == 1)this.goalY = 0;
         if(pipeType == 2)this.goalY = 240;
         if(pipeType == 3)this.goalY = 0;
         if(pipeType == 4)this.goalY = 240;
    }
    
    public void upMove(){
        float spead = 5f;
        this.y -= spead;
        if(this.y < this.goalY){
            this.upMove = false;
            return;
        }
        p.image(this.photo[10],x,y);
        
        if(this.pipeType == 1)Field.forUpPipe(this.p,p.width-150,depth);
        else if(this.pipeType == 2)Field.forDownPipe(this.p,75,depth);
        else if(this.pipeType == 3)Field.forUpPipe(this.p,75,depth);
        else if(this.pipeType == 4)Field.forDownPipe(this.p,MainView.width/2,depth);
    }
    
    public void otherPose(){
        if(getMove() > 0 && getMove() < 5){
            return;
        }
        
        if(this.otherPoseType > 0 && this.otherPoseType <= 7){//7はポーズの最大値
            p.image(this.photo[this.otherPoseType + 5],x,y);
        }else if(this.otherPoseType == 10){ //足踏み
            p.tint(0f,1*this.alpha);
            if(this.dashCount%this.dashRate < this.dashRate/2)p.image(this.photo[0],x,y);
            else p.image(this.photo[1],x,y);
            this.dashCount++;
            p.tint(0f,1f);
        }else if(this.otherPoseType == 11){ //向きを変える
            p.pushMatrix();
                p.tint(0f,1*this.alpha);
                p.scale(-1,1);
                if(this.dashCount%this.dashRate < this.dashRate/2)p.image(this.photo[0],x*-1,y);
                else p.image(this.photo[1],x*-1,y);
                this.dashCount++;
                p.tint(0f,1f);
            p.popMatrix();
        }
        
        if(this.waitTime > 0)this.waitTime--;
    }
    
    //動きを止める時間を設定(悩むなど)
    public void setWait(){
        double sec = 0.5;
        this.waitTime = (int)(30 * sec);
    }
    
    public void setWait(int sec){
        this.waitTime = (int)(30 * sec);
    }
    
    public void skeletonStart(){
        this.skeleton = true;
        setOtherPose(10);
    }
    
    public void skeleton(){ //薄くする
        if(this.skeleton == false)return;
        this.alpha -= 0.03;
        if(this.alpha < 0){
            this.alpha = 0;
            this.skeleton = false;
        }
    }
    
    public void doorStart(){
        this.door = true;
        this.y = this.ground;
        setOtherPose(10);
        this.alpha = 0f;
        p.tint(255,this.alpha);
    }
    
    public void door(){ //透けてる状態を鮮明にする
        if(this.door == false)return;
        this.alpha += 0.03;
        if(this.alpha > 1){
            this.alpha = 1f;
            this.door = false;
        }
    }

    private void gutspose(){
        p.image(this.photo[2],x,y);
        if(this.waitTime > 0)this.waitTime--;
    }
    
    public void setPlace(float x){
        this.x = x;
    }
    
    public void setPlace(float x,float y){
        this.x = x;
        this.y = y;
    }
    
    public int getMove(){
        //何かしらの動作をしていれば、
        if(this.run)return 1;//走っている
        if(this.drop)return 2;
        if(this.jump)return 3;
        if(this.punch)return 4;
        if(this.speak)return 5;
        if(this.waitTime > 0)return 6;
        if(this.skeleton)return 7;
        if(this.door)return 8;
        if(this.downMove)return 9;
        if(this.upMove)return 10;
        return 0;//何もしていない
    }
    
    public void setMove(OrderStatus order){ //OrderStatusクラス
        int orderType = order.getOrderType();
        ArrayList argument = order.getArgument();
//        System.out.println(orderType+" , "+argument.size());
        if(orderType == 1)setRun((float)argument.get(0));//走る
        else if(orderType == 2){
            if(argument.size() == 0)setDrop();
            else setDrop((int)argument.get(0));
        }//落ちる
        else if(orderType == 3)setJump((float)argument.get(0));//ジャンプ
        else if(orderType == 4)setPunch();//パンチ
        else if(orderType == 5)setSpeak((String)argument.get(0));//話す
        else if(orderType == 6)setOtherPose((int)argument.get(0));//見た目を変える  1受け取る。2捨てる、3つっこむ、4なやむ、5ばんざい、6ひらめき、7勝訴
        else if(orderType == 7)setStand();//台の上に立つ
        else if(orderType == 8){
            if(argument.size() == 0)setWait();
            else setWait((int)argument.get(0));
        }
        else if(orderType == 9)skeletonStart();
        else if(orderType == 10)doorStart();
        else if(orderType == 11){
            if(argument.size() == 1)setPlace((float)argument.get(0));
            else setPlace((float)argument.get(0),(float)argument.get(1));
        }
        else if(orderType == 12)downMoveStart((int)argument.get(0));//配置する土管の位置
        else if(orderType == 13)upMoveStart((int)argument.get(0));//配置する土管の位置
    }
    
    //台に立つ-----------------------------------------------------------------------------------------------
    public void setStand(){
        this.stand = !this.stand;
        if(this.stand == false)this.y = p.height*3/4;
    }
    
    public void stand(){
        this.x = 170;
        this.y = p.height*3/4 - Field.standY;
    }
    
    public String toString(){
        return "x:" + this.x+"  y:"+this.y;
    }
}
