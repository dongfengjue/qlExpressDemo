package com.chenbing.model;

public class Model {

    public Double getA1() {
        return a1;
    }

    public void setA1(Double a1) {
        this.a1 = a1;
    }

    public Double getA2() {
        return a2;
    }

    public void setA2(Double a2) {
        this.a2 = a2;
    }

    public Double getA3() {
        return a3;
    }

    public void setA3(Double a3) {
        this.a3 = a3;
    }

    private Double a1;

    private Double a2;

    @RefreshData("a1+a2")
    private Double a3;

    @Override
    public String toString() {
        return "Model{" +
                "a1=" + a1 +
                ", a2=" + a2 +
                ", a3=" + a3 +
                '}';
    }
}
