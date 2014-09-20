/*
* Copyright 2014 Arindam Bannerjee
* This work is distributed under the terms of the "MIT license". Please see the file
* LICENSE in this distribution for license terms.
*
*/

package edu.pdx.pcbparser;

/**
 * @author Arindam Banerjee
 *
 */

public class Pads {

  private int padId;
  private float padX;
  private float padY;
  private int padAngleZ;
  private float padWidth;
  private float padHeight;
  private PcbNets net;
  public void setPadId(int padId){
    this.padId = padId;
  }
  public void setPadX(float padX){
    this.padX = padX;
  }
  public void setPadY(float padY){
    this.padY = padY;
  }
  public void setPadAngleZ(int angleZ){
    this.padAngleZ = angleZ;
  }
  public void setPadWidth(float padWidth){
    this.padWidth = padWidth;
  }
  public void setPadHeight(float padHeight){
    this.padHeight = padHeight;
  }
  public void setNet(PcbNets net){
    this.net = net;
  }
}
