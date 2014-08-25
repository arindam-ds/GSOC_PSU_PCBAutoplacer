/*
 * Copyright 2014 Arindam Bannerjee
 * This work is distributed under the terms of the "MIT license". Please see the file
 * LICENSE in this distribution for license terms.
 *
 */

package edu.pdx.parser;

public class Nets {
  
  private int netId;
  private String compName;
  private int pin;

  public Nets (int Id, String comp, int pin) {
    this.netId = Id;
    this.compName = comp;
    this.pin = pin;
  }
  public String getCompName(){
    return compName;
  }
  public int getNetId(){
    return netId;
  }
  public int getPin(){
    return pin;
  }
}
