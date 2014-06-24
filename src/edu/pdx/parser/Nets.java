package edu.pdx.parser;

/**
 * @author Arindam Banerjee
 *
 */

public class Nets {
  
  public int netId;
  public String compName;
  public int pin;

  public Nets (int Id, String comp, int pin) {
    this.netId = Id;
    this.compName = comp;
    this.pin = pin;
  }
}
