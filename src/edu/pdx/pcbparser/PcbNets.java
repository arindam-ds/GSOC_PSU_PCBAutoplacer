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

public class PcbNets {
  
  int netId;
  String netName;
  
  public PcbNets(int netId, String netName){
    this.netId = netId;
    this.netName = netName;
  }
}
