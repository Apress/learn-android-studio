package com.apress.gerber.helloworld;



/**
 * Created by Adam Gerber on 4/19/2015.
 */
public class Sandbox {


    private String mShovel;
    private int mChildren;

    private Sandbox(String shovel, int children) {
        mShovel = shovel;
        mChildren = children;
        new Thread(new MyRunnable()).start();


    }

    public static Sandbox createSandbox(String shovel, int children) {
        return new Sandbox(shovel, children);
    }

    public String getShovel() {
        return mShovel;
    }

    public void setShovel(String shovel) {
        mShovel = shovel;
    }

    public Integer getChildren() {
        return new Integer(mChildren);
    }

    public void setChildren(int children) {
        mChildren = children;
    }

    private class MyRunnable implements Runnable {
        @Override
        public void run() {

        }
    }
}


