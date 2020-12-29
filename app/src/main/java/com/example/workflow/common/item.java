package com.example.workflow.common;

public class item {
    private String orderNo;
    private String itemName;
    private String itemNo;
    public item()
    {

    }
    public  item(String itemNo,String orderNo,String itemName)
    {
        this.itemNo=itemNo;
        this.orderNo=orderNo;
        this.itemName=itemName;
    }

    public String getItemName() {
        return itemName;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public String getItemNo() {
        return itemNo;
    }
}
