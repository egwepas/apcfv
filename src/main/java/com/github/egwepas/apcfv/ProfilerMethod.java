/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.egwepas.apcfv;

/**
 *
 * @author egwepas
 */
public class ProfilerMethod {

    public ProfilerMethods parent;
    public String name;
    public int count;

    public ProfilerMethod(ProfilerMethods parent, String name, int count) {
        this.parent = parent;
        this.name = name;
        this.count = count;
    }

    public float percentageOfTotal() {
        return (count * 100) / (float) parent.total;
    }
}
