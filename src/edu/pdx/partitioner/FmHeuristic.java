/*
* Copyright 2014 Arindam Bannerjee
* This work is distributed under the terms of the "MIT license". Please see the file
* LICENSE in this distribution for license terms.
*
*/

package edu.pdx.partitioner;

import java.util.*;
import edu.pdx.placer.*;
import edu.pdx.pcbparser.*;
import edu.pdx.parser.*;

/**
 * @author Arindam Banerjee
 *
 */

public class FmHeuristic {
  String netlistFile="";
  String pcbFile="";
  List<String> leftBucket = new ArrayList<String>();
  List<String> rightBucket = new ArrayList<String>();
  public List<String> topLeftBucket = new ArrayList<String>();
  public List<String> bottomLeftBucket = new ArrayList<String>();
  public List<String> topRightBucket = new ArrayList<String>();
  public List<String> bottomRightBucket = new ArrayList<String>();
  List<ComponentGain> compGainList = new ArrayList<ComponentGain>();
  public FmHeuristic(String netlistFile, String pcbFile){
    this.netlistFile = netlistFile;
    this.pcbFile = pcbFile;
  }
  public void FmVerticalPartitioner(){
    try{
      NetlistParser netparser = new NetlistParser(netlistFile);
	  netparser.parse();  
	  for(int i=0;i<(netparser.numberOfComponents/2);i++){
	    leftBucket.add(netparser.compList.get(i).getNameOfComp());	
	  }
	  for(int i=(netparser.numberOfComponents/2);i<netparser.numberOfComponents;i++){
	    rightBucket.add(netparser.compList.get(i).getNameOfComp());	
	  }
	  CorePartitioner(leftBucket, rightBucket);
    }catch (Exception e) {
        System.err.println("Error: " + e.getMessage());
     }
  }//FmVerticalPartitioner() ends
  public void FmHorizontalPartitioner(){
    NetlistParser netparser = new NetlistParser(netlistFile);
    netparser.parse();  
    for(int i=0;i<leftBucket.size()/2;i++){
      topLeftBucket.add(leftBucket.get(i));	
    }
    for(int i=leftBucket.size()/2;i<leftBucket.size();i++){
      bottomLeftBucket.add(leftBucket.get(i));		
    }
    CorePartitioner(topLeftBucket, bottomLeftBucket);
    for(int i=0;i<rightBucket.size()/2;i++){
      topRightBucket.add(rightBucket.get(i));	
    }
    for(int i=rightBucket.size()/2;i<rightBucket.size();i++){
      bottomRightBucket.add(rightBucket.get(i));		
    }
    CorePartitioner(topRightBucket, bottomRightBucket);
    CorePartitioner(topLeftBucket, bottomLeftBucket);
  }//FmHorizontalPartitioner() ends
  public void CorePartitioner(List<String> firstBucket, List<String> secondBucket){
    try{
    int totalPrevGain = 999, totalCurrentGain = 998;
    int numberOfPass=1;
    int areaConstraintMin = ((firstBucket.size()+secondBucket.size()) * 40)/100;
    List<String> tempFirstBucket = new ArrayList<String>();
    List<String> tempSecondBucket = new ArrayList<String>();
    List<String> prevFirstBucket = new ArrayList<String>();
    List<String> prevSecondBucket = new ArrayList<String>();
    NetlistParser netparser = new NetlistParser(netlistFile);
    netparser.parse();
    while((totalCurrentGain < totalPrevGain)&&(numberOfPass<=netparser.compList.size())){
      prevFirstBucket.clear();
      prevSecondBucket.clear();
      prevFirstBucket.addAll(firstBucket);
      prevSecondBucket.addAll(secondBucket);
	  numberOfPass++;
      totalPrevGain = totalCurrentGain;
      totalCurrentGain=0;
      compGainList.clear();
      /*Gain calculation*/
      /*Initialize compGainList with zero gain for each component for each pass*/
      for(int i=0; i<netparser.compList.size(); i++){
        compGainList.add(new ComponentGain(netparser.compList.get(i).getNameOfComp(), 0));
      }
      for(int i=1;i<=netparser.netId;i++){ //netId = number of nets
    	tempFirstBucket.clear();
    	tempSecondBucket.clear();
    	for(int j=0;j<netparser.netList.size();j++){
    	  if(netparser.netList.get(j).getNetId() == i){
            if(firstBucket.contains(netparser.netList.get(j).getCompName())){
              tempFirstBucket.add(netparser.netList.get(j).getCompName());
            }
            else{
              tempSecondBucket.add(netparser.netList.get(j).getCompName());
            }
    	  }
    	}
    	/*Removing duplicate entries from tempFirstBucket and tempSecondBucket*/
    	tempFirstBucket = RemoveDuplicates(tempFirstBucket);
    	tempSecondBucket = RemoveDuplicates(tempSecondBucket);
    	if(tempFirstBucket.size()==1){
          //gain++
          for(int m=0; m<=compGainList.size();m++){
            if(compGainList.get(m).getNameOfComp().equals(tempFirstBucket.get(0))){
              compGainList.get(m).setGain(compGainList.get(m).getGain()+1);
              break;
            }
    	  }
    	}
    	if(tempSecondBucket.size()==1){
          //gain++
          for(int m=0; m<=compGainList.size();m++){
            if(compGainList.get(m).getNameOfComp().equals(tempSecondBucket.get(0))){
              compGainList.get(m).setGain(compGainList.get(m).getGain()+1);
              break;
            }
      	  }
        }
        if(tempFirstBucket.isEmpty()){
          //gain--
          for(int k=0; k<tempSecondBucket.size(); k++){
            for(int m=0; m<compGainList.size(); m++){
              if(compGainList.get(m).getNameOfComp().equals(tempSecondBucket.get(k))){
                compGainList.get(m).setGain(compGainList.get(m).getGain()-1);
              }
            }
          }
      	}
    	if(tempSecondBucket.isEmpty()){
          //gain--
          for(int k=0; k<tempFirstBucket.size(); k++){
            for(int m=0; m<compGainList.size(); m++){
              if(compGainList.get(m).getNameOfComp().equals(tempFirstBucket.get(k))){
                compGainList.get(m).setGain(compGainList.get(m).getGain()-1);
              }
            }
          }
      	}
      }
      /*sorting component gain to get maximum gain */
      ComponentGain temp = new ComponentGain();
      for(int pass=compGainList.size()-1;pass>=0;pass--){
        for(int i=0;i<pass;i++){
          if(compGainList.get(i).getGain()<compGainList.get(i+1).getGain()){
            temp.setNameOfComp(compGainList.get(i).getNameOfComp());
		    temp.setGain(compGainList.get(i).getGain());
		    compGainList.get(i).setNameOfComp(compGainList.get(i+1).getNameOfComp());
		    compGainList.get(i).setGain(compGainList.get(i+1).getGain());
            compGainList.get(i+1).setNameOfComp(temp.getNameOfComp());
		    compGainList.get(i+1).setGain(temp.getGain());
		  }
		}
      }
      /*swap the nodes considering area constraint*/
      for(int i=0; i<compGainList.size(); i++){
        if(firstBucket.contains(compGainList.get(i).getNameOfComp())){
          if((firstBucket.size()-1)>=areaConstraintMin){
            firstBucket.remove(compGainList.get(i).getNameOfComp());
		    secondBucket.add(compGainList.get(i).getNameOfComp());
		    break;
		  }          
		}
		else{
		  if((secondBucket.size()-1)>=areaConstraintMin){
		    secondBucket.remove(compGainList.get(i).getNameOfComp());
		    firstBucket.add(compGainList.get(i).getNameOfComp());
		    break;
		  }	
        }      
	  }
      /*calculate total gain for current combination*/
	  for(int i=0;i<compGainList.size();i++){
        totalCurrentGain = totalCurrentGain + compGainList.get(i).getGain();
      }
	}//while loop ends 
    firstBucket.clear();
    firstBucket.addAll(prevFirstBucket);
    secondBucket.clear();
    secondBucket.addAll(prevSecondBucket);
    }catch (Exception e) {
        System.err.println("Error: " + e.getMessage());
     }
  }
  public List<String> RemoveDuplicates(List<String> listName){
    Set<String> setItem = new LinkedHashSet<String>(listName);
    listName.clear();
    listName.addAll(setItem);
    return listName;
  }
  public float CalculateHpwl(String pcbFile){
    float minX, maxX, minY, maxY, hpwl=0;
    PcbParser pcbparser = new PcbParser(pcbFile);
    pcbparser.pcbParse();
    NetlistParser netparser = new NetlistParser(netlistFile);
    netparser.parse();
    for(int i=1; i<=netparser.netId;i++){ //netId = number of nets
      minX = minY = 9999.99f;
      maxX = maxY = 0;
      for(int j=0; j<netparser.netList.size(); j++){
        if(netparser.netList.get(j).getNetId()>i)
          break; //break from for-j
        if(netparser.netList.get(j).getNetId()==i){
          for(int k=0; k<pcbparser.moduleList.size(); k++){
            if(netparser.netList.get(j).getCompName().equals(pcbparser.moduleList.get(k).getModuleName())){
              if(pcbparser.moduleList.get(k).getPositionX() < minX){
                minX = pcbparser.moduleList.get(k).getPositionX();
              }
              if(pcbparser.moduleList.get(k).getPositionX() > maxX){
                maxX = pcbparser.moduleList.get(k).getPositionX();
              }
              if(pcbparser.moduleList.get(k).getPositionY() < minY){
                minY = pcbparser.moduleList.get(k).getPositionY();
              }
              if(pcbparser.moduleList.get(k).getPositionY() > maxY){
                maxY = pcbparser.moduleList.get(k).getPositionY();
              }
              break; //break from for-k
            }
          }
        }
      }
      hpwl = hpwl + (maxX-minX)+(maxY-minY);
    }
    return hpwl;
  }
}
