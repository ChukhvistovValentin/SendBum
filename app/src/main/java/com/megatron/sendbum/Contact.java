package com.megatron.sendbum;

public class Contact {
    public long Datetime;
    public String Name;
    public String Phone;

    public Contact(String paramString1, String paramString2, long paramLong) {
        this.Name = paramString1;
        this.Phone = paramString2;
        this.Datetime = paramLong;
    }
}

