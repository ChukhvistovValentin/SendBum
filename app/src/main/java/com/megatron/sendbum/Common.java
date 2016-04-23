package com.megatron.sendbum;

import java.util.ArrayList;
import java.util.Iterator;

public class Common {
//    protected static final String CURRENT_OPERATOR = "CURRENT_OPERATOR";
//    public static final String IS_FIRST_RUN = "IS_FIRST_RUN";
//    public static final String LAST_REQUEST_NAME = "LAST_REQUEST_NAME";
//    public static final String LAST_REQUEST_PHONE = "LAST_REQUEST_PHONE";
//    public static final String NOTIFICATION_RECIVED = "NOTIFICATION_RECIVED";
//    public static final String NUMBER_STARTS = "NUMBER_STARTS";
//    public static final String SETTINGS = "SETTINGS";
    private static ArrayList<Operator> _operators;

    public static Operator getOperatorById(int paramInt) {
        Iterator localIterator = getOperators().iterator();
        Operator localOperator;
        do {
            if (!localIterator.hasNext()) {
                return null;
            }
            localOperator = (Operator) localIterator.next();
        } while (localOperator.Id != paramInt);
        return localOperator;
    }

    public static int getOperatorPositionById(int paramInt) {
        int i = 0;
        Iterator localIterator = getOperators().iterator();
        while (localIterator.hasNext()) {
            if (((Operator) localIterator.next()).Id == paramInt)
                return i;
            ++i;
        }
        return -1;
    }

    public static ArrayList<Operator> getOperators() {
        _operators = new ArrayList();
        _operators.add(new Operator(Country.Russia, "МТС Россия", "mts", "*110*8%s#", 1));
        _operators.add( new Operator(Country.Russia, "Билайн Россия", "beeline", "*144*%s#", 3));
        _operators.add( new Operator(Country.Russia, "Мегафон", "megafon", "*144*8%s#", 0));
        _operators.add( new Operator(Country.Russia, "Ростелеком", "utel", "*123*8%s#", 8));
        _operators.add( new Operator(Country.Russia, "Теле2 Россия", "tele2", "*118*8%s#", 4));
        _operators.add( new Operator(Country.Russia, "Мотив", "motiv", "*105*8%s#", 2));
        _operators.add( new Operator(Country.Russia, "Смартс", "smarts", "*134*%s#", 5));
        _operators.add( new Operator(Country.Russia, "НСС", "ncc", "*135*%s#", 6));
        _operators.add( new Operator(Country.Russia, "БВК", "bwc", "*141*%s#", 7));
        _operators.add( new Operator(Country.Russia, "АКОС GSM", "akos", "*123*%s#", 9));
        _operators.add( new Operator(Country.Russia, "Енисей Телеком", "enisei", "*102*50*%s#", 100));
        _operators.add( new Operator(Country.Russia, "Просто для общения", "prosto", "*168*8%s#", 101));
        _operators.add( new Operator(Country.Ukraine, "DJUICE", "djuice", "*130*38%s#", 10));
        _operators.add( new Operator(Country.Ukraine, "МТС Украина", "mts", "*104*%s#", 11));
        _operators.add( new Operator(Country.Ukraine, "Kyivstar", "kyivstar", "*130*38%s#", 12));
        _operators.add( new Operator(Country.Ukraine, "Билайн Украина", "beeline", "*130*38%s#", 13));
        _operators.add( new Operator(Country.Ukraine, "life:) Украина", "life", "*123*3*38%s#", 14));
        _operators.add( new Operator(Country.Moldova, "IDC", "idc", "887#%s", 20));
        _operators.add( new Operator(Country.Moldova, "MoldCell", "moldcell", "*111*7%s#", 21));
        _operators.add( new Operator(Country.Abkhazia, "Aquafon", "aquafon", "*151*7%s#", 30));
        _operators.add( new Operator(Country.Abkhazia, "A-Mobile", "a-mobile", "*107*%s#", 31));
        _operators.add( new Operator(Country.Belarus, "Velcom", "velcom", "*131*%s#", 40));
        _operators.add( new Operator(Country.Belarus, "МТС Беларусь", "mtsby", "*120*%s#", 41));
        _operators.add( new Operator(Country.Belarus, "life:) Беларусь", "lifeby", "*120*2*%s#", 42));
        _operators.add( new Operator(Country.Kazakhstan, "KCell Казахстан", "kcell", "*130*8%s#", 200));
        _operators.add( new Operator(Country.Kazakhstan, "Activ Казахстан", "activ", "*130*8%s#", 201));
        _operators.add( new Operator(Country.Kazakhstan, "BeeLine Казахстан", "beeline kz", "*144*8%s#", 202));
        _operators.add( new Operator(Country.Kazakhstan, "Tele2 Казахстан", "tele2 kz", "*144*8%s#", 203));
        return _operators;
    }

    public static String getValidNumber(String number, int operatorId) {
        if (number == null) {
            return null;
        }
        number = number.replace("+", "").replace("-", "").replace(" ", "").replace("_", "").replace(".", "").replace(")", "").replace("(", "");
        Operator operator = getOperatorById(operatorId);
        if (operator == null) {
            return null;
        }

        switch (operator.Country.ordinal()) { //ordinal()) {
            case 1 /*1*/:
                if (number.length() < 10) {
                    return null;
                }
                number = number.substring(number.length() - 10);
                if (!number.startsWith("9")) {
                    return null;
                }
                break;
            case  2/*2*/: //************
                if (number.length() == 12 && number.startsWith("38")) {
                    number = number.substring(2);
                }
                if (number.length() != 10) {
                    return null;
                }
                break;
            case  3/*3*/: //*****
                if (number.length() == 12) {
                    if (!number.startsWith("375")) {
                        return null;
                    }
                } else if (number.length() == 15) {
                    if (number.startsWith("810375")) {
                        number = number.substring(3);
                    }
                } else if (number.length() != 9) {
                    return null;
                } else {
                    number = "375" + number;
                }
                if (operator.Id == 40 && number.startsWith("375")) {
                    number = number.substring(3);
                    break;
                }
            case 4:
                if (operator.Id == 21 && number.length() >= 7) {
                    number = number.substring(number.length() - 7);
                    break;
                }
            case 5: //**********
                if (operator.Id == 31) {
                    if (number.length() >= 7) {
                        number = number.substring(number.length() - 7);
                        break;
                    }
                } else if (number.length() >= 10) {
                    number = number.substring(number.length() - 10);
                    break;
                } else {
                    return null;
                }
                break;
            case 6:
                if (number.length() < 10) {
                    return null;
                }
                number = number.substring(number.length() - 10);
                if (!number.startsWith("7")) {
                    return null;
                }
                break;
        }
        try {
            Long.parseLong(number);
            return number;
        } catch (Exception e) {
            return null;
        }
    }
}
