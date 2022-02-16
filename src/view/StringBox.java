package view;

import construction.VariableConstruction;
import processing.core.PApplet;
import static view.VariableBox.size;

public class StringBox extends VariableBox{
    public StringBox(PApplet p,VariableConstruction var,boolean seeValue,int depth){
        super(p,var,seeValue,depth);
    }
    
    @Override
    protected void draw(){
        float x = this.boxX - size / 2;
        float y = this.boxY - size / 2;
        p.rectMode(p.CORNER);
        ColorSet.strokeColor(p,this.depth, alpha);
        p.strokeWeight(3);
        ColorSet.variableColor(p, "String", alpha);
        p.rect(x,y,size,size);
        p.quad(x+size*0.2f, y-size*0.2f, x+size*1.2f, y-size*0.2f, x+size, y, x, y);
        p.quad(x+size, y, x+size*1.2f, y-size*0.2f,x+size*1.2f, y+size*0.8f, x+size, y+size);
        
        p.fill(0f,this.alpha);
        p.textSize(24);
        p.textAlign(p.CENTER,p.CENTER);
        p.text(this.name,x,y+size*0.9f,size*1.1f,40);
        if(this.value != null && this.seeValue){
            String str = this.value;
            if(str.length() > 3)str = str.substring(0, 3);
            p.textSize(30);
            x = this.valueX - size / 2;
            y = this.valueY - size/2;
            p.text(str,x,y,size,size);
        }
    }
}
