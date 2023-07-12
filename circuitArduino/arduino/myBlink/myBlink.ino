/*
  Blink
  
*/
int LEDPIN = 3;
int inputPIN = 4;
int outputPIN = 5;
int n;
// the setup function runs once when you press reset or power the board
void setup() {
  // initialize digital pin LEDPIN as an output.
  pinMode(LEDPIN, OUTPUT);
  pinMode(inputPIN, INPUT);
  pinMode(outputPIN, OUTPUT);
}
// the loop function runs over and over again forever
void loop() {
  
  for (n=1;n<6;n++){
  digitalWrite(LEDPIN, HIGH);   // turn the LED on (HIGH is the voltage level)
  delay(100);                       // wait for a second
  digitalWrite(LEDPIN, LOW);    // turn the LED off by making the voltage LOW
  delay(100);}                 // wait for a second
  digitalWrite(LEDPIN, HIGH);   // turn the LED on (HIGH is the voltage level)
  delay(1000);    
  digitalWrite(outputPIN, digitalRead(inputPIN));
  // wait for a second
  
}
