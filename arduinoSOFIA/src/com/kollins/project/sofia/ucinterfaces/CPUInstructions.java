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

public interface CPUInstructions {
    int INSTRUCTION_ADC = 0;
    int INSTRUCTION_ADD = 1;
    int INSTRUCTION_ADIW = 2;
    int INSTRUCTION_AND = 3;     //AND - TST
    int INSTRUCTION_ANDI = 4;
    int INSTRUCTION_ASR = 5;

    int INSTRUCTION_BCLR = 6;
    int INSTRUCTION_BLD = 7;
    int INSTRUCTION_BRBC = 8;
    int INSTRUCTION_BRBS = 9;
    int INSTRUCTION_BREAK = 10;
    int INSTRUCTION_BSET = 11;
    int INSTRUCTION_BST = 12;

    int INSTRUCTION_CALL = 13;
    int INSTRUCTION_CBI = 14;
    int INSTRUCTION_COM = 15;
    int INSTRUCTION_CP = 16;
    int INSTRUCTION_CPC = 17;
    int INSTRUCTION_CPI = 18;
    int INSTRUCTION_CPSE = 19;

    int INSTRUCTION_DEC = 20;

    int INSTRUCTION_EOR = 21; //EOR - CLR

    int INSTRUCTION_FMUL = 22;
    int INSTRUCTION_FMULS = 23;
    int INSTRUCTION_FMULSU = 24;

    int INSTRUCTION_ICALL = 25;
    int INSTRUCTION_IJMP = 26;
    int INSTRUCTION_IN = 27;
    int INSTRUCTION_INC = 28;

    int INSTRUCTION_JMP = 29;

    int INSTRUCTION_LD_X_POST_INCREMENT = 30;
    int INSTRUCTION_LD_X_PRE_INCREMENT = 31;
    int INSTRUCTION_LD_X_UNCHANGED = 32;
    int INSTRUCTION_LD_Y_POST_INCREMENT = 33;
    int INSTRUCTION_LD_Y_PRE_INCREMENT = 34;
    int INSTRUCTION_LD_Y_UNCHANGED = 35;
    int INSTRUCTION_LD_Z_POST_INCREMENT = 36;
    int INSTRUCTION_LD_Z_PRE_INCREMENT = 37;
    int INSTRUCTION_LD_Z_UNCHANGED = 38;
    int INSTRUCTION_LDD_Y = 39;
    int INSTRUCTION_LDD_Z = 40;
    int INSTRUCTION_LDI = 41; //LDI - SER
    int INSTRUCTION_LDS = 42;
    int INSTRUCTION_LPM_Z_POST_INCREMENT = 43;
    int INSTRUCTION_LPM_Z_UNCHANGED_DEST_R0 = 44;
    int INSTRUCTION_LPM_Z_UNCHANGED = 45;
    int INSTRUCTION_LSR = 46;

    int INSTRUCTION_MOV = 47;
    int INSTRUCTION_MOVW = 48;
    int INSTRUCTION_MUL = 49;
    int INSTRUCTION_MULS = 50;
    int INSTRUCTION_MULSU = 51;

    int INSTRUCTION_NEG = 52;
    int INSTRUCTION_NOP = 53;

    int INSTRUCTION_OR = 54;
    int INSTRUCTION_ORI = 55;   //ORI - SBR
    int INSTRUCTION_OUT = 56;

    int INSTRUCTION_POP = 57;
    int INSTRUCTION_PUSH = 58;

    int INSTRUCTION_RCALL = 59;
    int INSTRUCTION_RET = 60;
    int INSTRUCTION_RETI = 61;
    int INSTRUCTION_RJMP = 62;
    int INSTRUCTION_ROR = 63;

    int INSTRUCTION_SBC = 64;
    int INSTRUCTION_SBCI = 65;
    int INSTRUCTION_SBI = 66;
    int INSTRUCTION_SBIC = 67;
    int INSTRUCTION_SBIS = 68;
    int INSTRUCTION_SBIW = 69;
    int INSTRUCTION_SBRC = 70;
    int INSTRUCTION_SBRS = 71;
    int INSTRUCTION_SLEEP = 72;
    int INSTRUCTION_SPM = 73;
    int INSTRUCTION_ST_X_POST_INCREMENT = 74;
    int INSTRUCTION_ST_X_PRE_INCREMENT = 75;
    int INSTRUCTION_ST_X_UNCHANGED = 76;
    int INSTRUCTION_ST_Y_POST_INCREMENT = 77;
    int INSTRUCTION_ST_Y_PRE_INCREMENT = 78;
    int INSTRUCTION_ST_Y_UNCHANGED = 79;
    int INSTRUCTION_ST_Z_POST_INCREMENT = 80;
    int INSTRUCTION_ST_Z_PRE_INCREMENT = 81;
    int INSTRUCTION_ST_Z_UNCHANGED = 82;
    int INSTRUCTION_STD_Y = 83;
    int INSTRUCTION_STD_Z = 84;
    int INSTRUCTION_STS = 85;
    int INSTRUCTION_SUB = 86;
    int INSTRUCTION_SUBI = 87;
    int INSTRUCTION_SWAP = 88;

    int INSTRUCTION_WDR = 89;

    int UNDEFINED_INSTRUCTION = 90;
}
