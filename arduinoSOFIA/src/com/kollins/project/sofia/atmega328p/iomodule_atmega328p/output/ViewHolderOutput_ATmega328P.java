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

import android.view.View;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.kollins.project.sofia.R;

/**
 * Created by kollins on 3/21/18.
 */

public class ViewHolderOutput_ATmega328P {
    final Spinner pinSpinner;
    final TextView led;

    final LinearLayout meter;
    final TextView freqMeter;
    final TextView dcMeter;

    public ViewHolderOutput_ATmega328P(View view){
        pinSpinner = (Spinner) view.findViewById(R.id.pinSelectorOutput);
        led = (TextView) view.findViewById(R.id.ledState);

        meter = (LinearLayout) view.findViewById(R.id.meter);
        freqMeter = (TextView) view.findViewById(R.id.frequency);
        dcMeter = (TextView) view.findViewById(R.id.dutycycle);
    }
}
