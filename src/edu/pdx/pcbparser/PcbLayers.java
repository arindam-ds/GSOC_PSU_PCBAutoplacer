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

public class PcbLayers {
  
  int layerId;
  String layerName;
  String layerType;
  
  public PcbLayers(int layerId, String layerName, String layerType){
    this.layerId = layerId;
    this.layerName = layerName;
    this.layerType = layerType;
  }
}
