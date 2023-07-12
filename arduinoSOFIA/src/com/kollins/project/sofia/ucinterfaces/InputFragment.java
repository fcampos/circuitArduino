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

package com.kollins.project.sofia.ucinterfaces;

//import android.os.Handler;

/**
 * Created by kollins on 3/21/18.
 */

public interface InputFragment extends IOModule{

    String TAG_INPUT_FRAGMENT = "inputFragmentTAG";

    void addDigitalInput();
    void addAnalogicInput();
    boolean haveInput();
    void clearAll();

    void setDataMemory(DataMemory dataMemory);
 //   void setScreenUpdater(Handler screenUpdater);
	void inputRequest_inputChanel(int value, int i, int j);
}
