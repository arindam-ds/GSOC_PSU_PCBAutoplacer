/*
* Copyright 2014 Arindam Bannerjee
* This work is distributed under the terms of the "MIT license". Please see the file
* LICENSE in this distribution for license terms.
*
*/

package edu.pdx.pcbparser;

import java.util.*;

public class PcbModules {
  
  public int moduleId;
  public String moduleType;//Ex: LEDV, R4
  public String moduleLayer; // Ex: F.Cu
  public float positionX;
  public float positionY;
  public int angleZ;
  public String moduleName; //Ex: R1, R2, R3
  //String moduleNameValue;//future use
  public float componentWidth;
  public float componentHeight;
  public List<Pads> pad;
}
