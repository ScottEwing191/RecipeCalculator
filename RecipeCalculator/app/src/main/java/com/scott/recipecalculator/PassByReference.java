package com.scott.recipecalculator;

public class PassByReference<T> {
    public T Obj = null;
    public PassByReference(T value){
        this.Obj = value;
    }
}
