  
//#define LEDPIN 3;
// the setup function runs once when you press reset or power the board
int pot;
int sensorValue;
void setup() {
  // initialize digital pin LEDPIN as an output.
  pinMode(3, OUTPUT); // enable pin
  pinMode(5, OUTPUT); // move right pin
  pinMode(6, OUTPUT); // move left pin
}

// the loop function runs over and over again forever
void loop() {
  sensorValue = analogRead(A0);
  pot = analogRead(A1);
  digitalWrite(3, HIGH);   // do enable

  if (pot<512){
  analogWrite(5, 0); // stop move left;
  analogWrite(6, (512-pot)/2); //move right;
 // delay(1000);                       // wait for a second
  }
  if (pot>=512){ 
  analogWrite(5,(pot-512)/2); // stop move left;
  analogWrite(6, 0); //move right;
 //  analogWrite(5, 125); //move left;
 // analogWrite(6,0); //stop move right
 // delay(1000);                       // wait for a second
  }
}
