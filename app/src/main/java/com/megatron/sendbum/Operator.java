package com.megatron.sendbum;

public class Operator {
    public String CodeTemplate;
    public Country Country;
    public int Id;
    public String Name;
    public String TechnicalName;

    public Operator(Country paramCountry, String paramString1, String paramString2, String paramString3, int paramInt) {
        this.Name = paramString1;
        this.TechnicalName = paramString2;
        this.CodeTemplate = paramString3;
        this.Id = paramInt;
        this.Country = paramCountry;
    }

    public String toString() {
        return this.Name;
    }
}
