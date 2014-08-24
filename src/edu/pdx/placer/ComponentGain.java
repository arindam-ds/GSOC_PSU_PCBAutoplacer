/*
* Copyright 2014 Arindam Bannerjee
* This work is distributed under the terms of the "MIT license". Please see the file
* LICENSE in this distribution for license terms.
*
*/

package edu.pdx.placer;

public class ComponentGain {
  public String nameOfComp;
  public int gain;
  public ComponentGain (String compName, int gain) {
    this.nameOfComp = compName;
    this.gain = gain;
  }
  public ComponentGain () {
  }  
}
