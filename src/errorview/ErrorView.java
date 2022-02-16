package errorview;

import pictogramming.Main;
import processing.core.*;

public class ErrorView extends PApplet{
	public static String errorMessage;
		
	@Override
	public void settings() {
		size(600,600);
	}
	
	@Override
	public void setup() {
		surface.setTitle("ピクトグラミング：エラー");
		PImage img = loadImage("gutspose.png");
        surface.setIcon(img);

		noLoop();
		colorMode(RGB,1.0f);
        imageMode(CENTER);
		colorMode(RGB,255f);
		textFont(createFont("MS ゴシック",20,true));
		
		this.errorMessage = Main.errorMessage;
	}
	
	@Override
	public void draw() {
		background(255f);
		
		PImage errorImg = loadImage("worry.png");
		image(errorImg,width/2f,height/3f);
		
		fill(0f);
		textSize(36);
		textAlign(CENTER,CENTER);
		text("可視化できませんでした",width/2f,height/8f);
		textSize(20);
		textAlign(LEFT,TOP);
		text(this.errorMessage,25,height/2f,width-50,height/2f);
	}
	
	@Override
	public void keyPressed() {
	}
}
