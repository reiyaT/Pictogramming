package programSetup;

import java.util.ArrayList;

public class GetMainProgram {
	public static void getMainprogram(ArrayList<String> srcList) {
		int srcListIndex = 0;
		//classの{までスキップする
		while(srcListIndex < srcList.size()) {
			String srcWord = srcList.get(srcListIndex);
			srcListIndex++;
			if(srcWord.equals("{"))break;
		}
		
		//mainメソッドであるか判定する
		if(mainCheck(srcListIndex,srcList)) {
			srcListIndex += 9;
		}else {
			pictogramming.Main.errorMessage = "mainメソッドが見つかりませんでした";
			return;
		}
		
		//mainメソッドの中身だけを抽出する
		while(srcListIndex > 0) {
			srcList.remove(0);
			srcListIndex--;
		}
		srcList.remove(srcList.size()-1);
		srcList.remove(srcList.size()-1);
	}
	
	private static boolean mainCheck(int srcListIndex, ArrayList<String> srcList) {
		//public static void main(String args){がそれぞれ順番にあるか判定
		String[] mainMethod = {"public","static","void","main","(","String[]","args",")","{"};
		
		int mainMethodIndex = 0;
		while(srcListIndex < srcList.size() && mainMethodIndex < 9) {
			String srcWord = srcList.get(srcListIndex);
			String mainWord = mainMethod[mainMethodIndex];
			if(srcWord.equals(mainWord) == false)return false;
			
			srcListIndex++;
			mainMethodIndex++;
		}
		
		if(mainMethodIndex == 9)return true;
		else return false;
	}
}
