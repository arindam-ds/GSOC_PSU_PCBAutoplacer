package edu.pdx.parser;

public class Components {
    String nameOfComp;
    String nameOfCompPart;
    int numOfPin;

    public Components(String compName, String partName, int pin) {
      this.nameOfComp = compName;
      this.nameOfCompPart = partName;	
      this.numOfPin = pin;
    }
}
