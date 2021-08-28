package com.scott.recipecalculator;

public enum UnitTypes {
    //cups,
    g,
    oz,
    ml,
    cups,
    tsp,
    number;

    public static UnitTypes ConvertStringToEnum(String unitString){
        switch (unitString) {
            case "(g)":
                return UnitTypes.g;
            case "(oz)":
                return UnitTypes.oz;
            case "(ml)":
                return UnitTypes.ml;
            case "(cups)":
                return UnitTypes.cups;
            case "(tsp)":
                return UnitTypes.tsp;
            case "(#)":
                return UnitTypes.number;
        }
        return null;
    }
}
