/*
  Blink
  
*/
int LEDPIN = 8;
int inputPIN = 0;
int pwmPIN = 5;
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
  analogWrite(pwmPIN, 125);
  //digitalWrite(LEDPIN, LOW);    // turn the LED off by making the voltage LOW
 // delay(250);                 // wait for a second
 // digitalWrite(LEDPIN, HIGH);   // turn the LED on (HIGH is the voltage level)
 // delay(250);                       // wait for a second
  if (analogRead(inputPIN)>500)
  digitalWrite(LEDPIN, HIGH);
  else
  digitalWrite(LEDPIN, LOW);
  
  //}
 // else{
  //digitalWrite(LEDPIN, LOW);
 // }
}
