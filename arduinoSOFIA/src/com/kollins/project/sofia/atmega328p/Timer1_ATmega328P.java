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
// Modified by Francisco Campos
package com.kollins.project.sofia.atmega328p;

//import android.util.Log;

import com.kollins.project.sofia.UCModule;
import com.kollins.project.sofia.UCModule_View;
import com.kollins.project.sofia.atmega328p.iomodule_atmega328p.IOModule_ATmega328P;
import com.kollins.project.sofia.ucinterfaces.DataMemory;
import com.kollins.project.sofia.ucinterfaces.IOModule;
import com.kollins.project.sofia.ucinterfaces.Timer1Module;

public class Timer1_ATmega328P implements Timer1Module {

    private static final String TIMER1_TAG = "Timer1";

    public static final char MAX = 0xFFFF;
    public static final char MAX_8B = 0x00FF;
    public static final char MAX_9B = 0x01FF;
    public static final char MAX_10B = 0x03FF;
    public static final char BOTTOM = 0x0000;

    public static boolean timerOutputControl_OC1A;
    public static boolean timerOutputControl_OC1B;

    private static DataMemory_ATmega328P dataMemory;
//    private static Handler uCHandler;
    private static IOModule_ATmega328P ioModule;
    private UCModule uCModule;

    private static boolean oldExternalT1, newExternalT1;
    private static boolean oldICP1, newICP1;

    private static int stateOC1A, stateOC1B;
    private static boolean nextOverflow, nextClear;
    private static boolean phaseCorrect_UPCount,        //Tell about the next count
                           phaseCorrect_UPCount_old;    //Tell about how I get to the actual value
    private static char doubleBufferOCR1A, doubleBufferOCR1B;

    private byte modeSelector;
    public static boolean enableICRWrite;

    private static short clockCount;
    public static int increment=1;

    private static boolean match_A, match_B;
    private static char progress, icr1, ocr1a, ocr1b;

    public Timer1_ATmega328P(DataMemory dataMemory, IOModule ioModule) {// , IOModule ioModule) {
        this.dataMemory = (DataMemory_ATmega328P) dataMemory;
//        this.uCHandler = uCHandler;
        this.uCModule = uCModule;
        this.ioModule = (IOModule_ATmega328P) ioModule;

        oldExternalT1 = dataMemory.readBit(DataMemory_ATmega328P.PIND_ADDR, 5);
        oldICP1 = dataMemory.readBit(DataMemory_ATmega328P.PINB_ADDR, 0);

        timerOutputControl_OC1A = false;
        timerOutputControl_OC1B = false;
        phaseCorrect_UPCount = true;
        phaseCorrect_UPCount_old = false;

        stateOC1A = IOModule.TRI_STATE;
        stateOC1B = IOModule.TRI_STATE;

        nextOverflow = false;
        nextClear = false;

        enableICRWrite = true;

        doubleBufferOCR1A = 0;
        doubleBufferOCR1B = 0;

        clockCount = 0;
    }
    @Override
    public void setIncrement(int inc) {increment = inc;};
    @Override
    public void run() {

        //Power Reduction Register
        if (dataMemory.readBit(DataMemory_ATmega328P.PRR_ADDR, 3)){
            return;
        }

        if (ClockSource.values()[0x07 & dataMemory.readByte(DataMemory_ATmega328P.TCCR1B_ADDR)].work()) {

            if (dataMemory.readBit(DataMemory_ATmega328P.GTCCR_ADDR, 0)) {
                return;   //Synchronization Mode
            }

            modeSelector = (byte) (0x03 & dataMemory.readByte(DataMemory_ATmega328P.TCCR1A_ADDR));
            modeSelector = (byte) (((0x18 & dataMemory.readByte(DataMemory_ATmega328P.TCCR1B_ADDR)) >> 1) | modeSelector);

            switch (modeSelector) {
                case 0x00:
                    TimerMode.NORMAL_OPERATION.count();
                    break;
                case 0x01:
                    TimerMode.PWM_PHASE_CORRECT_8B.count();
                    break;
                case 0x02:
                    TimerMode.PWM_PHASE_CORRECT_9B.count();
                    break;
                case 0x03:
                    TimerMode.PWM_PHASE_CORRECT_10B.count();
                    break;
                case 0x04:
                    TimerMode.CTC_OPERATION_TOP_OCR1A.count();
                    break;
                case 0x05:
                    TimerMode.FAST_PWM_8B.count();
                    break;
                case 0x06:
                    TimerMode.FAST_PWM_9B.count();
                    break;
                case 0x07:
                    TimerMode.FAST_PWM_10B.count();
                    break;
                case 0x08:
                    TimerMode.PWM_PHASE_AND_FREQUENCY_CORRECT_TOP_ICR1.count();
                    break;
                case 0x09:
                    TimerMode.PWM_PHASE_AND_FREQUENCY_CORRECT_TOP_OCRA.count();
                    break;
                case 0x0A:
                    TimerMode.PWM_PHASE_CORRECT_TOP_ICR1.count();
                    break;
                case 0x0B:
                    TimerMode.PWM_PHASE_CORRECT_TOP_OCRA.count();
                    break;
                case 0x0C:
                    TimerMode.CTC_OPERATION_TOP_ICR1.count();
                    break;
                case 0x0E:
                    TimerMode.FAST_PWM_TOP_ICR1.count();
                    break;
                case 0x0F:
                    TimerMode.FAST_PWM_TOP_OCRA.count();
                    break;
                default:
                    break;
            }

            if (!enableICRWrite) {
                //Input Capture Unit
                newICP1 = dataMemory.readBit(DataMemory_ATmega328P.PINB_ADDR, 0);
                if (dataMemory.readBit(DataMemory_ATmega328P.TCCR1B_ADDR, 6)) {
                    //Rising Edge detect
                    if (!oldICP1 && newICP1) {
                        UCModule.interruptionModule.timer1InputCapture();
                        dataMemory.write16bits(DataMemory_ATmega328P.ICR1L_ADDR, DataMemory_ATmega328P.ICR1H_ADDR,
                                (byte) (0x00FF & progress), (byte) (0x00FF & (progress >> 8)));

                    }
                } else {
                    //Falling Edge detect
                    if (oldICP1 && !newICP1) {
                        UCModule.interruptionModule.timer1InputCapture();
                        dataMemory.write16bits(DataMemory_ATmega328P.ICR1L_ADDR, DataMemory_ATmega328P.ICR1H_ADDR,
                                (byte) (0x00FF & progress), (byte) (0x00FF & (progress >> 8)));

                    }
                }
                oldICP1 = newICP1;
            }

        }
    }

    public enum ClockSource {
        NO_CLOCK_SOURCE {
            @Override
            public boolean work() {
//                Log.i(TIMER1_TAG, "No Clock Source");
                return false;
            }
        },
        CLOCK_PRESCALER_1 {
            @Override
            public boolean work() {
//                Log.i(TIMER1_TAG, "Prescaler 1");
                return true;
            }
        },
        CLOCK_PRESCALER_8 {
            @Override
            public boolean work() {
//                Log.i(TIMER1_TAG, "Prescaler 8");
                if ((clockCount+=increment) < 8){//(++clockCount < 8){
                    return false;
                } else {
                    clockCount = 0;
                    return true;
                }
            }
        },
        CLOCK_PRESCALER_64 {
            @Override
            public boolean work() {
//                Log.i(TIMER1_TAG, "Prescaler 64");
                if ((clockCount+=increment) < 64){//(++clockCount < 64){
                    return false;
                } else {
                    clockCount = 0;
                    return true;
                }
            }
        },
        CLOCK_PRESCALER_256 {
            @Override
            public boolean work() {
//                Log.i(TIMER1_TAG, "Prescaler 256");
                if ((clockCount+=increment) < 256){//(++clockCount < 256){
                    return false;
                } else {
                    clockCount = 0;
                    return true;
                }
            }
        },
        CLOCK_PRESCALER_1024 {
            @Override
            public boolean work() {
//                Log.i(TIMER1_TAG, "Prescaler 1024");
                if ((clockCount+=increment) < 1024){//(++clockCount < 1024){
                    return false;
                } else {
                    clockCount = 0;
                    return true;
                }
            }
        },
        EXTERNAL_CLOCK_T1_FALLING_EDGE {
            @Override
            public boolean work() {
                newExternalT1 = dataMemory.readBit(DataMemory_ATmega328P.PIND_ADDR, 5);
                if (oldExternalT1 & !newExternalT1) {
                    oldExternalT1 = newExternalT1;
                    return true;
                } else {
                    oldExternalT1 = newExternalT1;
                    return false;
                }
            }
        },
        EXTERNAL_CLOCK_T1_RISING_EDGE {
            @Override
            public boolean work() {
                newExternalT1 = dataMemory.readBit(DataMemory_ATmega328P.PIND_ADDR, 5);
                if (!oldExternalT1 & newExternalT1) {
                    oldExternalT1 = newExternalT1;
                    return true;
                } else {
                    oldExternalT1 = newExternalT1;
                    return false;
                }
            }
        };

        public abstract boolean work();
    }

    public enum TimerMode {
        NORMAL_OPERATION {
            @Override
            public void count() {
                enableICRWrite = false;
                match_A = false; match_B = false;
                progress = dataMemory.read16bits(DataMemory_ATmega328P.TCNT1L_ADDR, DataMemory_ATmega328P.TCNT1H_ADDR);

                progress += 1;

                if (progress == BOTTOM) {
                    UCModule.interruptionModule.timer1Overflow();
                }
                if (dataMemory.readForceMatchA_timer1()) {
                    match_A = true; //FORCE MATCH
                } else {
                    ocr1a = dataMemory.read16bits(DataMemory_ATmega328P.OCR1AL_ADDR, DataMemory_ATmega328P.OCR1AH_ADDR);

                    if (progress == ocr1a) {
                        UCModule.interruptionModule.timer1MatchA();
                        match_A = true;
                    }
                }
                if (dataMemory.readForceMatchB_timer1()) {
                    match_B = true; //FORCE MATCH
                } else {
                    ocr1b = dataMemory.read16bits(DataMemory_ATmega328P.OCR1BL_ADDR, DataMemory_ATmega328P.OCR1BH_ADDR);

                    if (progress == ocr1b) {
                        UCModule.interruptionModule.timer1MatchB();
                        match_B = true;
                    }
                }

                byte outputMode = dataMemory.readByte(DataMemory_ATmega328P.TCCR1A_ADDR);

                //CHANEL A
                switch (0xC0 & outputMode) {
                    case 0x00:
                        //OC1A disconected
                        timerOutputControl_OC1A = false;
                        break;
                    case 0x40:
                        //OC1A Toggle on Compare Match
                        timerOutputControl_OC1A = true;
                        if (match_A) {
                            stateOC1A = (stateOC1A + 1) % 2;
                            ioModule.setOC1A(stateOC1A, UCModule_View.simulatedTime);
                        }
                        break;
                    case 0x80:
                        //OC1A Clear on Compare Match
                        timerOutputControl_OC1A = true;
                        if (match_A) {
                            stateOC1A = IOModule.LOW_LEVEL;
                    ioModule.setOC1A(stateOC1A, UCModule_View.simulatedTime);
                        }
                        break;
                    case 0xC0:
                        //OC1A Set on Compare Match
                        timerOutputControl_OC1A = true;
                        if (match_A) {
                            stateOC1A = IOModule.HIGH_LEVEL;
                            ioModule.setOC1A(stateOC1A, UCModule_View.simulatedTime);
                        }
                        break;

                    default:
                        break;
                }

                //CHANEL B
                switch (0x30 & outputMode) {
                    case 0x00:
                        //OC1B disconected
                        timerOutputControl_OC1B = false;
                        break;
                    case 0x10:
                        //OC0B Toggle on Compare Match
                        timerOutputControl_OC1B = true;
                        if (match_B) {
                            stateOC1B = (stateOC1B + 1) % 2;
                            ioModule.setOC1B(stateOC1B, UCModule_View.simulatedTime);
                        }
                        break;
                    case 0x20:
                        //OC0B Clear on Compare Match
                        timerOutputControl_OC1B = true;
                        if (match_B) {
                            stateOC1B = IOModule.LOW_LEVEL;
                            ioModule.setOC1B(stateOC1B, UCModule_View.simulatedTime);
                        }
                        break;
                    case 0x30:
                        //OC0B Set on Compare Match
                        timerOutputControl_OC1B = true;
                        if (match_B) {
                            stateOC1B = IOModule.HIGH_LEVEL;
                            ioModule.setOC1B(stateOC1B, UCModule_View.simulatedTime);
                        }
                        break;

                    default:
                        break;
                }

                dataMemory.write16bits(DataMemory_ATmega328P.TCNT1L_ADDR, DataMemory_ATmega328P.TCNT1H_ADDR,
                        (byte) (0x00FF & progress), (byte) (0x00FF & (progress >> 8)));

            }
        },
        CTC_OPERATION_TOP_OCR1A {
            @Override
            public void count() {
                enableICRWrite = false;
                match_A = false; match_B = false;
                progress = dataMemory.read16bits(DataMemory_ATmega328P.TCNT1L_ADDR, DataMemory_ATmega328P.TCNT1H_ADDR);
                progress += 1;

                if (nextClear) {
                    nextClear = false;
                    progress = BOTTOM;
                }

                if (progress == BOTTOM && nextOverflow) {
                    nextOverflow = false;
                    UCModule.interruptionModule.timer1Overflow();
                } else if (progress == MAX) {
                    nextOverflow = true;
                }

                if (dataMemory.readForceMatchA_timer1()) {
                    match_A = true; //FORCE MATCH
                } else {
                    ocr1a = dataMemory.read16bits(DataMemory_ATmega328P.OCR1AL_ADDR, DataMemory_ATmega328P.OCR1AH_ADDR);

                    if (progress == ocr1a) {
                        UCModule.interruptionModule.timer1MatchA();
                        match_A = true;
                        nextClear = true;
                    }
                }

                if (dataMemory.readForceMatchB_timer1()) {
                    match_B = true; //FORCE MATCH
                } else {
                    ocr1b = dataMemory.read16bits(DataMemory_ATmega328P.OCR1BL_ADDR, DataMemory_ATmega328P.OCR1BH_ADDR);

                    if (progress == ocr1b) {
                        UCModule.interruptionModule.timer1MatchB();
                        match_B = true;
                    }
                }

                byte outputMode = dataMemory.readByte(DataMemory_ATmega328P.TCCR1A_ADDR);

                //CHANEL A
                switch (0xC0 & outputMode) {
                    case 0x00:
                        //OC1A disconected
                        timerOutputControl_OC1A = false;
                        break;
                    case 0x40:
                        //OC1A Toggle on Compare Match
                        timerOutputControl_OC1A = true;
                        if (match_A) {
                            stateOC1A = (stateOC1A + 1) % 2;
                            ioModule.setOC1A(stateOC1A, UCModule_View.simulatedTime);
                        }
                        break;
                    case 0x80:
                        //OC1A Clear on Compare Match
                        timerOutputControl_OC1A = true;
                        if (match_A) {
                            stateOC1A = IOModule.LOW_LEVEL;
                            ioModule.setOC1A(stateOC1A, UCModule_View.simulatedTime);
                        }
                        break;
                    case 0xC0:
                        //OC1A Set on Compare Match
                        timerOutputControl_OC1A = true;
                        if (match_A) {
                            stateOC1A = IOModule.HIGH_LEVEL;
                            ioModule.setOC1A(stateOC1A, UCModule_View.simulatedTime);
                        }
                        break;

                    default:
                        break;
                }

                //CHANEL B
                switch (0x30 & outputMode) {
                    case 0x00:
                        //OC1B disconected
                        timerOutputControl_OC1B = false;
                        break;
                    case 0x10:
                        //OC0B Toggle on Compare Match
                        timerOutputControl_OC1B = true;
                        if (match_B) {
                            stateOC1B = (stateOC1B + 1) % 2;
                            ioModule.setOC1B(stateOC1B, UCModule_View.simulatedTime);
                        }
                        break;
                    case 0x20:
                        //OC0B Clear on Compare Match
                        timerOutputControl_OC1B = true;
                        if (match_B) {
                            stateOC1B = IOModule.LOW_LEVEL;
                            ioModule.setOC1B(stateOC1B, UCModule_View.simulatedTime);
                        }
                        break;
                    case 0x30:
                        //OC0B Set on Compare Match
                        timerOutputControl_OC1B = true;
                        if (match_B) {
                            stateOC1B = IOModule.HIGH_LEVEL;
                            ioModule.setOC1B(stateOC1B, UCModule_View.simulatedTime);
                        }
                        break;

                    default:
                        break;
                }

                dataMemory.write16bits(DataMemory_ATmega328P.TCNT1L_ADDR, DataMemory_ATmega328P.TCNT1H_ADDR,
                        (byte) (0x00FF & progress), (byte) (0x00FF & (progress >> 8)));
            }
        },
        CTC_OPERATION_TOP_ICR1 {
            @Override
            public void count() {
                enableICRWrite = true;
                match_A = false; match_B = false;
                progress = dataMemory.read16bits(DataMemory_ATmega328P.TCNT1L_ADDR, DataMemory_ATmega328P.TCNT1H_ADDR);
                progress += 1;

                if (nextClear) {
                    nextClear = false;
                    progress = BOTTOM;
                }

                icr1 = dataMemory.read16bits(DataMemory_ATmega328P.ICR1L_ADDR, DataMemory_ATmega328P.ICR1H_ADDR);
                if (progress == icr1) {
                    UCModule.interruptionModule.timer1InputCapture();
                    nextClear = true;
                }

                if (progress == BOTTOM && nextOverflow) {
                    nextOverflow = false;
                    UCModule.interruptionModule.timer1Overflow();
                } else if (progress == MAX) {
                    nextOverflow = true;
                }

                if (dataMemory.readForceMatchA_timer1()) {
                    match_A = true; //FORCE MATCH
                } else {
                    ocr1a = dataMemory.read16bits(DataMemory_ATmega328P.OCR1AL_ADDR, DataMemory_ATmega328P.OCR1AH_ADDR);

                    if (progress == ocr1a) {
                        UCModule.interruptionModule.timer1MatchA();
                        match_A = true;
                    }
                }

                if (dataMemory.readForceMatchB_timer1()) {
                    match_B = true; //FORCE MATCH
                } else {
                    ocr1b = dataMemory.read16bits(DataMemory_ATmega328P.OCR1BL_ADDR, DataMemory_ATmega328P.OCR1BH_ADDR);

                    if (progress == ocr1b) {
                        UCModule.interruptionModule.timer1MatchB();
                        match_B = true;
                    }
                }

                byte outputMode = dataMemory.readByte(DataMemory_ATmega328P.TCCR1A_ADDR);

                //CHANEL A
                switch (0xC0 & outputMode) {
                    case 0x00:
                        //OC1A disconected
                        timerOutputControl_OC1A = false;
                        break;
                    case 0x40:
                        //OC1A Toggle on Compare Match
                        timerOutputControl_OC1A = true;
                        if (match_A) {
                            stateOC1A = (stateOC1A + 1) % 2;
                            ioModule.setOC1A(stateOC1A, UCModule_View.simulatedTime);
                        }
                        break;
                    case 0x80:
                        //OC1A Clear on Compare Match
                        timerOutputControl_OC1A = true;
                        if (match_A) {
                            stateOC1A = IOModule.LOW_LEVEL;
                            ioModule.setOC1A(stateOC1A, UCModule_View.simulatedTime);
                        }
                        break;
                    case 0xC0:
                        //OC1A Set on Compare Match
                        timerOutputControl_OC1A = true;
                        if (match_A) {
                            stateOC1A = IOModule.HIGH_LEVEL;
                            ioModule.setOC1A(stateOC1A, UCModule_View.simulatedTime);
                        }
                        break;

                    default:
                        break;
                }

                //CHANEL B
                switch (0x30 & outputMode) {
                    case 0x00:
                        //OC1B disconected
                        timerOutputControl_OC1B = false;
                        break;
                    case 0x10:
                        //OC0B Toggle on Compare Match
                        timerOutputControl_OC1B = true;
                        if (match_B) {
                            stateOC1B = (stateOC1B + 1) % 2;
                            ioModule.setOC1B(stateOC1B, UCModule_View.simulatedTime);
                        }
                        break;
                    case 0x20:
                        //OC0B Clear on Compare Match
                        timerOutputControl_OC1B = true;
                        if (match_B) {
                            stateOC1B = IOModule.LOW_LEVEL;
                            ioModule.setOC1B(stateOC1B, UCModule_View.simulatedTime);
                        }
                        break;
                    case 0x30:
                        //OC0B Set on Compare Match
                        timerOutputControl_OC1B = true;
                        if (match_B) {
                            stateOC1B = IOModule.HIGH_LEVEL;
                            ioModule.setOC1B(stateOC1B, UCModule_View.simulatedTime);
                        }
                        break;

                    default:
                        break;
                }

                dataMemory.write16bits(DataMemory_ATmega328P.TCNT1L_ADDR, DataMemory_ATmega328P.TCNT1H_ADDR,
                        (byte) (0x00FF & progress), (byte) (0x00FF & (progress >> 8)));
            }
        },
        FAST_PWM_8B {
            @Override
            public void count() {
                enableICRWrite = false;
                if (!dataMemory.isOCR1AReady()) {
                    return;
                }

                match_A = false; match_B = false;
                progress = dataMemory.read16bits(DataMemory_ATmega328P.TCNT1L_ADDR, DataMemory_ATmega328P.TCNT1H_ADDR);

                if (progress == BOTTOM) {
                    doubleBufferOCR1A = dataMemory.read16bits(DataMemory_ATmega328P.OCR1AL_ADDR, DataMemory_ATmega328P.OCR1AH_ADDR);

                    doubleBufferOCR1B = dataMemory.read16bits(DataMemory_ATmega328P.OCR1BL_ADDR, DataMemory_ATmega328P.OCR1BH_ADDR);
                }

                progress += 1;

                if (nextClear) {
                    nextClear = false;
                    progress = BOTTOM;
                    UCModule.interruptionModule.timer1Overflow();
                }

                if (progress == MAX_8B) {
                    nextClear = true;
                }

                if (progress == doubleBufferOCR1A) {
                    UCModule.interruptionModule.timer1MatchA();
                    match_A = true;
                }

                if (progress == doubleBufferOCR1B) {
                    UCModule.interruptionModule.timer1MatchB();
                    match_B = true;
                }


                byte outputMode = dataMemory.readByte(DataMemory_ATmega328P.TCCR1A_ADDR);

                //CHANEL A
                switch (0xC0 & outputMode) {
                    case 0x00:
                    case 0x40:
                        //OC1A disconected
                        timerOutputControl_OC1A = false;
                        break;
                    case 0x80:
                        //OC1A Clear on Compare Match, set at BOTTOM
                        timerOutputControl_OC1A = true;
                        if (doubleBufferOCR1A == MAX_8B) {
                            stateOC1A = IOModule.HIGH_LEVEL;
                            ioModule.setOC1A(stateOC1A, UCModule_View.simulatedTime);
                        } else {
                            if (match_A) {
                                stateOC1A = IOModule.LOW_LEVEL;
                                ioModule.setOC1A(stateOC1A, UCModule_View.simulatedTime);
                            }
                            if (progress == BOTTOM) {
                                stateOC1A = IOModule.HIGH_LEVEL;
                                ioModule.setOC1A(stateOC1A, UCModule_View.simulatedTime);
                            }
                        }
                        break;
                    case 0xC0:
                        //OC1A Set on Compare Match, clear at BOTTOM
                        timerOutputControl_OC1A = true;
                        if (doubleBufferOCR1A == MAX_8B) {
                            stateOC1A = IOModule.LOW_LEVEL;
                            ioModule.setOC1A(stateOC1A, UCModule_View.simulatedTime);
                        } else {
                            if (match_A) {
                                stateOC1A = IOModule.HIGH_LEVEL;
                                ioModule.setOC1A(stateOC1A, UCModule_View.simulatedTime);
                            }

                            if (progress == BOTTOM) {
                                stateOC1A = IOModule.LOW_LEVEL;
                                ioModule.setOC1A(stateOC1A, UCModule_View.simulatedTime);
                            }
                        }
                        break;

                    default:
                        break;
                }

                //CHANEL B
                switch (0x30 & outputMode) {
                    case 0x00:
                    case 0x10:
                        //OC1B disconected
                        timerOutputControl_OC1B = false;
                        break;
                    case 0x20:
                        //OC1B Clear on Compare Match, set at BOTTOM
                        timerOutputControl_OC1B = true;
                        if (doubleBufferOCR1B == MAX_8B) {
                            stateOC1B = IOModule.HIGH_LEVEL;
                            ioModule.setOC1B(stateOC1B, UCModule_View.simulatedTime);
                        } else {
                            if (match_B) {
                                stateOC1B = IOModule.LOW_LEVEL;
                                ioModule.setOC1B(stateOC1B, UCModule_View.simulatedTime);
                            }
                            if (progress == BOTTOM) {
                                stateOC1B = IOModule.HIGH_LEVEL;
                                ioModule.setOC1B(stateOC1B, UCModule_View.simulatedTime);
                            }
                        }
                        break;
                    case 0x30:
                        //OC1B Set on Compare Match, clear at BOTTOM
                        timerOutputControl_OC1B = true;
                        if (doubleBufferOCR1B == MAX_8B) {
                            stateOC1B = IOModule.LOW_LEVEL;
                            ioModule.setOC1B(stateOC1B, UCModule_View.simulatedTime);
                        } else {
                            if (match_B) {
                                stateOC1B = IOModule.HIGH_LEVEL;
                                ioModule.setOC1B(stateOC1B, UCModule_View.simulatedTime);
                            }
                            if (progress == BOTTOM) {
                                stateOC1B = IOModule.LOW_LEVEL;
                                ioModule.setOC1B(stateOC1B, UCModule_View.simulatedTime);
                            }
                        }
                        break;

                    default:
                        break;
                }

                dataMemory.write16bits(DataMemory_ATmega328P.TCNT1L_ADDR, DataMemory_ATmega328P.TCNT1H_ADDR,
                        (byte) (0x00FF & progress), (byte) (0x00FF & (progress >> 8)));
            }
        },
        FAST_PWM_9B {
            @Override
            public void count() {
                enableICRWrite = false;
                if (!dataMemory.isOCR1AReady()) {
                    return;
                }

                match_A = false; match_B = false;
                progress = dataMemory.read16bits(DataMemory_ATmega328P.TCNT1L_ADDR, DataMemory_ATmega328P.TCNT1H_ADDR);

                if (progress == BOTTOM) {
                    doubleBufferOCR1A = dataMemory.read16bits(DataMemory_ATmega328P.OCR1AL_ADDR, DataMemory_ATmega328P.OCR1AH_ADDR);

                    doubleBufferOCR1B = dataMemory.read16bits(DataMemory_ATmega328P.OCR1BL_ADDR, DataMemory_ATmega328P.OCR1BH_ADDR);
                }

                progress += 1;

                if (nextClear) {
                    nextClear = false;
                    progress = BOTTOM;
                    UCModule.interruptionModule.timer1Overflow();
                }

                if (progress == MAX_9B) {
                    nextClear = true;
                }

                if (progress == doubleBufferOCR1A) {
                    UCModule.interruptionModule.timer1MatchA();
                    match_A = true;
                }

                if (progress == doubleBufferOCR1B) {
                    UCModule.interruptionModule.timer1MatchB();
                    match_B = true;
                }


                byte outputMode = dataMemory.readByte(DataMemory_ATmega328P.TCCR1A_ADDR);

                //CHANEL A
                switch (0xC0 & outputMode) {
                    case 0x00:
                    case 0x40:
                        //OC1A disconected
                        timerOutputControl_OC1A = false;
                        break;
                    case 0x80:
                        //OC1A Clear on Compare Match, set at BOTTOM
                        timerOutputControl_OC1A = true;
                        if (doubleBufferOCR1A == MAX_9B) {
                            stateOC1A = IOModule.HIGH_LEVEL;
                            ioModule.setOC1A(stateOC1A, UCModule_View.simulatedTime);
                        } else {
                            if (match_A) {
                                stateOC1A = IOModule.LOW_LEVEL;
                                ioModule.setOC1A(stateOC1A, UCModule_View.simulatedTime);
                            }
                            if (progress == BOTTOM) {
                                stateOC1A = IOModule.HIGH_LEVEL;
                                ioModule.setOC1A(stateOC1A, UCModule_View.simulatedTime);
                            }
                        }
                        break;
                    case 0xC0:
                        //OC1A Set on Compare Match, clear at BOTTOM
                        timerOutputControl_OC1A = true;
                        if (doubleBufferOCR1A == MAX_9B) {
                            stateOC1A = IOModule.LOW_LEVEL;
                            ioModule.setOC1A(stateOC1A, UCModule_View.simulatedTime);
                        } else {
                            if (match_A) {
                                stateOC1A = IOModule.HIGH_LEVEL;
                                ioModule.setOC1A(stateOC1A, UCModule_View.simulatedTime);
                            }

                            if (progress == BOTTOM) {
                                stateOC1A = IOModule.LOW_LEVEL;
                                ioModule.setOC1A(stateOC1A, UCModule_View.simulatedTime);
                            }
                        }
                        break;

                    default:
                        break;

                }

                //CHANEL B
                switch (0x30 & outputMode) {
                    case 0x00:
                    case 0x10:
                        //OC1B disconected
                        timerOutputControl_OC1B = false;
                        break;
                    case 0x20:
                        //OC1B Clear on Compare Match, set at BOTTOM
                        timerOutputControl_OC1B = true;
                        if (doubleBufferOCR1B == MAX_9B) {
                            stateOC1B = IOModule.HIGH_LEVEL;
                            ioModule.setOC1B(stateOC1B, UCModule_View.simulatedTime);
                        } else {
                            if (match_B) {
                                stateOC1B = IOModule.LOW_LEVEL;
                                ioModule.setOC1B(stateOC1B, UCModule_View.simulatedTime);
                            }
                            if (progress == BOTTOM) {
                                stateOC1B = IOModule.HIGH_LEVEL;
                                ioModule.setOC1B(stateOC1B, UCModule_View.simulatedTime);
                            }
                        }
                        break;
                    case 0x30:
                        //OC1B Set on Compare Match, clear at BOTTOM
                        timerOutputControl_OC1B = true;
                        if (doubleBufferOCR1B == MAX_9B) {
                            stateOC1B = IOModule.LOW_LEVEL;
                            ioModule.setOC1B(stateOC1B, UCModule_View.simulatedTime);
                        } else {
                            if (match_B) {
                                stateOC1B = IOModule.HIGH_LEVEL;
                                ioModule.setOC1B(stateOC1B, UCModule_View.simulatedTime);
                            }
                            if (progress == BOTTOM) {
                                stateOC1B = IOModule.LOW_LEVEL;
                                ioModule.setOC1B(stateOC1B, UCModule_View.simulatedTime);
                            }
                        }
                        break;

                    default:
                        break;
                }

                dataMemory.write16bits(DataMemory_ATmega328P.TCNT1L_ADDR, DataMemory_ATmega328P.TCNT1H_ADDR,
                        (byte) (0x00FF & progress), (byte) (0x00FF & (progress >> 8)));
            }
        },
        FAST_PWM_10B {
            @Override
            public void count() {
                enableICRWrite = false;
                if (!dataMemory.isOCR1AReady()) {
                    return;
                }

                match_A = false; match_B = false;
                progress = dataMemory.read16bits(DataMemory_ATmega328P.TCNT1L_ADDR, DataMemory_ATmega328P.TCNT1H_ADDR);

                if (progress == BOTTOM) {
                    doubleBufferOCR1A = dataMemory.read16bits(DataMemory_ATmega328P.OCR1AL_ADDR, DataMemory_ATmega328P.OCR1AH_ADDR);

                    doubleBufferOCR1B = dataMemory.read16bits(DataMemory_ATmega328P.OCR1BL_ADDR, DataMemory_ATmega328P.OCR1BH_ADDR);
                }

                progress += 1;

                if (nextClear) {
                    nextClear = false;
                    progress = BOTTOM;
                    UCModule.interruptionModule.timer1Overflow();
                }

                if (progress == MAX_10B) {
                    nextClear = true;
                }

                if (progress == doubleBufferOCR1A) {
                    UCModule.interruptionModule.timer1MatchA();
                    match_A = true;
                }

                if (progress == doubleBufferOCR1B) {
                    UCModule.interruptionModule.timer1MatchB();
                    match_B = true;
                }


                byte outputMode = dataMemory.readByte(DataMemory_ATmega328P.TCCR1A_ADDR);

                //CHANEL A
                switch (0xC0 & outputMode) {
                    case 0x00:
                    case 0x40:
                        //OC1A disconected
                        timerOutputControl_OC1A = false;
                        break;
                    case 0x80:
                        //OC1A Clear on Compare Match, set at BOTTOM
                        timerOutputControl_OC1A = true;
                        if (doubleBufferOCR1A == MAX_10B) {
                            stateOC1A = IOModule.HIGH_LEVEL;
                            ioModule.setOC1A(stateOC1A, UCModule_View.simulatedTime);
                        } else {
                            if (match_A) {
                                stateOC1A = IOModule.LOW_LEVEL;
                                ioModule.setOC1A(stateOC1A, UCModule_View.simulatedTime);
                            }
                            if (progress == BOTTOM) {
                                stateOC1A = IOModule.HIGH_LEVEL;
                                ioModule.setOC1A(stateOC1A, UCModule_View.simulatedTime);
                            }
                        }
                        break;
                    case 0xC0:
                        //OC1A Set on Compare Match, clear at BOTTOM
                        timerOutputControl_OC1A = true;
                        if (doubleBufferOCR1A == MAX_10B) {
                            stateOC1A = IOModule.LOW_LEVEL;
                            ioModule.setOC1A(stateOC1A, UCModule_View.simulatedTime);
                        } else {
                            if (match_A) {
                                stateOC1A = IOModule.HIGH_LEVEL;
                                ioModule.setOC1A(stateOC1A, UCModule_View.simulatedTime);
                            }

                            if (progress == BOTTOM) {
                                stateOC1A = IOModule.LOW_LEVEL;
                                ioModule.setOC1A(stateOC1A, UCModule_View.simulatedTime);
                            }
                        }
                        break;

                    default:
                        break;

                }

                //CHANEL B
                switch (0x30 & outputMode) {
                    case 0x00:
                    case 0x10:
                        //OC1B disconected
                        timerOutputControl_OC1B = false;
                        break;
                    case 0x20:
                        //OC1B Clear on Compare Match, set at BOTTOM
                        timerOutputControl_OC1B = true;
                        if (doubleBufferOCR1B == MAX_10B) {
                            stateOC1B = IOModule.HIGH_LEVEL;
                            ioModule.setOC1B(stateOC1B, UCModule_View.simulatedTime);
                        } else {
                            if (match_B) {
                                stateOC1B = IOModule.LOW_LEVEL;
                                ioModule.setOC1B(stateOC1B, UCModule_View.simulatedTime);
                            }
                            if (progress == BOTTOM) {
                                stateOC1B = IOModule.HIGH_LEVEL;
                                ioModule.setOC1B(stateOC1B, UCModule_View.simulatedTime);
                            }
                        }
                        break;
                    case 0x30:
                        //OC1B Set on Compare Match, clear at BOTTOM
                        timerOutputControl_OC1B = true;
                        if (doubleBufferOCR1B == MAX_10B) {
                            stateOC1B = IOModule.LOW_LEVEL;
                            ioModule.setOC1B(stateOC1B, UCModule_View.simulatedTime);
                        } else {
                            if (match_B) {
                                stateOC1B = IOModule.HIGH_LEVEL;
                                ioModule.setOC1B(stateOC1B, UCModule_View.simulatedTime);
                            }
                            if (progress == BOTTOM) {
                                stateOC1B = IOModule.LOW_LEVEL;
                                ioModule.setOC1B(stateOC1B, UCModule_View.simulatedTime);
                            }
                        }
                        break;

                    default:
                        break;
                }

                dataMemory.write16bits(DataMemory_ATmega328P.TCNT1L_ADDR, DataMemory_ATmega328P.TCNT1H_ADDR,
                        (byte) (0x00FF & progress), (byte) (0x00FF & (progress >> 8)));
            }
        },
        FAST_PWM_TOP_ICR1 {
            @Override
            public void count() {
                enableICRWrite = true;
                if (!dataMemory.isOCR1AReady()) {
                    return;
                }

                match_A = false; match_B = false;
                progress = dataMemory.read16bits(DataMemory_ATmega328P.TCNT1L_ADDR, DataMemory_ATmega328P.TCNT1H_ADDR);

                if (progress == BOTTOM) {
                    doubleBufferOCR1A = dataMemory.read16bits(DataMemory_ATmega328P.OCR1AL_ADDR, DataMemory_ATmega328P.OCR1AH_ADDR);

                    doubleBufferOCR1B = dataMemory.read16bits(DataMemory_ATmega328P.OCR1BL_ADDR, DataMemory_ATmega328P.OCR1BH_ADDR);
                }

                progress += 1;

                if (nextClear) {
                    nextClear = false;
                    progress = BOTTOM;
                    UCModule.interruptionModule.timer1Overflow();
                }

                icr1 = dataMemory.read16bits(DataMemory_ATmega328P.ICR1L_ADDR, DataMemory_ATmega328P.ICR1H_ADDR);

               System.out.println("TIMER1_MODE"+ "Progress: " + Integer.toHexString(progress));
               System.out.println("TIMER1_MODE"+ "ICR1: " + Integer.toHexString(icr1));

                if (progress == icr1) {
                    UCModule.interruptionModule.timer1InputCapture();
                    nextClear = true;
                }

                if (progress == doubleBufferOCR1A) {
                    UCModule.interruptionModule.timer1MatchA();
                    match_A = true;
                }

                if (progress == doubleBufferOCR1B) {
                    UCModule.interruptionModule.timer1MatchB();
                    match_B = true;
                }


                byte outputMode = dataMemory.readByte(DataMemory_ATmega328P.TCCR1A_ADDR);

                //CHANEL A
                switch (0xC0 & outputMode) {
                    case 0x00:
                        //OC1A disconected
                        timerOutputControl_OC1A = false;
                        break;
                    case 0x40:
                        timerOutputControl_OC1A = true;
                        if (match_A) {
                            stateOC1A = (stateOC1A + 1) % 2;  //Toggle
                            ioModule.setOC1A(stateOC1A, UCModule_View.simulatedTime);
                        }
                        break;
                    case 0x80:
                        //OC1A Clear on Compare Match, set at BOTTOM
                        timerOutputControl_OC1A = true;
                        if (doubleBufferOCR1A == icr1) {
                            stateOC1A = IOModule.HIGH_LEVEL;
                            ioModule.setOC1A(stateOC1A, UCModule_View.simulatedTime);
                        } else {
                            if (match_A) {
                                stateOC1A = IOModule.LOW_LEVEL;
                                ioModule.setOC1A(stateOC1A, UCModule_View.simulatedTime);
                            }
                            if (progress == BOTTOM) {
                                stateOC1A = IOModule.HIGH_LEVEL;
                                ioModule.setOC1A(stateOC1A, UCModule_View.simulatedTime);
                            }
                        }
                        break;
                    case 0xC0:
                        //OC1A Set on Compare Match, clear at BOTTOM
                        timerOutputControl_OC1A = true;
                        if (doubleBufferOCR1A == icr1) {
                            stateOC1A = IOModule.LOW_LEVEL;
                            ioModule.setOC1A(stateOC1A, UCModule_View.simulatedTime);
                        } else {
                            if (match_A) {
                                stateOC1A = IOModule.HIGH_LEVEL;
                                ioModule.setOC1A(stateOC1A, UCModule_View.simulatedTime);
                            }

                            if (progress == BOTTOM) {
                                stateOC1A = IOModule.LOW_LEVEL;
                                ioModule.setOC1A(stateOC1A, UCModule_View.simulatedTime);
                            }
                        }
                        break;

                    default:
                        break;

                }

                //CHANEL B
                switch (0x30 & outputMode) {
                    case 0x00:
                    case 0x10:
                        //OC1B disconected
                        timerOutputControl_OC1B = false;
                        break;
                    case 0x20:
                        //OC1B Clear on Compare Match, set at BOTTOM
                        timerOutputControl_OC1B = true;
                        if (doubleBufferOCR1B == icr1) {
                            stateOC1B = IOModule.HIGH_LEVEL;
                            ioModule.setOC1B(stateOC1B, UCModule_View.simulatedTime);
                        } else {
                            if (match_B) {
                                stateOC1B = IOModule.LOW_LEVEL;
                                ioModule.setOC1B(stateOC1B, UCModule_View.simulatedTime);
                            }
                            if (progress == BOTTOM) {
                                stateOC1B = IOModule.HIGH_LEVEL;
                                ioModule.setOC1B(stateOC1B, UCModule_View.simulatedTime);
                            }
                        }
                        break;
                    case 0x30:
                        //OC1B Set on Compare Match, clear at BOTTOM
                        timerOutputControl_OC1B = true;
                        if (doubleBufferOCR1B == icr1) {
                            stateOC1B = IOModule.LOW_LEVEL;
                            ioModule.setOC1B(stateOC1B, UCModule_View.simulatedTime);
                        } else {
                            if (match_B) {
                                stateOC1B = IOModule.HIGH_LEVEL;
                                ioModule.setOC1B(stateOC1B, UCModule_View.simulatedTime);
                            }
                            if (progress == BOTTOM) {
                                stateOC1B = IOModule.LOW_LEVEL;
                                ioModule.setOC1B(stateOC1B, UCModule_View.simulatedTime);
                            }
                        }
                        break;

                    default:
                        break;
                }

                dataMemory.write16bits(DataMemory_ATmega328P.TCNT1L_ADDR, DataMemory_ATmega328P.TCNT1H_ADDR,
                        (byte) (0x00FF & progress), (byte) (0x00FF & (progress >> 8)));
            }
        },
        FAST_PWM_TOP_OCRA {
            @Override
            public void count() {
                enableICRWrite = false;
                if (!dataMemory.isOCR1AReady()) {
                    return;
                }

                match_A = false; match_B = false;
                progress = dataMemory.read16bits(DataMemory_ATmega328P.TCNT1L_ADDR, DataMemory_ATmega328P.TCNT1H_ADDR);

                if (progress == BOTTOM) {
                    doubleBufferOCR1A = dataMemory.read16bits(DataMemory_ATmega328P.OCR1AL_ADDR, DataMemory_ATmega328P.OCR1AH_ADDR);

                    doubleBufferOCR1B = dataMemory.read16bits(DataMemory_ATmega328P.OCR1BL_ADDR, DataMemory_ATmega328P.OCR1BH_ADDR);
                }

                progress += 1;

                if (nextClear) {
                    nextClear = false;
                    progress = BOTTOM;
                    UCModule.interruptionModule.timer1Overflow();
                }

                if (progress == doubleBufferOCR1A) {
                    UCModule.interruptionModule.timer1MatchA();
                    match_A = true;
                    nextClear = true;
                }

                if (progress == doubleBufferOCR1B) {
                    UCModule.interruptionModule.timer1MatchB();
                    match_B = true;
                }


                byte outputMode = dataMemory.readByte(DataMemory_ATmega328P.TCCR1A_ADDR);

                //CHANEL A
                switch (0xC0 & outputMode) {
                    case 0x00:
                        //OC1A disconected
                        timerOutputControl_OC1A = false;
                        break;
                    case 0x40:
                        timerOutputControl_OC1A = true;
                        if (match_A) {
                            stateOC1A = (stateOC1A + 1) % 2;  //Toggle
                            ioModule.setOC1A(stateOC1A, UCModule_View.simulatedTime);
                        }
                        break;
                    case 0x80:
                        //OC1A Clear on Compare Match, set at BOTTOM
                        timerOutputControl_OC1A = true;
                        if (doubleBufferOCR1A == MAX) {
                            stateOC1A = IOModule.HIGH_LEVEL;
                            ioModule.setOC1A(stateOC1A, UCModule_View.simulatedTime);
                        } else {
                            if (match_A) {
                                stateOC1A = IOModule.LOW_LEVEL;
                                ioModule.setOC1A(stateOC1A, UCModule_View.simulatedTime);
                            }
                            if (progress == BOTTOM) {
                                stateOC1A = IOModule.HIGH_LEVEL;
                                ioModule.setOC1A(stateOC1A, UCModule_View.simulatedTime);
                            }
                        }
                        break;
                    case 0xC0:
                        //OC1A Set on Compare Match, clear at BOTTOM
                        timerOutputControl_OC1A = true;
                        if (doubleBufferOCR1A == MAX) {
                            stateOC1A = IOModule.LOW_LEVEL;
                            ioModule.setOC1A(stateOC1A, UCModule_View.simulatedTime);
                        } else {
                            if (match_A) {
                                stateOC1A = IOModule.HIGH_LEVEL;
                                ioModule.setOC1A(stateOC1A, UCModule_View.simulatedTime);
                            }

                            if (progress == BOTTOM) {
                                stateOC1A = IOModule.LOW_LEVEL;
                                ioModule.setOC1A(stateOC1A, UCModule_View.simulatedTime);
                            }
                        }
                        break;

                    default:
                        break;

                }

                //CHANEL B
                switch (0x30 & outputMode) {
                    case 0x00:
                    case 0x10:
                        //OC1B disconected
                        timerOutputControl_OC1B = false;
                        break;
                    case 0x20:
                        //OC1B Clear on Compare Match, set at BOTTOM
                        timerOutputControl_OC1B = true;
                        if (doubleBufferOCR1B == doubleBufferOCR1A) {
                            stateOC1B = IOModule.HIGH_LEVEL;
                            ioModule.setOC1B(stateOC1B, UCModule_View.simulatedTime);
                        } else {
                            if (match_B) {
                                stateOC1B = IOModule.LOW_LEVEL;
                                ioModule.setOC1B(stateOC1B, UCModule_View.simulatedTime);
                            }
                            if (progress == BOTTOM) {
                                stateOC1B = IOModule.HIGH_LEVEL;
                                ioModule.setOC1B(stateOC1B, UCModule_View.simulatedTime);
                            }
                        }
                        break;
                    case 0x30:
                        //OC1B Set on Compare Match, clear at BOTTOM
                        timerOutputControl_OC1B = true;
                        if (doubleBufferOCR1B == doubleBufferOCR1A) {
                            stateOC1B = IOModule.LOW_LEVEL;
                            ioModule.setOC1B(stateOC1B, UCModule_View.simulatedTime);
                        } else {
                            if (match_B) {
                                stateOC1B = IOModule.HIGH_LEVEL;
                                ioModule.setOC1B(stateOC1B, UCModule_View.simulatedTime);
                            }
                            if (progress == BOTTOM) {
                                stateOC1B = IOModule.LOW_LEVEL;
                                ioModule.setOC1B(stateOC1B, UCModule_View.simulatedTime);
                            }
                        }
                        break;

                    default:
                        break;
                }

                dataMemory.write16bits(DataMemory_ATmega328P.TCNT1L_ADDR, DataMemory_ATmega328P.TCNT1H_ADDR,
                        (byte) (0x00FF & progress), (byte) (0x00FF & (progress >> 8)));
            }
        },
        PWM_PHASE_CORRECT_8B {
            @Override
            public void count() {
                enableICRWrite = false;
                if (!dataMemory.isOCR1AReady()) {
                    return;
                }

                match_A = false; match_B = false;
                progress = dataMemory.read16bits(DataMemory_ATmega328P.TCNT1L_ADDR, DataMemory_ATmega328P.TCNT1H_ADDR);

                if (progress == MAX_8B) {
                    doubleBufferOCR1A = dataMemory.read16bits(DataMemory_ATmega328P.OCR1AL_ADDR, DataMemory_ATmega328P.OCR1AH_ADDR);

                    doubleBufferOCR1B = dataMemory.read16bits(DataMemory_ATmega328P.OCR1BL_ADDR, DataMemory_ATmega328P.OCR1BH_ADDR);
                }

                if (phaseCorrect_UPCount) {
                    progress += 1;
                } else {
                    progress -= 1;
                }

                phaseCorrect_UPCount_old = phaseCorrect_UPCount;

                if (progress == BOTTOM) {
                    UCModule.interruptionModule.timer1Overflow();
                    phaseCorrect_UPCount = true;
                } else if (progress == MAX_8B) {
                    phaseCorrect_UPCount = false;
                }

                if (progress == doubleBufferOCR1A) {
                    UCModule.interruptionModule.timer1MatchA();
                    match_A = true;
                }
                if (progress == doubleBufferOCR1B) {
                    UCModule.interruptionModule.timer1MatchB();
                    match_B = true;
                }

                byte outputMode = dataMemory.readByte(DataMemory_ATmega328P.TCCR1A_ADDR);

                //CHANEL A
                switch (0xC0 & outputMode) {
                    case 0x00:
                    case 0x40:
                        //OC1A disconected
                        timerOutputControl_OC1A = false;
                        break;
                    case 0x80:
                        //OC1A Clear on Compare Match counting up, OC1A Set on Compare Match counting down
                        timerOutputControl_OC1A = true;
                        if (doubleBufferOCR1A == MAX_8B) {
                            stateOC1A = IOModule.HIGH_LEVEL;
                            ioModule.setOC1A(stateOC1A, UCModule_View.simulatedTime);
                        } else if (doubleBufferOCR1A == BOTTOM) {
                            stateOC1A = IOModule.LOW_LEVEL;
                            ioModule.setOC1A(stateOC1A, UCModule_View.simulatedTime);
                        } else {
                            if (match_A) {
                                if (phaseCorrect_UPCount_old) {
                                    stateOC1A = IOModule.LOW_LEVEL;
                                } else {
                                    stateOC1A = IOModule.HIGH_LEVEL;
                                }
                                ioModule.setOC1A(stateOC1A, UCModule_View.simulatedTime);
                            }
                        }
                        break;
                    case 0xC0:
                        //OC1A Set on Compare Match counting up, OC1A Clear on Compare Match counting down
                        timerOutputControl_OC1A = true;
                        if (doubleBufferOCR1A == MAX_8B) {
                            stateOC1A = IOModule.LOW_LEVEL;
                            ioModule.setOC1A(stateOC1A, UCModule_View.simulatedTime);
                        } else if (doubleBufferOCR1A == BOTTOM) {
                            stateOC1A = IOModule.HIGH_LEVEL;
                            ioModule.setOC1A(stateOC1A, UCModule_View.simulatedTime);
                        } else {
                            if (match_A) {
                                if (phaseCorrect_UPCount_old) {
                                    stateOC1A = IOModule.HIGH_LEVEL;
                                } else {
                                    stateOC1A = IOModule.LOW_LEVEL;
                                }
                                ioModule.setOC1A(stateOC1A, UCModule_View.simulatedTime);
                            }
                        }
                        break;

                    default:
                        break;
                }

                //CHANEL B
                switch (0x30 & outputMode) {
                    case 0x00:
                    case 0x10:
                        //OC1B disconected
                        timerOutputControl_OC1B = false;
                        break;
                    case 0x20:
                        //OC1B Clear on Compare Match counting up, OC1B Set on Compare Match counting down
                        timerOutputControl_OC1B = true;
                        if (doubleBufferOCR1B == MAX_8B) {
                            stateOC1B = IOModule.HIGH_LEVEL;
                            ioModule.setOC1B(stateOC1B, UCModule_View.simulatedTime);
                        } else if (doubleBufferOCR1B == BOTTOM) {
                            stateOC1B = IOModule.LOW_LEVEL;
                            ioModule.setOC1B(stateOC1B, UCModule_View.simulatedTime);
                        } else {
                            if (match_B) {
                                if (phaseCorrect_UPCount_old) {
                                    stateOC1B = IOModule.LOW_LEVEL;
                                } else {
                                    stateOC1B = IOModule.HIGH_LEVEL;
                                }
                                ioModule.setOC1B(stateOC1B, UCModule_View.simulatedTime);
                            }
                        }
                        break;
                    case 0x30:
                        //OC1B Set on Compare Match counting up, OC1B Clear on Compare Match counting down
                        timerOutputControl_OC1B = true;
                        if (doubleBufferOCR1B == MAX_8B) {
                            stateOC1B = IOModule.LOW_LEVEL;
                            ioModule.setOC1B(stateOC1B, UCModule_View.simulatedTime);
                        } else if (doubleBufferOCR1B == BOTTOM) {
                            stateOC1B = IOModule.HIGH_LEVEL;
                            ioModule.setOC1B(stateOC1B, UCModule_View.simulatedTime);
                        } else {
                            if (match_B) {
                                if (phaseCorrect_UPCount_old) {
                                    stateOC1B = IOModule.HIGH_LEVEL;
                                } else {
                                    stateOC1B = IOModule.LOW_LEVEL;
                                }
                                ioModule.setOC1B(stateOC1B, UCModule_View.simulatedTime);
                            }
                        }
                        break;

                    default:
                        break;
                }

                dataMemory.write16bits(DataMemory_ATmega328P.TCNT1L_ADDR, DataMemory_ATmega328P.TCNT1H_ADDR,
                        (byte) (0x00FF & progress), (byte) (0x00FF & (progress >> 8)));
            }
        },
        PWM_PHASE_CORRECT_9B {
            @Override
            public void count() {
                enableICRWrite = false;
                if (!dataMemory.isOCR1AReady()) {
                    return;
                }

                match_A = false; match_B = false;
                progress = dataMemory.read16bits(DataMemory_ATmega328P.TCNT1L_ADDR, DataMemory_ATmega328P.TCNT1H_ADDR);

                if (progress == MAX_9B) {
                    doubleBufferOCR1A = dataMemory.read16bits(DataMemory_ATmega328P.OCR1AL_ADDR, DataMemory_ATmega328P.OCR1AH_ADDR);

                    doubleBufferOCR1B = dataMemory.read16bits(DataMemory_ATmega328P.OCR1BL_ADDR, DataMemory_ATmega328P.OCR1BH_ADDR);
                }

                if (phaseCorrect_UPCount) {
                    progress += 1;
                } else {
                    progress -= 1;
                }

                phaseCorrect_UPCount_old = phaseCorrect_UPCount;

                if (progress == BOTTOM) {
                    UCModule.interruptionModule.timer1Overflow();
                    phaseCorrect_UPCount = true;
                } else if (progress == MAX_9B) {
                    phaseCorrect_UPCount = false;
                }

                if (progress == doubleBufferOCR1A) {
                    UCModule.interruptionModule.timer1MatchA();
                    match_A = true;
                }
                if (progress == doubleBufferOCR1B) {
                    UCModule.interruptionModule.timer1MatchB();
                    match_B = true;
                }

                byte outputMode = dataMemory.readByte(DataMemory_ATmega328P.TCCR1A_ADDR);

                //CHANEL A
                switch (0xC0 & outputMode) {
                    case 0x00:
                    case 0x40:
                        //OC1A disconected
                        timerOutputControl_OC1A = false;
                        break;
                    case 0x80:
                        //OC1A Clear on Compare Match counting up, OC1A Set on Compare Match counting down
                        timerOutputControl_OC1A = true;
                        if (doubleBufferOCR1A == MAX_9B) {
                            stateOC1A = IOModule.HIGH_LEVEL;
                            ioModule.setOC1A(stateOC1A, UCModule_View.simulatedTime);
                        } else if (doubleBufferOCR1A == BOTTOM) {
                            stateOC1A = IOModule.LOW_LEVEL;
                            ioModule.setOC1A(stateOC1A, UCModule_View.simulatedTime);
                        } else {
                            if (match_A) {
                                if (phaseCorrect_UPCount_old) {
                                    stateOC1A = IOModule.LOW_LEVEL;
                                } else {
                                    stateOC1A = IOModule.HIGH_LEVEL;
                                }
                                ioModule.setOC1A(stateOC1A, UCModule_View.simulatedTime);
                            }
                        }
                        break;
                    case 0xC0:
                        //OC1A Set on Compare Match counting up, OC1A Clear on Compare Match counting down
                        timerOutputControl_OC1A = true;
                        if (doubleBufferOCR1A == MAX_9B) {
                            stateOC1A = IOModule.LOW_LEVEL;
                            ioModule.setOC1A(stateOC1A, UCModule_View.simulatedTime);
                        } else if (doubleBufferOCR1A == BOTTOM) {
                            stateOC1A = IOModule.HIGH_LEVEL;
                            ioModule.setOC1A(stateOC1A, UCModule_View.simulatedTime);
                        } else {
                            if (match_A) {
                                if (phaseCorrect_UPCount_old) {
                                    stateOC1A = IOModule.HIGH_LEVEL;
                                } else {
                                    stateOC1A = IOModule.LOW_LEVEL;
                                }
                                ioModule.setOC1A(stateOC1A, UCModule_View.simulatedTime);
                            }
                        }
                        break;

                    default:
                        break;
                }

                //CHANEL B
                switch (0x30 & outputMode) {
                    case 0x00:
                    case 0x10:
                        //OC1B disconected
                        timerOutputControl_OC1B = false;
                        break;
                    case 0x20:
                        //OC1B Clear on Compare Match counting up, OC1B Set on Compare Match counting down
                        timerOutputControl_OC1B = true;
                        if (doubleBufferOCR1B == MAX_9B) {
                            stateOC1B = IOModule.HIGH_LEVEL;
                            ioModule.setOC1B(stateOC1B, UCModule_View.simulatedTime);
                        } else if (doubleBufferOCR1B == BOTTOM) {
                            stateOC1B = IOModule.LOW_LEVEL;
                            ioModule.setOC1B(stateOC1B, UCModule_View.simulatedTime);
                        } else {
                            if (match_B) {
                                if (phaseCorrect_UPCount_old) {
                                    stateOC1B = IOModule.LOW_LEVEL;
                                } else {
                                    stateOC1B = IOModule.HIGH_LEVEL;
                                }
                                ioModule.setOC1B(stateOC1B, UCModule_View.simulatedTime);
                            }
                        }
                        break;
                    case 0x30:
                        //OC1B Set on Compare Match counting up, OC1B Clear on Compare Match counting down
                        timerOutputControl_OC1B = true;
                        if (doubleBufferOCR1B == MAX_9B) {
                            stateOC1B = IOModule.LOW_LEVEL;
                            ioModule.setOC1B(stateOC1B, UCModule_View.simulatedTime);
                        } else if (doubleBufferOCR1B == BOTTOM) {
                            stateOC1B = IOModule.HIGH_LEVEL;
                            ioModule.setOC1B(stateOC1B, UCModule_View.simulatedTime);
                        } else {
                            if (match_B) {
                                if (phaseCorrect_UPCount_old) {
                                    stateOC1B = IOModule.HIGH_LEVEL;
                                } else {
                                    stateOC1B = IOModule.LOW_LEVEL;
                                }
                                ioModule.setOC1B(stateOC1B, UCModule_View.simulatedTime);
                            }
                        }
                        break;

                    default:
                        break;
                }

                dataMemory.write16bits(DataMemory_ATmega328P.TCNT1L_ADDR, DataMemory_ATmega328P.TCNT1H_ADDR,
                        (byte) (0x00FF & progress), (byte) (0x00FF & (progress >> 8)));
            }
        },
        PWM_PHASE_CORRECT_10B {
            @Override
            public void count() {
                enableICRWrite = false;
                if (!dataMemory.isOCR1AReady()) {
                    return;
                }

                match_A = false; match_B = false;
                progress = dataMemory.read16bits(DataMemory_ATmega328P.TCNT1L_ADDR, DataMemory_ATmega328P.TCNT1H_ADDR);

                if (progress == MAX_10B) {
                    doubleBufferOCR1A = dataMemory.read16bits(DataMemory_ATmega328P.OCR1AL_ADDR, DataMemory_ATmega328P.OCR1AH_ADDR);

                    doubleBufferOCR1B = dataMemory.read16bits(DataMemory_ATmega328P.OCR1BL_ADDR, DataMemory_ATmega328P.OCR1BH_ADDR);
                }

                if (phaseCorrect_UPCount) {
                    progress += 1;
                } else {
                    progress -= 1;
                }

                phaseCorrect_UPCount_old = phaseCorrect_UPCount;

                if (progress == BOTTOM) {
                    UCModule.interruptionModule.timer1Overflow();
                    phaseCorrect_UPCount = true;
                } else if (progress == MAX_10B) {
                    phaseCorrect_UPCount = false;
                }

                if (progress == doubleBufferOCR1A) {
                    UCModule.interruptionModule.timer1MatchA();
                    match_A = true;
                }
                if (progress == doubleBufferOCR1B) {
                    UCModule.interruptionModule.timer1MatchB();
                    match_B = true;
                }

                byte outputMode = dataMemory.readByte(DataMemory_ATmega328P.TCCR1A_ADDR);

                //CHANEL A
                switch (0xC0 & outputMode) {
                    case 0x00:
                    case 0x40:
                        //OC1A disconected
                        timerOutputControl_OC1A = false;
                        break;
                    case 0x80:
                        //OC1A Clear on Compare Match counting up, OC1A Set on Compare Match counting down
                        timerOutputControl_OC1A = true;
                        if (doubleBufferOCR1A == MAX_10B) {
                            stateOC1A = IOModule.HIGH_LEVEL;
                            ioModule.setOC1A(stateOC1A, UCModule_View.simulatedTime);
                        } else if (doubleBufferOCR1A == BOTTOM) {
                            stateOC1A = IOModule.LOW_LEVEL;
                            ioModule.setOC1A(stateOC1A, UCModule_View.simulatedTime);
                        } else {
                            if (match_A) {
                                if (phaseCorrect_UPCount_old) {
                                    stateOC1A = IOModule.LOW_LEVEL;
                                } else {
                                    stateOC1A = IOModule.HIGH_LEVEL;
                                }
                                ioModule.setOC1A(stateOC1A, UCModule_View.simulatedTime);
                            }
                        }
                        break;
                    case 0xC0:
                        //OC1A Set on Compare Match counting up, OC1A Clear on Compare Match counting down
                        timerOutputControl_OC1A = true;
                        if (doubleBufferOCR1A == MAX_10B) {
                            stateOC1A = IOModule.LOW_LEVEL;
                            ioModule.setOC1A(stateOC1A, UCModule_View.simulatedTime);
                        } else if (doubleBufferOCR1A == BOTTOM) {
                            stateOC1A = IOModule.HIGH_LEVEL;
                            ioModule.setOC1A(stateOC1A, UCModule_View.simulatedTime);
                        } else {
                            if (match_A) {
                                if (phaseCorrect_UPCount_old) {
                                    stateOC1A = IOModule.HIGH_LEVEL;
                                } else {
                                    stateOC1A = IOModule.LOW_LEVEL;
                                }
                                ioModule.setOC1A(stateOC1A, UCModule_View.simulatedTime);
                            }
                        }
                        break;

                    default:
                        break;
                }

                //CHANEL B
                switch (0x30 & outputMode) {
                    case 0x00:
                    case 0x10:
                        //OC1B disconected
                        timerOutputControl_OC1B = false;
                        break;
                    case 0x20:
                        //OC1B Clear on Compare Match counting up, OC1B Set on Compare Match counting down
                        timerOutputControl_OC1B = true;
                        if (doubleBufferOCR1B == MAX_10B) {
                            stateOC1B = IOModule.HIGH_LEVEL;
                            ioModule.setOC1B(stateOC1B, UCModule_View.simulatedTime);
                        } else if (doubleBufferOCR1B == BOTTOM) {
                            stateOC1B = IOModule.LOW_LEVEL;
                            ioModule.setOC1B(stateOC1B, UCModule_View.simulatedTime);
                        } else {
                            if (match_B) {
                                if (phaseCorrect_UPCount_old) {
                                    stateOC1B = IOModule.LOW_LEVEL;
                                } else {
                                    stateOC1B = IOModule.HIGH_LEVEL;
                                }
                                ioModule.setOC1B(stateOC1B, UCModule_View.simulatedTime);
                            }
                        }
                        break;
                    case 0x30:
                        //OC1B Set on Compare Match counting up, OC1B Clear on Compare Match counting down
                        timerOutputControl_OC1B = true;
                        if (doubleBufferOCR1B == MAX_10B) {
                            stateOC1B = IOModule.LOW_LEVEL;
                            ioModule.setOC1B(stateOC1B, UCModule_View.simulatedTime);
                        } else if (doubleBufferOCR1B == BOTTOM) {
                            stateOC1B = IOModule.HIGH_LEVEL;
                            ioModule.setOC1B(stateOC1B, UCModule_View.simulatedTime);
                        } else {
                            if (match_B) {
                                if (phaseCorrect_UPCount_old) {
                                    stateOC1B = IOModule.HIGH_LEVEL;
                                } else {
                                    stateOC1B = IOModule.LOW_LEVEL;
                                }
                                ioModule.setOC1B(stateOC1B, UCModule_View.simulatedTime);
                            }
                        }
                        break;

                    default:
                        break;
                }

                dataMemory.write16bits(DataMemory_ATmega328P.TCNT1L_ADDR, DataMemory_ATmega328P.TCNT1H_ADDR,
                        (byte) (0x00FF & progress), (byte) (0x00FF & (progress >> 8)));
            }
        },
        PWM_PHASE_CORRECT_TOP_ICR1 {
            @Override
            public void count() {
                enableICRWrite = true;
                if (!dataMemory.isOCR1AReady()) {
                    return;
                }

                match_A = false; match_B = false;
                progress = dataMemory.read16bits(DataMemory_ATmega328P.TCNT1L_ADDR, DataMemory_ATmega328P.TCNT1H_ADDR);

                icr1 = dataMemory.read16bits(DataMemory_ATmega328P.ICR1L_ADDR, DataMemory_ATmega328P.ICR1H_ADDR);

                if (progress == icr1) {
                    doubleBufferOCR1A = dataMemory.read16bits(DataMemory_ATmega328P.OCR1AL_ADDR, DataMemory_ATmega328P.OCR1AH_ADDR);

                    doubleBufferOCR1B = dataMemory.read16bits(DataMemory_ATmega328P.OCR1BL_ADDR, DataMemory_ATmega328P.OCR1BH_ADDR);
                }

                if (phaseCorrect_UPCount) {
                    progress += 1;
                } else {
                    progress -= 1;
                }

                phaseCorrect_UPCount_old = phaseCorrect_UPCount;

                if (progress == BOTTOM) {
                    UCModule.interruptionModule.timer1Overflow();
                    phaseCorrect_UPCount = true;
                } else if (progress == icr1) {
                    UCModule.interruptionModule.timer1InputCapture();
                    phaseCorrect_UPCount = false;
                }

                if (progress == doubleBufferOCR1A) {
                    UCModule.interruptionModule.timer1MatchA();
                    match_A = true;
                }
                if (progress == doubleBufferOCR1B) {
                    UCModule.interruptionModule.timer1MatchB();
                    match_B = true;
                }

                byte outputMode = dataMemory.readByte(DataMemory_ATmega328P.TCCR1A_ADDR);

                //CHANEL A
                switch (0xC0 & outputMode) {
                    case 0x00:
                        //OC1A disconected
                        timerOutputControl_OC1A = false;
                        break;
                    case 0x40:
                        timerOutputControl_OC1A = true;
                        if (match_A) {
                            stateOC1A = (stateOC1A + 1) % 2;  //Toggle
                            ioModule.setOC1A(stateOC1A, UCModule_View.simulatedTime);
                        }
                        break;
                    case 0x80:
                        //OC1A Clear on Compare Match counting up, OC1A Set on Compare Match counting down
                        timerOutputControl_OC1A = true;
                        if (doubleBufferOCR1A == icr1) {
                            stateOC1A = IOModule.HIGH_LEVEL;
                            ioModule.setOC1A(stateOC1A, UCModule_View.simulatedTime);
                        } else if (doubleBufferOCR1A == BOTTOM) {
                            stateOC1A = IOModule.LOW_LEVEL;
                            ioModule.setOC1A(stateOC1A, UCModule_View.simulatedTime);
                        } else {
                            if (match_A) {
                                if (phaseCorrect_UPCount_old) {
                                    stateOC1A = IOModule.LOW_LEVEL;
                                } else {
                                    stateOC1A = IOModule.HIGH_LEVEL;
                                }
                                ioModule.setOC1A(stateOC1A, UCModule_View.simulatedTime);
                            }
                        }
                        break;
                    case 0xC0:
                        //OC1A Set on Compare Match counting up, OC1A Clear on Compare Match counting down
                        timerOutputControl_OC1A = true;
                        if (doubleBufferOCR1A == icr1) {
                            stateOC1A = IOModule.LOW_LEVEL;
                            ioModule.setOC1A(stateOC1A, UCModule_View.simulatedTime);
                        } else if (doubleBufferOCR1A == BOTTOM) {
                            stateOC1A = IOModule.HIGH_LEVEL;
                            ioModule.setOC1A(stateOC1A, UCModule_View.simulatedTime);
                        } else {
                            if (match_A) {
                                if (phaseCorrect_UPCount_old) {
                                    stateOC1A = IOModule.HIGH_LEVEL;
                                } else {
                                    stateOC1A = IOModule.LOW_LEVEL;
                                }
                                ioModule.setOC1A(stateOC1A, UCModule_View.simulatedTime);
                            }
                        }
                        break;

                    default:
                        break;
                }

                //CHANEL B
                switch (0x30 & outputMode) {
                    case 0x00:
                    case 0x10:
                        //OC1B disconected
                        timerOutputControl_OC1B = false;
                        break;
                    case 0x20:
                        //OC1B Clear on Compare Match counting up, OC1B Set on Compare Match counting down
                        timerOutputControl_OC1B = true;
                        if (doubleBufferOCR1B == icr1) {
                            stateOC1B = IOModule.HIGH_LEVEL;
                            ioModule.setOC1B(stateOC1B, UCModule_View.simulatedTime);
                        } else if (doubleBufferOCR1B == BOTTOM) {
                            stateOC1B = IOModule.LOW_LEVEL;
                            ioModule.setOC1B(stateOC1B, UCModule_View.simulatedTime);
                        } else {
                            if (match_B) {
                                if (phaseCorrect_UPCount_old) {
                                    stateOC1B = IOModule.LOW_LEVEL;
                                } else {
                                    stateOC1B = IOModule.HIGH_LEVEL;
                                }
                                ioModule.setOC1B(stateOC1B, UCModule_View.simulatedTime);
                            }
                        }
                        break;
                    case 0x30:
                        //OC1B Set on Compare Match counting up, OC1B Clear on Compare Match counting down
                        timerOutputControl_OC1B = true;
                        if (doubleBufferOCR1B == icr1) {
                            stateOC1B = IOModule.LOW_LEVEL;
                            ioModule.setOC1B(stateOC1B, UCModule_View.simulatedTime);
                        } else if (doubleBufferOCR1B == BOTTOM) {
                            stateOC1B = IOModule.HIGH_LEVEL;
                            ioModule.setOC1B(stateOC1B, UCModule_View.simulatedTime);
                        } else {
                            if (match_B) {
                                if (phaseCorrect_UPCount_old) {
                                    stateOC1B = IOModule.HIGH_LEVEL;
                                } else {
                                    stateOC1B = IOModule.LOW_LEVEL;
                                }
                                ioModule.setOC1B(stateOC1B, UCModule_View.simulatedTime);
                            }
                        }
                        break;

                    default:
                        break;
                }

                dataMemory.write16bits(DataMemory_ATmega328P.TCNT1L_ADDR, DataMemory_ATmega328P.TCNT1H_ADDR,
                        (byte) (0x00FF & progress), (byte) (0x00FF & (progress >> 8)));
            }
        },
        PWM_PHASE_CORRECT_TOP_OCRA {
            @Override
            public void count() {
                enableICRWrite = false;
                if (!dataMemory.isOCR1AReady()) {
                    return;
                }

                match_A = false; match_B = false;
                progress = dataMemory.read16bits(DataMemory_ATmega328P.TCNT1L_ADDR, DataMemory_ATmega328P.TCNT1H_ADDR);

                if (progress == doubleBufferOCR1A) {
                    doubleBufferOCR1A = dataMemory.read16bits(DataMemory_ATmega328P.OCR1AL_ADDR, DataMemory_ATmega328P.OCR1AH_ADDR);

                    doubleBufferOCR1B = dataMemory.read16bits(DataMemory_ATmega328P.OCR1BL_ADDR, DataMemory_ATmega328P.OCR1BH_ADDR);
                }

                if (phaseCorrect_UPCount) {
                    progress += 1;
                } else {
                    progress -= 1;
                }

                phaseCorrect_UPCount_old = phaseCorrect_UPCount;

                if (progress == BOTTOM) {
                    UCModule.interruptionModule.timer1Overflow();
                    phaseCorrect_UPCount = true;
                } else if (progress == doubleBufferOCR1A) {
                    phaseCorrect_UPCount = false;
                }

                if (progress == doubleBufferOCR1A) {
                    UCModule.interruptionModule.timer1MatchA();
                    match_A = true;
                }
                if (progress == doubleBufferOCR1B) {
                    UCModule.interruptionModule.timer1MatchB();
                    match_B = true;
                }

                byte outputMode = dataMemory.readByte(DataMemory_ATmega328P.TCCR1A_ADDR);

                //CHANEL A
                switch (0xC0 & outputMode) {
                    case 0x00:
                        //OC1A disconected
                        timerOutputControl_OC1A = false;
                        break;
                    case 0x40:
                        timerOutputControl_OC1A = true;
                        if (match_A) {
                            stateOC1A = (stateOC1A + 1) % 2;  //Toggle
                            ioModule.setOC1A(stateOC1A, UCModule_View.simulatedTime);
                        }
                        break;
                    case 0x80:
                        //OC1A Clear on Compare Match counting up, OC1A Set on Compare Match counting down
                        timerOutputControl_OC1A = true;
                        if (doubleBufferOCR1A == MAX) {
                            stateOC1A = IOModule.HIGH_LEVEL;
                            ioModule.setOC1A(stateOC1A, UCModule_View.simulatedTime);
                        } else if (doubleBufferOCR1A == BOTTOM) {
                            stateOC1A = IOModule.LOW_LEVEL;
                            ioModule.setOC1A(stateOC1A, UCModule_View.simulatedTime);
                        } else {
                            if (match_A) {
                                if (phaseCorrect_UPCount_old) {
                                    stateOC1A = IOModule.LOW_LEVEL;
                                } else {
                                    stateOC1A = IOModule.HIGH_LEVEL;
                                }
                                ioModule.setOC1A(stateOC1A, UCModule_View.simulatedTime);
                            }
                        }
                        break;
                    case 0xC0:
                        //OC1A Set on Compare Match counting up, OC1A Clear on Compare Match counting down
                        timerOutputControl_OC1A = true;
                        if (doubleBufferOCR1A == MAX) {
                            stateOC1A = IOModule.LOW_LEVEL;
                            ioModule.setOC1A(stateOC1A, UCModule_View.simulatedTime);
                        } else if (doubleBufferOCR1A == BOTTOM) {
                            stateOC1A = IOModule.HIGH_LEVEL;
                            ioModule.setOC1A(stateOC1A, UCModule_View.simulatedTime);
                        } else {
                            if (match_A) {
                                if (phaseCorrect_UPCount_old) {
                                    stateOC1A = IOModule.HIGH_LEVEL;
                                } else {
                                    stateOC1A = IOModule.LOW_LEVEL;
                                }
                                ioModule.setOC1A(stateOC1A, UCModule_View.simulatedTime);
                            }
                        }
                        break;

                    default:
                        break;
                }

                //CHANEL B
                switch (0x30 & outputMode) {
                    case 0x00:
                    case 0x10:
                        //OC1B disconected
                        timerOutputControl_OC1B = false;
                        break;
                    case 0x20:
                        //OC1B Clear on Compare Match counting up, OC1B Set on Compare Match counting down
                        timerOutputControl_OC1B = true;
                        if (doubleBufferOCR1B == doubleBufferOCR1A) {
                            stateOC1B = IOModule.HIGH_LEVEL;
                            ioModule.setOC1B(stateOC1B, UCModule_View.simulatedTime);
                        } else if (doubleBufferOCR1B == BOTTOM) {
                            stateOC1B = IOModule.LOW_LEVEL;
                            ioModule.setOC1B(stateOC1B, UCModule_View.simulatedTime);
                        } else {
                            if (match_B) {
                                if (phaseCorrect_UPCount_old) {
                                    stateOC1B = IOModule.LOW_LEVEL;
                                } else {
                                    stateOC1B = IOModule.HIGH_LEVEL;
                                }
                                ioModule.setOC1B(stateOC1B, UCModule_View.simulatedTime);
                            }
                        }
                        break;
                    case 0x30:
                        //OC1B Set on Compare Match counting up, OC1B Clear on Compare Match counting down
                        timerOutputControl_OC1B = true;
                        if (doubleBufferOCR1B == doubleBufferOCR1A) {
                            stateOC1B = IOModule.LOW_LEVEL;
                            ioModule.setOC1B(stateOC1B, UCModule_View.simulatedTime);
                        } else if (doubleBufferOCR1B == BOTTOM) {
                            stateOC1B = IOModule.HIGH_LEVEL;
                            ioModule.setOC1B(stateOC1B, UCModule_View.simulatedTime);
                        } else {
                            if (match_B) {
                                if (phaseCorrect_UPCount_old) {
                                    stateOC1B = IOModule.HIGH_LEVEL;
                                } else {
                                    stateOC1B = IOModule.LOW_LEVEL;
                                }
                                ioModule.setOC1B(stateOC1B, UCModule_View.simulatedTime);
                            }
                        }
                        break;

                    default:
                        break;
                }

                dataMemory.write16bits(DataMemory_ATmega328P.TCNT1L_ADDR, DataMemory_ATmega328P.TCNT1H_ADDR,
                        (byte) (0x00FF & progress), (byte) (0x00FF & (progress >> 8)));
            }
        },
        PWM_PHASE_AND_FREQUENCY_CORRECT_TOP_ICR1 {
            @Override
            public void count() {
                enableICRWrite = true;
                if (!dataMemory.isOCR1AReady()) {
                    return;
                }

                match_A = false; match_B = false;
                progress = dataMemory.read16bits(DataMemory_ATmega328P.TCNT1L_ADDR, DataMemory_ATmega328P.TCNT1H_ADDR);

                icr1 = dataMemory.read16bits(DataMemory_ATmega328P.ICR1L_ADDR, DataMemory_ATmega328P.ICR1H_ADDR);

                if (progress == BOTTOM) {
                    doubleBufferOCR1A = dataMemory.read16bits(DataMemory_ATmega328P.OCR1AL_ADDR, DataMemory_ATmega328P.OCR1AH_ADDR);

                    doubleBufferOCR1B = dataMemory.read16bits(DataMemory_ATmega328P.OCR1BL_ADDR, DataMemory_ATmega328P.OCR1BH_ADDR);
                }

                if (phaseCorrect_UPCount) {
                    progress += 1;
                } else {
                    progress -= 1;
                }

                phaseCorrect_UPCount_old = phaseCorrect_UPCount;

                if (progress == BOTTOM) {
                    UCModule.interruptionModule.timer1Overflow();
                    phaseCorrect_UPCount = true;
                } else if (progress == icr1) {
                    UCModule.interruptionModule.timer1InputCapture();
                    phaseCorrect_UPCount = false;
                }

                if (progress == doubleBufferOCR1A) {
                    UCModule.interruptionModule.timer1MatchA();
                    match_A = true;
                }
                if (progress == doubleBufferOCR1B) {
                    UCModule.interruptionModule.timer1MatchB();
                    match_B = true;
                }

                byte outputMode = dataMemory.readByte(DataMemory_ATmega328P.TCCR1A_ADDR);

                //CHANEL A
                switch (0xC0 & outputMode) {
                    case 0x00:
                    case 0x40:
                        //OC1A disconected
                        timerOutputControl_OC1A = false;
                        break;
//                    case 0x40:
//                        timerOutputControl_OC1A = true;
//                        if (match_A) {
//                            stateOC1A = (stateOC1A + 1) % 2;  //Toggle
//                            ioModule.setOC1A(stateOC1A, UCModule_View.simulatedTime);
//                        }
//                        break;
                    case 0x80:
                        //OC1A Clear on Compare Match counting up, OC1A Set on Compare Match counting down
                        timerOutputControl_OC1A = true;
                        if (doubleBufferOCR1A == icr1) {
                            stateOC1A = IOModule.HIGH_LEVEL;
                            ioModule.setOC1A(stateOC1A, UCModule_View.simulatedTime);
                        } else if (doubleBufferOCR1A == BOTTOM) {
                            stateOC1A = IOModule.LOW_LEVEL;
                            ioModule.setOC1A(stateOC1A, UCModule_View.simulatedTime);
                        } else {
                            if (match_A) {
                                if (phaseCorrect_UPCount_old) {
                                    stateOC1A = IOModule.LOW_LEVEL;
                                } else {
                                    stateOC1A = IOModule.HIGH_LEVEL;
                                }
                                ioModule.setOC1A(stateOC1A, UCModule_View.simulatedTime);
                            }
                        }
                        break;
                    case 0xC0:
                        //OC1A Set on Compare Match counting up, OC1A Clear on Compare Match counting down
                        timerOutputControl_OC1A = true;
                        if (doubleBufferOCR1A == icr1) {
                            stateOC1A = IOModule.LOW_LEVEL;
                            ioModule.setOC1A(stateOC1A, UCModule_View.simulatedTime);
                        } else if (doubleBufferOCR1A == BOTTOM) {
                            stateOC1A = IOModule.HIGH_LEVEL;
                            ioModule.setOC1A(stateOC1A, UCModule_View.simulatedTime);
                        } else {
                            if (match_A) {
                                if (phaseCorrect_UPCount_old) {
                                    stateOC1A = IOModule.HIGH_LEVEL;
                                } else {
                                    stateOC1A = IOModule.LOW_LEVEL;
                                }
                                ioModule.setOC1A(stateOC1A, UCModule_View.simulatedTime);
                            }
                        }
                        break;

                    default:
                        break;
                }

                //CHANEL B
                switch (0x30 & outputMode) {
                    case 0x00:
                    case 0x10:
                        //OC1B disconected
                        timerOutputControl_OC1B = false;
                        break;
                    case 0x20:
                        //OC1B Clear on Compare Match counting up, OC1B Set on Compare Match counting down
                        timerOutputControl_OC1B = true;
                        if (doubleBufferOCR1B == icr1) {
                            stateOC1B = IOModule.HIGH_LEVEL;
                            ioModule.setOC1B(stateOC1B, UCModule_View.simulatedTime);
                        } else if (doubleBufferOCR1B == BOTTOM) {
                            stateOC1B = IOModule.LOW_LEVEL;
                            ioModule.setOC1B(stateOC1B, UCModule_View.simulatedTime);
                        } else {
                            if (match_B) {
                                if (phaseCorrect_UPCount_old) {
                                    stateOC1B = IOModule.LOW_LEVEL;
                                } else {
                                    stateOC1B = IOModule.HIGH_LEVEL;
                                }
                                ioModule.setOC1B(stateOC1B, UCModule_View.simulatedTime);
                            }
                        }
                        break;
                    case 0x30:
                        //OC1B Set on Compare Match counting up, OC1B Clear on Compare Match counting down
                        timerOutputControl_OC1B = true;
                        if (doubleBufferOCR1B == icr1) {
                            stateOC1B = IOModule.LOW_LEVEL;
                            ioModule.setOC1B(stateOC1B, UCModule_View.simulatedTime);
                        } else if (doubleBufferOCR1B == BOTTOM) {
                            stateOC1B = IOModule.HIGH_LEVEL;
                            ioModule.setOC1B(stateOC1B, UCModule_View.simulatedTime);
                        } else {
                            if (match_B) {
                                if (phaseCorrect_UPCount_old) {
                                    stateOC1B = IOModule.HIGH_LEVEL;
                                } else {
                                    stateOC1B = IOModule.LOW_LEVEL;
                                }
                                ioModule.setOC1B(stateOC1B, UCModule_View.simulatedTime);
                            }
                        }
                        break;

                    default:
                        break;
                }

                dataMemory.write16bits(DataMemory_ATmega328P.TCNT1L_ADDR, DataMemory_ATmega328P.TCNT1H_ADDR,
                        (byte) (0x00FF & progress), (byte) (0x00FF & (progress >> 8)));
            }
        },
        PWM_PHASE_AND_FREQUENCY_CORRECT_TOP_OCRA {
            @Override
            public void count() {
                enableICRWrite = false;
                if (!dataMemory.isOCR1AReady()) {
                    return;
                }

                match_A = false; match_B = false;
                progress = dataMemory.read16bits(DataMemory_ATmega328P.TCNT1L_ADDR, DataMemory_ATmega328P.TCNT1H_ADDR);

                if (progress == BOTTOM) {
                    doubleBufferOCR1A = dataMemory.read16bits(DataMemory_ATmega328P.OCR1AL_ADDR, DataMemory_ATmega328P.OCR1AH_ADDR);

                    doubleBufferOCR1B = dataMemory.read16bits(DataMemory_ATmega328P.OCR1BL_ADDR, DataMemory_ATmega328P.OCR1BH_ADDR);
                }

                if (phaseCorrect_UPCount) {
                    progress += 1;
                } else {
                    progress -= 1;
                }

                phaseCorrect_UPCount_old = phaseCorrect_UPCount;

                if (progress == BOTTOM) {
                    UCModule.interruptionModule.timer1Overflow();
                    phaseCorrect_UPCount = true;
                } else if (progress == doubleBufferOCR1A) {
                    phaseCorrect_UPCount = false;
                }

                if (progress == doubleBufferOCR1A) {
                    UCModule.interruptionModule.timer1MatchA();
                    match_A = true;
                }
                if (progress == doubleBufferOCR1B) {
                    UCModule.interruptionModule.timer1MatchB();
                    match_B = true;
                }

                byte outputMode = dataMemory.readByte(DataMemory_ATmega328P.TCCR1A_ADDR);

                //CHANEL A
                switch (0xC0 & outputMode) {
                    case 0x00:
                        //OC1A disconected
                        timerOutputControl_OC1A = false;
                        break;
                    case 0x40:
                        timerOutputControl_OC1A = true;
                        if (match_A) {
                            stateOC1A = (stateOC1A + 1) % 2;  //Toggle
                            ioModule.setOC1A(stateOC1A, UCModule_View.simulatedTime);
                        }
                        break;
                    case 0x80:
                        //OC1A Clear on Compare Match counting up, OC1A Set on Compare Match counting down
                        timerOutputControl_OC1A = true;
                        if (doubleBufferOCR1A == MAX) {
                            stateOC1A = IOModule.HIGH_LEVEL;
                            ioModule.setOC1A(stateOC1A, UCModule_View.simulatedTime);
                        } else if (doubleBufferOCR1A == BOTTOM) {
                            stateOC1A = IOModule.LOW_LEVEL;
                            ioModule.setOC1A(stateOC1A, UCModule_View.simulatedTime);
                        } else {
                            if (match_A) {
                                if (phaseCorrect_UPCount_old) {
                                    stateOC1A = IOModule.LOW_LEVEL;
                                } else {
                                    stateOC1A = IOModule.HIGH_LEVEL;
                                }
                                ioModule.setOC1A(stateOC1A, UCModule_View.simulatedTime);
                            }
                        }
                        break;
                    case 0xC0:
                        //OC1A Set on Compare Match counting up, OC1A Clear on Compare Match counting down
                        timerOutputControl_OC1A = true;
                        if (doubleBufferOCR1A == MAX) {
                            stateOC1A = IOModule.LOW_LEVEL;
                            ioModule.setOC1A(stateOC1A, UCModule_View.simulatedTime);
                        } else if (doubleBufferOCR1A == BOTTOM) {
                            stateOC1A = IOModule.HIGH_LEVEL;
                            ioModule.setOC1A(stateOC1A, UCModule_View.simulatedTime);
                        } else {
                            if (match_A) {
                                if (phaseCorrect_UPCount_old) {
                                    stateOC1A = IOModule.HIGH_LEVEL;
                                } else {
                                    stateOC1A = IOModule.LOW_LEVEL;
                                }
                                ioModule.setOC1A(stateOC1A, UCModule_View.simulatedTime);
                            }
                        }
                        break;

                    default:
                        break;
                }

                //CHANEL B
                switch (0x30 & outputMode) {
                    case 0x00:
                    case 0x10:
                        //OC1B disconected
                        timerOutputControl_OC1B = false;
                        break;
                    case 0x20:
                        //OC1B Clear on Compare Match counting up, OC1B Set on Compare Match counting down
                        timerOutputControl_OC1B = true;
                        if (doubleBufferOCR1B == doubleBufferOCR1A) {
                            stateOC1B = IOModule.HIGH_LEVEL;
                            ioModule.setOC1B(stateOC1B, UCModule_View.simulatedTime);
                        } else if (doubleBufferOCR1B == BOTTOM) {
                            stateOC1B = IOModule.LOW_LEVEL;
                            ioModule.setOC1B(stateOC1B, UCModule_View.simulatedTime);
                        } else {
                            if (match_B) {
                                if (phaseCorrect_UPCount_old) {
                                    stateOC1B = IOModule.LOW_LEVEL;
                                } else {
                                    stateOC1B = IOModule.HIGH_LEVEL;
                                }
                                ioModule.setOC1B(stateOC1B, UCModule_View.simulatedTime);
                            }
                        }
                        break;
                    case 0x30:
                        //OC1B Set on Compare Match counting up, OC1B Clear on Compare Match counting down
                        timerOutputControl_OC1B = true;
                        if (doubleBufferOCR1B == doubleBufferOCR1A) {
                            stateOC1B = IOModule.LOW_LEVEL;
                            ioModule.setOC1B(stateOC1B, UCModule_View.simulatedTime);
                        } else if (doubleBufferOCR1B == BOTTOM) {
                            stateOC1B = IOModule.HIGH_LEVEL;
                            ioModule.setOC1B(stateOC1B, UCModule_View.simulatedTime);
                        } else {
                            if (match_B) {
                                if (phaseCorrect_UPCount_old) {
                                    stateOC1B = IOModule.HIGH_LEVEL;
                                } else {
                                    stateOC1B = IOModule.LOW_LEVEL;
                                }
                                ioModule.setOC1B(stateOC1B, UCModule_View.simulatedTime);
                            }
                        }
                        break;

                    default:
                        break;
                }

                dataMemory.write16bits(DataMemory_ATmega328P.TCNT1L_ADDR, DataMemory_ATmega328P.TCNT1H_ADDR,
                        (byte) (0x00FF & progress), (byte) (0x00FF & (progress >> 8)));
            }
        };

        public abstract void count();
    }
}
