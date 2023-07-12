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

package com.kollins.project.sofia.atmega328p.iomodule_atmega328p.input;

/*import android.graphics.Color;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.kollins.project.sofia.R;*/
import com.kollins.project.sofia.UCModule;
import com.kollins.project.sofia.atmega328p.ADC_ATmega328P;
import com.kollins.project.sofia.ucinterfaces.IOModule;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Random;

/**
 * Created by kollins on 3/21/18.
 */

public class InputAdapter_ATmega328P  { //extends BaseAdapter {

    private String[] pinArray;
    private String[] pinModeArray;

    private int sourcePower;

    private InputFragment_ATmega328P inputFragment;
    private List<InputPin_ATmega328P> inputPins;

    private Random randomGenerator;
    private DecimalFormat decimalFormat = new DecimalFormat("0.00V", DecimalFormatSymbols.getInstance());

    public InputAdapter_ATmega328P(InputFragment_ATmega328P inputFragment, List<InputPin_ATmega328P> inputPins) {
        this.inputFragment = inputFragment;
        this.inputPins = inputPins;

        pinArray = UCModule.getPinArrayWithHint();
        pinModeArray = UCModule.getPinModeArray();
        sourcePower = UCModule.getSourcePower();

        randomGenerator = new Random();
    }
/*
    @Override
    public int getCount() {
        return inputPins != null ? inputPins.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return inputPins.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        return inputPins.get(position).getDescription() ? 1 : 0;
    }
    */
/*
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View view = convertView;
        final ViewHolderInput_ATmega328P holder;
        final ListView listView = (ListView) parent;

        final InputPin_ATmega328P pin = inputPins.get(position);

        if (pin.getDescription()) {
            ///////////////////////////////DIGITAL PIN/////////////////////////////////////////

            final HintAdapter pinSpinnerAdapter =
                    new HintAdapter(inputFragment.getContext(), android.R.layout.simple_spinner_item, pinArray);
            ArrayAdapter pinSpinnerModeAdapter =
                    new ArrayAdapter(inputFragment.getContext(), android.R.layout.simple_spinner_item, pinModeArray);

            if (convertView == null) {
                view = LayoutInflater.from(inputFragment.getContext()).inflate(R.layout.digital_input_pin,
                        parent, false);

                Spinner pinSpinner = (Spinner) view.findViewById(R.id.pinSelectorDigitalInput);
                Spinner pinModeSpinner = (Spinner) view.findViewById(R.id.pinModeSelector);
                Button pushButton = (Button) view.findViewById(R.id.digitalPushButton);
                ImageView inputPinState = (ImageView) view.findViewById(R.id.digitalInputState);

                holder = new ViewHolderInput_ATmega328P(pinSpinner, pinModeSpinner, pushButton, inputPinState);
                view.setTag(holder);
            } else {
                holder = (ViewHolderInput_ATmega328P) view.getTag();
            }

            pinSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            pinSpinnerModeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            holder.pinModeSpinner.setAdapter(pinSpinnerModeAdapter);
            holder.pinSpinner.setAdapter(pinSpinnerAdapter);

            if (pin.getPinSpinnerPosition() < 0) {
                holder.pinSpinner.setSelection(pinSpinnerAdapter.getCount());
            } else {
                holder.pinSpinner.setSelection(pin.getPinSpinnerPosition());
            }

            if (pin.getPinModePosition() > 0) {
                holder.pinModeSpinner.setSelection(pin.getPinModePosition());
            }

            if (listView.getChoiceMode() != ListView.CHOICE_MODE_MULTIPLE) {
                holder.inputPinState.setBackgroundResource(R.drawable.digital_input_undefined);
            }

            holder.pinSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int positionSpinner, long id) {
                    if (positionSpinner == pinSpinnerAdapter.getCount()) {
                        return;
                    }
                    pin.setPin(pinArray[positionSpinner]);
                    pin.setPinSpinnerPosition(positionSpinner);

                    if (pin.getPinMode() == IOModule.TOGGLE) {
                        inputFragment.requestHiZ(false, pin);
                        inputFragment.inputRequest_inputChanel(IOModule.LOW_LEVEL, pin.getMemory(), pin.getBitPosition(), pin);
                    } else {

                        ///////////    Simulate button release    ///////////
                        int[] coordinates = new int[2];
                        holder.pushButton.getLocationOnScreen(coordinates);

                        // MotionEvent parameters
                        long downTime = SystemClock.uptimeMillis();
                        long eventTime = SystemClock.uptimeMillis();
                        int action = MotionEvent.ACTION_UP;
                        int x = coordinates[0];
                        int y = coordinates[1];
                        int metaState = 0;

                        // dispatch the event
                        MotionEvent event = MotionEvent.obtain(downTime, eventTime, action, x, y, metaState);
                        holder.pushButton.dispatchTouchEvent(event);
                        ///////////////////////////////////////////////////////
                    }

                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    //Should never occur
                }
            });

            holder.pinModeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int positionSpinner, long id) {
                    pin.setPinMode(IOModule.PIN_MODES[positionSpinner]);
                    pin.setPinModePosition(positionSpinner);

                    switch (pin.getPinMode()) {
                        case IOModule.PUSH_GND:
                        case IOModule.PUSH_VDD:
                            pin.setPinState(IOModule.TRI_STATE);

                            //It's an analog pin
                            if (pin.getPinSpinnerPosition() >= 14 && pin.getPinSpinnerPosition() <= 19) {
                                ADC_ATmega328P.adcInput[pin.getPinSpinnerPosition() - 14] = (short) (randomGenerator.nextInt(5 * 1000));
                            }

                            break;

                        case IOModule.PULL_UP:
                            pin.setPinState(IOModule.HIGH_LEVEL);

                            //It's an analog pin
                            if (pin.getPinSpinnerPosition() >= 14 && pin.getPinSpinnerPosition() <= 19) {
                                ADC_ATmega328P.adcInput[pin.getPinSpinnerPosition() - 14] = (short) (5 * 1000);
                            }
                            break;

                        case IOModule.PULL_DOWN:
                            pin.setPinState(IOModule.LOW_LEVEL);

                            //It's an analog pin
                            if (pin.getPinSpinnerPosition() >= 14 && pin.getPinSpinnerPosition() <= 19) {
                                ADC_ATmega328P.adcInput[pin.getPinSpinnerPosition() - 14] = (short) (0);
                            }
                            break;
                        case IOModule.TOGGLE:
                            if (listView.getChoiceMode() == ListView.CHOICE_MODE_MULTIPLE) {
                                break;
                            }
                            pin.setPinState(IOModule.LOW_LEVEL);

                            //It's an analog pin
                            if (pin.getPinSpinnerPosition() >= 14 && pin.getPinSpinnerPosition() <= 19) {
                                ADC_ATmega328P.adcInput[pin.getPinSpinnerPosition() - 14] = (short) (0);
                            }

                            holder.inputPinState.setBackgroundResource(R.drawable.digital_input_off);

                            //No pin selected
                            if (pin.getPinSpinnerPosition() < 0) {
                                break;
                            }
                            inputFragment.requestHiZ(false, pin);
                            inputFragment.inputRequest_inputChanel(IOModule.LOW_LEVEL, pin.getMemory(), pin.getBitPosition(), pin);
                            break;

                        default:
                            //There are no other modes
                            break;

                    }

                    ///////////////////////Simulate button release///////////////////////
                    int[] coordinates = new int[2];
                    holder.pushButton.getLocationOnScreen(coordinates);

                    // MotionEvent parameters
                    long downTime = SystemClock.uptimeMillis();
                    long eventTime = SystemClock.uptimeMillis();
                    int action = MotionEvent.ACTION_UP;
                    int x = coordinates[0];
                    int y = coordinates[1];
                    int metaState = 0;

                    // dispatch the event
                    MotionEvent event = MotionEvent.obtain(downTime, eventTime, action, x, y, metaState);
                    holder.pushButton.dispatchTouchEvent(event);
                    ////////////////////////////////////////////////////
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    //Should never occur
                }
            });

            holder.pushButton.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {

                    //No pin selected
                    if (pin.getPinSpinnerPosition() < 0) {
                        return false;
                    }

                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        holder.pushButton.setText(UCModule.getButtonTextOn());
                        holder.pushButton.setBackgroundColor(UCModule.getButonOnCollor());

                        inputFragment.requestHiZ(false, pin);

                        switch (pin.getPinMode()) {
                            case IOModule.PUSH_GND:
                            case IOModule.PULL_UP:
                                holder.inputPinState.setBackgroundResource(R.drawable.digital_input_off);
                                pin.setPinState(IOModule.LOW_LEVEL);

                                //It's an analog pin
                                if (pin.getPinSpinnerPosition() >= 14 && pin.getPinSpinnerPosition() <= 19) {
                                    ADC_ATmega328P.adcInput[pin.getPinSpinnerPosition() - 14] = (short) (0);
                                }

                                inputFragment.inputRequest_inputChanel(IOModule.LOW_LEVEL, pin.getMemory(), pin.getBitPosition(), pin);
                                break;
                            case IOModule.PUSH_VDD:
                            case IOModule.PULL_DOWN:
                                holder.inputPinState.setBackgroundResource(R.drawable.digital_input_on);
                                pin.setPinState(IOModule.HIGH_LEVEL);

                                //It's an analog pin
                                if (pin.getPinSpinnerPosition() >= 14 && pin.getPinSpinnerPosition() <= 19) {
                                    ADC_ATmega328P.adcInput[pin.getPinSpinnerPosition() - 14] = (short) (5 * 1000);
                                }

                                inputFragment.inputRequest_inputChanel(IOModule.HIGH_LEVEL, pin.getMemory(), pin.getBitPosition(), pin);
                                break;
                            case IOModule.TOGGLE:

                                if (pin.getPinState() == IOModule.LOW_LEVEL) {
                                    holder.inputPinState.setBackgroundResource(R.drawable.digital_input_on);
                                    pin.setPinState(IOModule.HIGH_LEVEL);

                                    //It's an analog pin
                                    if (pin.getPinSpinnerPosition() >= 14 && pin.getPinSpinnerPosition() <= 19) {
                                        ADC_ATmega328P.adcInput[pin.getPinSpinnerPosition() - 14] = (short) (5 * 1000);
                                    }

                                    inputFragment.inputRequest_inputChanel(IOModule.HIGH_LEVEL, pin.getMemory(), pin.getBitPosition(), pin);
                                } else {
                                    holder.inputPinState.setBackgroundResource(R.drawable.digital_input_off);
                                    pin.setPinState(IOModule.LOW_LEVEL);

                                    //It's an analog pin
                                    if (pin.getPinSpinnerPosition() >= 14 && pin.getPinSpinnerPosition() <= 19) {
                                        ADC_ATmega328P.adcInput[pin.getPinSpinnerPosition() - 14] = (short) (0);
                                    }

                                    inputFragment.inputRequest_inputChanel(IOModule.LOW_LEVEL, pin.getMemory(), pin.getBitPosition(), pin);
                                }
                                break;

                            default:
                                //There are no other modes
                                break;
                        }

                    } else if (event.getAction() == MotionEvent.ACTION_UP) {
                        holder.pushButton.setText(UCModule.getButtonTextOff());
                        holder.pushButton.setBackgroundColor(UCModule.getButonOffCollor());

                        switch (pin.getPinMode()) {
                            case IOModule.PUSH_GND:
                            case IOModule.PUSH_VDD:

                                pin.setHiZDone(false, pin.getPinSpinnerPosition());
                                inputFragment.requestHiZ(true, pin);
                                pin.setPinState(IOModule.TRI_STATE);

                                //It's an analog pin
                                if (pin.getPinSpinnerPosition() >= 14 && pin.getPinSpinnerPosition() <= 19) {
                                    ADC_ATmega328P.adcInput[pin.getPinSpinnerPosition() - 14] = (short) (randomGenerator.nextInt(5 * 1000));
                                }

                                holder.inputPinState.setBackgroundResource(R.drawable.digital_input_undefined);

                                new WaitHiZPushButton(pin).execute();

                                //Wait for HiZ Request
//                                while (!pin.getHiZDone(pin.getPinSpinnerPosition())) {
//                                    Thread.yield();
//                                }
//
//                                //Check if request HiZ was acepted
//                                if (pin.getHiZ(pin.getPinSpinnerPosition())) {
//
//                                    if (inputFragment.isPullUpEnabled() && inputFragment.isPinPullUPEnabled(pin.getMemory(), pin.getBitPosition())) {
//                                        inputFragment.inputRequest_inputChanel(IOModule.HIGH_LEVEL, pin.getMemory(), pin.getBitPosition(), pin);
//                                    } else {
//                                        inputFragment.inputRequest_inputChanel(randomGenerator.nextInt(2), pin.getMemory(), pin.getBitPosition(), pin);
//                                    }
//                                }
                                break;
                            case IOModule.PULL_UP:
                                inputFragment.requestHiZ(false, pin);
                                holder.inputPinState.setBackgroundResource(R.drawable.digital_input_on);
                                pin.setPinState(IOModule.HIGH_LEVEL);

                                //It's an analog pin
                                if (pin.getPinSpinnerPosition() >= 14 && pin.getPinSpinnerPosition() <= 19) {
                                    ADC_ATmega328P.adcInput[pin.getPinSpinnerPosition() - 14] = (short) (5 * 1000);
                                }

                                inputFragment.inputRequest_inputChanel(IOModule.HIGH_LEVEL, pin.getMemory(), pin.getBitPosition(), pin);
                                break;
                            case IOModule.PULL_DOWN:
                                inputFragment.requestHiZ(false, pin);
                                holder.inputPinState.setBackgroundResource(R.drawable.digital_input_off);
                                pin.setPinState(IOModule.LOW_LEVEL);

                                //It's an analog pin
                                if (pin.getPinSpinnerPosition() >= 14 && pin.getPinSpinnerPosition() <= 19) {
                                    ADC_ATmega328P.adcInput[pin.getPinSpinnerPosition() - 14] = (short) (0);
                                }

                                inputFragment.inputRequest_inputChanel(IOModule.LOW_LEVEL, pin.getMemory(), pin.getBitPosition(), pin);
                                break;

                            default:
                                //There are no other modes
                                break;
                        }
                    }
                    return true;
                }
            });
        } else {
            ///////////////////////////////ANALOGIC PIN/////////////////////////////////////////
            final HintAdapter pinSpinnerAdapter =
                    new HintAdapter(inputFragment.getContext(), android.R.layout.simple_spinner_item, pinArray);

            if (convertView == null) {
                view = LayoutInflater.from(inputFragment.getContext()).inflate(R.layout.analog_input_pin,
                        parent, false);

                Spinner pinSpinner = (Spinner) view.findViewById(R.id.pinSelectorAnalogInput);
                SeekBar voltageLevel = (SeekBar) view.findViewById(R.id.voltageLevel);
                TextView voltageDisplay = (TextView) view.findViewById(R.id.voltageDisplay);

                holder = new ViewHolderInput_ATmega328P(pinSpinner, voltageLevel, voltageDisplay);
                view.setTag(holder);
            } else {
                holder = (ViewHolderInput_ATmega328P) view.getTag();
            }

            holder.voltageDisplay.setText(decimalFormat.format(0));

            pinSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            holder.pinSpinner.setAdapter(pinSpinnerAdapter);

            if (pin.getPinSpinnerPosition() < 0) {
                holder.pinSpinner.setSelection(pinSpinnerAdapter.getCount());
            } else {
                holder.pinSpinner.setSelection(pin.getPinSpinnerPosition());
            }

            holder.pinSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int positionSpinner, long id) {
                    if (positionSpinner == pinSpinnerAdapter.getCount()) {
                        return;
                    }
                    pin.setPin(pinArray[positionSpinner]);
                    pin.setPinSpinnerPosition(positionSpinner);

                    double voltage = sourcePower * (holder.voltageLevel.getProgress() / 100.0);
                    holder.voltageDisplay.setText(decimalFormat.format(voltage));

                    //It's an analog pin
                    if (pin.getPinSpinnerPosition() >= 14 && pin.getPinSpinnerPosition() <= 19) {
                        ADC_ATmega328P.adcInput[pin.getPinSpinnerPosition() - 14] = (short) (voltage * 1000);
                    }

                    int state = pin.getPinStateFromAnalog(voltage);
                    pin.setPinState(state);
                    inputFragment.requestHiZ(false, pin);

                    if (state == IOModule.TRI_STATE) {
//                        inputFragment.requestHiZ(true, pin);
//
//                        //Wait for pull Up Request
//                        try {
//                            Thread.sleep(10);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }

                        //Check if request HiZ was acepted
//                        if (pin.getHiZ(pin.getPinSpinnerPosition())) {
                        if (inputFragment.isPullUpEnabled() && inputFragment.isPinPullUPEnabled(pin.getMemory(), pin.getBitPosition())) {
                            inputFragment.inputRequest_inputChanel(IOModule.HIGH_LEVEL, pin.getMemory(), pin.getBitPosition(), pin);

                        } else {
                            inputFragment.inputRequest_inputChanel(randomGenerator.nextInt(2), pin.getMemory(), pin.getBitPosition(), pin);
                        }
//                        }

                    } else {
//                        inputFragment.requestHiZ(false, pin);
                        inputFragment.inputRequest_inputChanel(state, pin.getMemory(), pin.getBitPosition(), pin);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    //Should never occur
                }
            });

            holder.voltageLevel.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    double voltage = sourcePower * (progress / 100.0);
                    holder.voltageDisplay.setText(decimalFormat.format(voltage));

                    if (pin.getPinSpinnerPosition() >= 0) {

                        //It's an analog pin
                        if (pin.getPinSpinnerPosition() >= 14 && pin.getPinSpinnerPosition() <= 19) {
                            ADC_ATmega328P.adcInput[pin.getPinSpinnerPosition() - 14] = (short) (voltage * 1000);
                        }

                        int state = pin.getPinStateFromAnalog(voltage);
                        pin.setPinState(state);
//                        inputFragment.requestHiZ(false, pin);

                        if (state == IOModule.TRI_STATE) {
//                            inputFragment.requestHiZ(true, pin);
//
//                            //Wait for pull Up Request
//                            try {
//                                Thread.sleep(10);
//                            } catch (InterruptedException e) {
//                                e.printStackTrace();
//                            }

                            //Check if request HiZ was acepted
//                            if (pin.getHiZ(pin.getPinSpinnerPosition())) {
                            if (inputFragment.isPullUpEnabled() && inputFragment.isPinPullUPEnabled(pin.getMemory(), pin.getBitPosition())) {
                                inputFragment.inputRequest_inputChanel(IOModule.HIGH_LEVEL, pin.getMemory(), pin.getBitPosition(), pin);

                            } else {
                                inputFragment.inputRequest_inputChanel(randomGenerator.nextInt(2), pin.getMemory(), pin.getBitPosition(), pin);
                            }
//                            }

                        } else {
//                            inputFragment.requestHiZ(false, pin);
                            inputFragment.inputRequest_inputChanel(state, pin.getMemory(), pin.getBitPosition(), pin);
                        }
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    //Not needed
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    //Not needed
                }
            });
        }

        view.setBackgroundColor(listView.isItemChecked(position) ?
                UCModule.getSelectedColor() :
                Color.TRANSPARENT);

        return view;
    }
*/
  /*  private class WaitHiZPushButton extends AsyncTask<Void, Void, Void>{

        private InputPin_ATmega328P waitPin;

        public WaitHiZPushButton(InputPin_ATmega328P pin) {
            this.waitPin = pin;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            while (!waitPin.getHiZDone(waitPin.getPinSpinnerPosition())) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            //Check if request HiZ was acepted
            if (waitPin.getHiZ(waitPin.getPinSpinnerPosition())) {

                if (inputFragment.isPullUpEnabled() && inputFragment.isPinPullUPEnabled(waitPin.getMemory(), waitPin.getBitPosition())) {
                    inputFragment.inputRequest_inputChanel(IOModule.HIGH_LEVEL, waitPin.getMemory(), waitPin.getBitPosition(), waitPin);
                } else {
                    inputFragment.inputRequest_inputChanel(randomGenerator.nextInt(2), waitPin.getMemory(), waitPin.getBitPosition(), waitPin);
                }
            }
            return null;
        }
    }*/
}
