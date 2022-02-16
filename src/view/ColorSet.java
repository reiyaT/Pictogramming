package view;

import processing.core.PApplet;

public class ColorSet{
    public static void fillColor(PApplet p, int depth){
        depth &= 7;
        if(depth == 0)p.fill(0f);
        else if(depth == 1)p.fill(0.44f,0.18f,0.62f);
        else if(depth == 2)p.fill(0f,0.69f,0.94f);
        else if(depth == 3)p.fill(0.57f,0.81f,0.31f);
        else if(depth == 4)p.fill(1f,1f,0);
        else if(depth == 5)p.fill(1f,0.75f,0f);
        else if(depth == 6)p.fill(1f,0f,0f);
    }
    
    public static void fillColor(PApplet p, int depth,float alpha){
        depth &= 7;
        if(depth == 0)p.fill(0f,alpha);
        else if(depth == 1)p.fill(0.44f,0.18f,0.62f,alpha);
        else if(depth == 2)p.fill(0f,0.69f,0.94f,alpha);
        else if(depth == 3)p.fill(0.57f,0.81f,0.31f,alpha);
        else if(depth == 4)p.fill(1f,1f,0,alpha);
        else if(depth == 5)p.fill(1f,0.75f,0f,alpha);
        else if(depth == 6)p.fill(1f,0f,0f,alpha);
    }
    
    public static void strokeColor(PApplet p,int depth){
        depth &= 7;
        if(depth == 0)p.stroke(0f);
        else if(depth == 1)p.stroke(0.44f,0.18f,0.62f);
        else if(depth == 2)p.stroke(0f,0.69f,0.94f);
        else if(depth == 3)p.stroke(0.57f,0.81f,0.31f);
        else if(depth == 4)p.stroke(1f,1f,0);
        else if(depth == 5)p.stroke(1f,0.75f,0f);
        else if(depth == 6)p.stroke(1f,0f,0f);
    }
    
    public static void strokeColor(PApplet p,int depth,float alpha){
        depth &= 7;
        if(depth == 0)p.stroke(0f,alpha);
        else if(depth == 1)p.stroke(0.44f,0.18f,0.62f,alpha);
        else if(depth == 2)p.stroke(0f,0.69f,0.94f,alpha);
        else if(depth == 3)p.stroke(0.57f,0.81f,0.31f,alpha);
        else if(depth == 4)p.stroke(1f,1f,0,alpha);
        else if(depth == 5)p.stroke(1f,0.75f,0f,alpha);
        else if(depth == 6)p.stroke(1f,0f,0f,alpha);
    }
    
    public static void doorColor(PApplet p, int depth){
        depth &= 7;
        if(depth == 0)p.fill(0f,0.8f);
        else if(depth == 1)p.fill(0.44f,0.18f,0.62f,0.8f);
        else if(depth == 2)p.fill(0f,0.69f,0.94f,0.8f);
        else if(depth == 3)p.fill(0.57f,0.81f,0.31f,0.8f);
        else if(depth == 4)p.fill(1f,1f,0,0.8f);
        else if(depth == 5)p.fill(1f,0.75f,0f,0.8f);
        else if(depth == 6)p.fill(1f,0f,0f,0.8f);
    }
    
    public static void variableColor(PApplet p,String type,float alpha){
        if(type.equals("int"))p.fill(0.74f,0.84f,0.93f,alpha);
        else if(type.equals("double"))p.fill(0.78f,0.88f,0.70f,alpha);
        else if(type.equals("boolean"))p.fill(1f,0.90f,0.6f,alpha);
        else if(type.equals("String"))p.fill(0.97f,0.80f,0.68f,alpha);
    }
}
