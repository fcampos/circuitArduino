/*
 * Copyright 2018
 * Kollins Lima (kollins.lima@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kollins.project.sofia.atmega328p.iomodule_atmega328p.output;

import com.kollins.project.sofia.UCModule;
import com.kollins.project.sofia.ucinterfaces.IOModule;

import java.util.Arrays;

/**
 * Created by kollins on 3/14/18.
 * Modified by Francisco Campos
 */

public class OutputPin_ATmega328P {

    private String pin;
    private int[] pinState = new int[UCModule.getPinArray().length];
    private int pinPositionSpinner;
    private boolean meter;

    public OutputPin_ATmega328P(String pin, int pinPositionSpinner){
        this.pin = pin;
        this.pinPositionSpinner = pinPositionSpinner;
        meter = true;
        resetPinState();
    }

    public OutputPin_ATmega328P(String pin, int pinPositionSpinner, int[] pinState){
        this.pin = pin;
        this.pinPositionSpinner = pinPositionSpinner;
        this.pinState = pinState;
        meter = true;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public int getPinState(int index) {
        return pinState[index];
    }

    public void setPinState(int pinState, int index) {
        this.pinState[index] = pinState;
    }

    public void setPinPositionSpinner(int position){
        pinPositionSpinner = position;
    }

    public int getPinPositionSpinner(){
        return pinPositionSpinner;
    }

    public void resetPinState(){
        Arrays.fill(pinState, IOModule.TRI_STATE);
    }

    public int[] getStates() {
        return pinState;
    }

    public void showMeter(){
        meter = true;
    }

    public boolean getMeter(){
        return meter;
    }
}
