package com.smarter.tools;

import android.util.Log;

import java.util.Random;

/**
 * Created by kasal on 28/03/2018.
 */

public class RandomGenerator {

    private Random rand = new Random();
    private double fridge = 0.f;
    private double conditioner = 0.f;
    private double washmach = 0.f;


    public RandomGenerator()
    {

    }

    public RandomGenerator(int continuous, int hour, int counter, double temperature)
    {
        generateHour(continuous, hour, counter, temperature);
    }

    public double getFridge()
    {
        return fridge;
    }
    public double getConditioner()
    {
        return conditioner;
    }
    public double getWashmach()
    {
        return washmach;
    }
    private void generateFridge()
    {
        this.fridge = randomGenerate(0.3,0.8);
    }

    private void generateWashmach(int continuous, int hour)
    {
        if(hour >= 6 && hour <= 21  && continuous < 3)
            this.washmach = randomGenerate(0.4,1.3);
        else
            this.washmach = 0.f;
    }

    private void generateConditioner(int counter, int hour, double temperature)
    {
        if(hour >= 9  && counter < 10 && temperature > 20.f)
            this.conditioner = randomGenerate(1.f,5.f);
        else
            this.conditioner = 0.f;
    }

    public void generateHour(int continuous, int hour, int counter, double temperature)
    {
        Log.i("RandomGenerator","Generating Electricity usage");

        generateFridge();
        generateWashmach(continuous, hour);
        generateConditioner(counter, hour, temperature);

        Log.i("RandomGenerator","fridge: " + this.fridge);
        Log.i("RandomGenerator","conditioner: " + this.conditioner);
        Log.i("RandomGenerator","washmach: " + this.washmach);

    }

    public double randomGenerate(double max, double min)
    {
        return rand.nextDouble() * (max - min) + min;
    }

}
