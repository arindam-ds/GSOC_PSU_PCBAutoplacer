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
