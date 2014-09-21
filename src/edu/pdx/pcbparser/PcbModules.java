/*
* Copyright 2014 Arindam Bannerjee
* This work is distributed under the terms of the "MIT license". Please see the file
* LICENSE in this distribution for license terms.
*
*/

package edu.pdx.pcbparser;

import java.util.*;

public class PcbModules {
  
  private int moduleId;
  private String moduleType;//Ex: LEDV, R4
  private String moduleLayer; // Ex: F.Cu
  private float positionX;
  private float positionY;
  private int angleZ;
  private String moduleName; //Ex: R1, R2, R3
  //String moduleNameValue;//future use
  private float componentWidth;
  private float componentHeight;
  private List<Pads> pad;
  
  public void setModuleId(int moduleId){
    this.moduleId = moduleId;
  }
  public void setModuleType(String moduleType){
    this.moduleType = moduleType;
  }
  public void setModuleLayer(String moduleLayer){
    this.moduleLayer = moduleLayer;
  }
  public void setPositionX(float positionX){
    this.positionX = positionX;
  }
  public float getPositionX(){
    return positionX;
  }
  public void setPositionY(float positionY){
    this.positionY = positionY;
  }
  public float getPositionY(){
    return positionY;
  }
  public void setAngleZ(int angleZ){
    this.angleZ = angleZ;
  }
  public int getAngleZ(){
    return angleZ;
  }
  public void setModuleName(String moduleName){
    this.moduleName = moduleName;
  }
  public String getModuleName(){
    return moduleName;
  }
  public float getComponentWidth() {
    return componentWidth;
  }
  public void setComponentWidth(float componentWidth) {
    this.componentWidth = componentWidth;
  }
  public float getComponentHeight() {
    return componentHeight;
  }
  public void setComponentHeight(float componentHeight) {
    this.componentHeight = componentHeight;
  }
  public List<Pads> getPad() {
    return pad;
  }
  public void setPad(List<Pads> pad) {
    this.pad = pad;
  }
}
