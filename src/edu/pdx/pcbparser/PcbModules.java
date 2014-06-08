package edu.pdx.pcbparser;

import java.util.*;

public class PcbModules {
  
  int moduleId;
  String moduleType;//Ex: LEDV, R4
  String moduleLayer; // Ex: F.Cu
  float positionX;
  float positionY;
  int angleZ;
  String moduleName; //Ex: R1, R2, R3
  //String moduleNameValue;//future use
  float componentWidth;
  float componentHeight;
  List<Pads> pad;
}
