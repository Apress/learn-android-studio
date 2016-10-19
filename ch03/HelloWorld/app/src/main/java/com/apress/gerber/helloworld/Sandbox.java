package com.apress.gerber.helloworld;

import android.graphics.drawable.shapes.RectShape;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Adam Gerber on 4/19/2015.
 */
public class Sandbox extends RectShape {


    public static final String HELLO = "Hello Sandbox";
    //refactor initialization to constructor
    private List<String> mGreetings = new ArrayList<>();

    //&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&
    // CONSTRUCTORS
    //&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&
    public Sandbox() {
    }

    public List<String> getGreetings() {
        return mGreetings;
    }

    public void setGreetings(List<String> greetings) {
        mGreetings = greetings;
    }

    @Override
    public boolean hasAlpha() {
        return true;
    }

    @Override
    public String toString() {

        StringBuilder stringBuilder = new StringBuilder();
        for (String greeting : mGreetings) {
            stringBuilder.append(greeting + " ");
        }
        return stringBuilder.toString().trim();


    }

    public boolean add(String object) {
        System.out.println("Sandbox.add");
        return mGreetings.add(object);
    }


}
