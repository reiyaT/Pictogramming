public class IfStatement {
    public static void main(String[] args){
        int num = 5;
        if (num > 0){
            if(num < 3){
                System.out.println("0 < num < 3");
            } else{
                System.out.println("num > 3");
            }
        } else {
            System.out.println("num < 0");
        }
    }
}
