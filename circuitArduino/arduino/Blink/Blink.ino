/*
  Blink
  
*/
int LEDPIN = 3;
int inputPIN = 0;
int pwmPIN = 9;
int n;
// the setup function runs once when you press reset or power the board
void setup() {
  // initialize digital pin LEDPIN as an output.
  pinMode(LEDPIN, OUTPUT);
  pinMode(pwmPIN, OUTPUT);
  pinMode(inputPIN, INPUT);
}

// the loop function runs over and over again forever
void loop() {
  //analogWrite(pwmPIN, 125);
  digitalWrite(LEDPIN, LOW);    // turn the LED off by making the voltage LOW
  delay(1000);                 // wait for a second
  digitalWrite(LEDPIN, HIGH);   // turn the LED on (HIGH is the voltage level)
  delay(1000);                       // wait for a second
 // if (analogRead(inputPIN)>500)
 // digitalWrite(LEDPIN, digitalRead(inputPIN));
 // else
 // digitalWrite(LEDPIN, LOW);
  
  //}
 // else{
  //digitalWrite(LEDPIN, LOW);
 // }
}
