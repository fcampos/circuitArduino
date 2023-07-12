/*
  Blink
  
*/
//#define LEDPIN 3;
// the setup function runs once when you press reset or power the board

void setup() {
  // initialize digital pin LEDPIN as an output.
  pinMode(3, OUTPUT); // enable pin
  pinMode(5, OUTPUT); // move right pin
  pinMode(6, OUTPUT); // move left pin
}

// the loop function runs over and over again forever
void loop() {
  digitalWrite(3, HIGH);   // do enable
  
  analogWrite(5, 0); // stop move left;
  analogWrite(6, 125); //move right;
  delay(3000);                       // wait for a second
  analogWrite(5, 125); //move left;
  analogWrite(6,0); //stop move right
  delay(3000);                       // wait for a second
}
