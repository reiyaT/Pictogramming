package ordermake;

import java.util.ArrayList;

public class OrderStatus {
    private int orderType;
    private ArrayList argument = new ArrayList<>();
    
    public OrderStatus(int order,ArrayList argu){
        this.orderType = order;
        setArgument(argu);
    }
    
    public OrderStatus(int order,Object argument){
        this.orderType = order;
        ArrayList argu = new ArrayList<>();
        argu.add(argument);
        setArgument(argu);
    }
    
    public OrderStatus(int order,Object argument1,Object argument2){
        this.orderType = order;
        ArrayList argu = new ArrayList<>();
        argu.add(argument1);
        argu.add(argument2);
        setArgument(argu);
    }
    
    public OrderStatus(int order){
        this.orderType = order;
    }
    
    private void setArgument(ArrayList argu){
        this.argument = argu;
    }
    
    public int getOrderType(){
        return this.orderType;
    }
    
    public ArrayList getArgument(){
        return this.argument;
    }
}
