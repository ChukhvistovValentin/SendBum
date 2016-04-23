package com.megatron.sendbum;

//import java.lang.Class;
//import java.lang.Enum;
//import java.lang.Object;
//import java.lang.String;
//import java.lang.System;
//
//public final class Country extends java.lang.Enum<Country> {
//    public static final Country Abkhazia;
//    public static final Country Belarus;
//    private static final Country ENUM$VALUES[];
//    public static final Country Kazakhstan;
//    public static final Country Moldova;
//    public static final Country Russia;
//    public static final Country Ukraine;
//
//    static {
//        Russia = new Country("Russia", 0);
//        Ukraine = new Country("Ukraine", 1);
//        Belarus = new Country("Belarus", 2);
//        Moldova = new Country("Moldova", 3);
//        Abkhazia = new Country("Abkhazia", 4);
//        Kazakhstan = new Country("Kazakhstan", 5);
//        Country[] arrcountry = new Country[]{Russia, Ukraine, Belarus, Moldova, Abkhazia, Kazakhstan};
//        ENUM$VALUES = arrcountry;
//    }
//
//    private Country(String string, int n) {
//        super(string, n);
//    }
//
//    public static Country valueOf(String string) {
//        return (Country) Enum.valueOf((Class) Country.class, (String) string);
//    }
//
//    public static Country[] values() {
//        Country[] arrcountry = ENUM$VALUES;
//        int n = arrcountry.length;
//        Country[] arrcountry2 = new Country[n];
//        System.arraycopy((Object) arrcountry, (int) 0, (Object) arrcountry2, (int) 0, (int) n);
//        return arrcountry2;
//    }
//
//
//}
//*******************************************************************************
public enum Country {
    Abkhazia,
    Belarus,
    Kazakhstan,
    Moldova,
    Russia,
    Ukraine;
}


//*******************************************************************************
//public class Country {
//    public static final Country Abkhazia;
//    public static final Country Belarus;
//    private static final Country ENUM$VALUES[];
//    public static final Country Kazakhstan;
//    public static final Country Moldova;
//    public static final Country Russia;
//    public static final Country Ukraine;
//
////    Russia("Russia", 0),
////    Ukraine("Ukraine", 1),
////    Belarus("Belarus", 2),
////    Moldova("Moldova", 3),
////    Abkhazia("Abkhazia", 4),
////    Kazakhstan("Kazakhstan", 5);
////    private enum Countrys {
////        Russia, Ukraine, Belarus, Moldova,
////        Abkhazia, Kazakhstan
////    }
////    private final static Country[] ENUM$VALUES;
//
//    private final String country;
//    private final int cod;
//
//    private Country(String country, int cod) {
////        super(country, cod);
//        this.country = country;
//        this.cod = cod;
//    }
//
//    public int getCod() {
//        return cod;
//    }
//
////        public static Country valueOf(String s) {
////        return (Country) Enum.valueOf(Country.class, s);
////    }
//
//    public static Country[] values() {
//        Country acountry[] = ENUM$VALUES;
//        int i = acountry.length;
//        Country acountry1[] = new Country[i];
//        System.arraycopy(acountry, 0, acountry1, 0, i);
//        return acountry1;
//    }
//
//    private int getCountryLength(){
//        return ENUM$VALUES.length;
//    }
//
////    public int getCountryCod(Country country){
////        Country acountry[] = ENUM$VALUES;
////        int j = getCountryLength();
////
////        for (int i=0; j; i++){
////            if (country.getCod() == acountry[i].getCod())
////                return acountry[i].getCod();
////            break;
////        }
////        return -1;
////    }
//
//    static {
//        Russia = new Country("Russia", 0){
//            @Override
//            public int getCod() {
//                return super.getCod();
//            }
//        };
//        Ukraine = new Country("Ukraine", 1);
//        Belarus = new Country("Belarus", 2);
//        Moldova = new Country("Moldova", 3);
//        Abkhazia = new Country("Abkhazia", 4);
//        Kazakhstan = new Country("Kazakhstan", 5);
//        ENUM$VALUES = (new Country[]{
//                Russia, Ukraine, Belarus, Moldova, Abkhazia, Kazakhstan
//        });
//    }
////    static {
////        ENUM$VALUES = (new Country[]{
////                Russia, Ukraine, Belarus, Moldova, Abkhazia, Kazakhstan
////        });
////
////    }
//    }
