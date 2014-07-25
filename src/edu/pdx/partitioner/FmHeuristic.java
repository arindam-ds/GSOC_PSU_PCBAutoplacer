/**
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
  int totalPrevGain, totalCurrentGain;
  int numberOfPass=1;
  int gain=0;
  int areaConstraintMin;
  List<String> leftBucket = new ArrayList<String>();
  List<String> rightBucket = new ArrayList<String>();
  List<String> topLeftBucket = new ArrayList<String>();
  List<String> bottomLeftBucket = new ArrayList<String>();
  List<String> topRightBucket = new ArrayList<String>();
  List<String> bottomRightBucket = new ArrayList<String>();
  List<ComponentGain> compGainList = new ArrayList<ComponentGain>();
  //float verticalCutX = pcbparser.pcbBoardXmin +((pcbparser.pcbBoardXmax-pcbparser.pcbBoardXmin)/2);
  public FmHeuristic(String netlistFile, String pcbFile){
    this.netlistFile = netlistFile;
    this.pcbFile = pcbFile;
  }
  public void FmVerticalPartitioner(){
    try{
    NetlistParser netparser = new NetlistParser(netlistFile);
	netparser.parse();  
	for(int i=0;i<(netparser.numberOfComponents/2);i++){
	  leftBucket.add(netparser.compList.get(i).nameOfComp);	
	}
	for(int i=(netparser.numberOfComponents/2);i<netparser.numberOfComponents;i++){
	  rightBucket.add(netparser.compList.get(i).nameOfComp);	
	}
	for(int j=0;j<leftBucket.size();j++)
	  System.out.println("test left "+leftBucket.get(j));
	for(int j=0;j<rightBucket.size();j++)
	  System.out.println("test right "+rightBucket.get(j));
	CorePartitioner(leftBucket, rightBucket);
	for(int j=0;j<leftBucket.size();j++)
      System.out.println("test left after:"+leftBucket.get(j));
    for(int j=0;j<rightBucket.size();j++)
      System.out.println("test right after:"+rightBucket.get(j));
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
  }//FmHorizontalPartitioner() ends
  public void CorePartitioner(List<String> firstBucket, List<String> secondBucket){
    try{
    List<String> tempFirstBucket = new ArrayList<String>();
    List<String> tempSecondBucket = new ArrayList<String>();
    NetlistParser netparser = new NetlistParser(netlistFile);
    netparser.parse();  
    totalPrevGain = 999;
    totalCurrentGain=998;
    areaConstraintMin = (netparser.numberOfComponents * 40)/100;
    //int areaConstraintMax = (netparser.numberOfComponents)*(60/100);
    while((totalCurrentGain < totalPrevGain)&&(numberOfPass<=netparser.compList.size())){
	  numberOfPass++;
      totalPrevGain = totalCurrentGain;
      totalCurrentGain=0;
      compGainList.clear();
      /*Gain calculation*/
      for(int i=0; i<netparser.compList.size(); i++){
        compGainList.add(new ComponentGain(netparser.compList.get(i).nameOfComp, 0));
      }
      for(int i=1;i<=netparser.netId;i++){ //netId = number of nets
    	tempFirstBucket.clear();
    	tempSecondBucket.clear();
    	for(int j=0;j<netparser.netList.size();j++){
          //System.out.println(netList.get(j).netId+" "+netList.get(j).compName+" "+netList.get(j).pin);
    	  if(netparser.netList.get(j).netId == i){
            if(firstBucket.contains(netparser.netList.get(j).compName)){
              tempFirstBucket.add(netparser.netList.get(j).compName);
            }
            else{
              tempSecondBucket.add(netparser.netList.get(j).compName);
            }
    	  }
    	}
    	if(tempFirstBucket.size()==1){
          //gain++
          for(int m=0; m<=compGainList.size();m++){
            if(compGainList.get(m).nameOfComp.equals(tempFirstBucket.get(0))){
              compGainList.get(m).gain++;
              break;
            }
    	  }
    	}
    	if(tempSecondBucket.size()==1){
          //gain++
          for(int m=0; m<=compGainList.size();m++){
            if(compGainList.get(m).nameOfComp.equals(tempSecondBucket.get(0))){
              compGainList.get(m).gain++;
              break;
            }
      	  }
        }
        if(tempFirstBucket.isEmpty()){
          //gain--
          for(int k=0; k<tempSecondBucket.size(); k++){
            for(int m=0; m<compGainList.size(); m++){
              if(compGainList.get(m).nameOfComp.equals(tempSecondBucket.get(k))){
                compGainList.get(m).gain--;
              }
            }
          }
      	}
    	if(tempSecondBucket.isEmpty()){
          //gain--
          for(int k=0; k<tempFirstBucket.size(); k++){
            for(int m=0; m<compGainList.size(); m++){
              if(compGainList.get(m).nameOfComp.equals(tempFirstBucket.get(k))){
                compGainList.get(m).gain--;
              }
            }
          }
      	}
      }
      /*sorting component gain to get maximum gain */
      ComponentGain temp = new ComponentGain();
      for(int pass=compGainList.size()-1;pass>=0;pass--){
        for(int i=0;i<pass;i++){
          if(compGainList.get(i).gain<compGainList.get(i+1).gain){
            temp.nameOfComp = compGainList.get(i).nameOfComp;
		    temp.gain = compGainList.get(i).gain;
		    compGainList.get(i).nameOfComp = compGainList.get(i+1).nameOfComp;
		    compGainList.get(i).gain = compGainList.get(i+1).gain;
            compGainList.get(i+1).nameOfComp = temp.nameOfComp;
		    compGainList.get(i+1).gain = temp.gain;
		  }
		}
      }
      /*swap the nodes considering area constraint*/
      for(int i=0; i<compGainList.size(); i++){
        if(firstBucket.contains(compGainList.get(i).nameOfComp)){
          if((firstBucket.size()-1)>=areaConstraintMin){
            firstBucket.remove(compGainList.get(i).nameOfComp);
		    secondBucket.add(compGainList.get(i).nameOfComp);
		    break;
		  }          
		}
		else{
		  if((secondBucket.size()-1)>=areaConstraintMin){
		    secondBucket.remove(compGainList.get(i).nameOfComp);
		    firstBucket.add(compGainList.get(i).nameOfComp);
		    break;
		  }	
        }      
	  }
      /*calculate total gain for current combination*/
	  for(int i=0;i<compGainList.size();i++){
        totalCurrentGain = totalCurrentGain + compGainList.get(i).gain;
      }
	}//while loop ends 
    for(int i=0;i<compGainList.size();i++){
      System.out.println("FmH Component: "+compGainList.get(i).nameOfComp+" FmH Gain: "+compGainList.get(i).gain);	
    }
    }catch (Exception e) {
        System.err.println("Error: " + e.getMessage());
      }
  }
}