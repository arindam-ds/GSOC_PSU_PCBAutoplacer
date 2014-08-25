/*
 * Copyright 2014 Arindam Bannerjee
 * This work is distributed under the terms of the "MIT license". Please see the file
 * LICENSE in this distribution for license terms.
 *
 */

package edu.pdx.parser;

import java.util.*;

public class ComponentToNet {
  private String nameOfComp;
  private List<Integer> netIdList;
  
  public String getNameOfComp(){
    return nameOfComp;
  }
  public void setNameOfComp(String nameOfComp){
    this.nameOfComp = nameOfComp;
  }
  public List<Integer> getNetIdList(){
    return netIdList;
  }
  public void setNetIdList(List<Integer> netIdList){
    this.netIdList = netIdList;
  }
}
