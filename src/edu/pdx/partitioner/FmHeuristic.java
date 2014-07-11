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
  String args0="";
  String args1="";
  int totalPrevGain = 999, totalCurrentGain=0;
  int numberOfPass=1;
  int gain=0;
  List<String> leftBucket = new ArrayList<String>();
  List<String> rightBucket = new ArrayList<String>();
  List<ComponentGain> compGainList = new ArrayList<ComponentGain>();
  //float verticalCutX = pcbparser.pcbBoardXmin +((pcbparser.pcbBoardXmax-pcbparser.pcbBoardXmin)/2);
  public FmHeuristic(String args0, String args1){
    this.args0 = args0;
    this.args1 = args1;
  }
  public void FmVerticalPartitioner(){
    NetlistParser netparser = new NetlistParser(args0);
	netparser.parse();  
	int areaConstraintMin = (netparser.numberOfComponents)*(40/100);
	//int areaConstraintMax = (netparser.numberOfComponents)*(60/100);
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
	while((totalCurrentGain < totalPrevGain)&&(numberOfPass<=netparser.compList.size())){
	  numberOfPass++;
	  totalPrevGain = totalCurrentGain;
	  /*Gain calculation*/
	  for(int i=0;i<netparser.compToNetList.size();i++){
	    gain=0;
	    totalCurrentGain=0;
	    for(int n=0;n<netparser.compToNetList.get(i).netIdList.size();n++){
	      for(int j=i+1;j<netparser.compToNetList.size();j++){
	        if(netparser.compToNetList.get(j).netIdList.contains(netparser.compToNetList.get(i).netIdList.get(n))){
	          if(leftBucket.contains(netparser.compToNetList.get(i).nameOfComp) && rightBucket.contains(netparser.compToNetList.get(j).nameOfComp)){
	            gain++;
	          }
	          else if(rightBucket.contains(netparser.compToNetList.get(i).nameOfComp) && leftBucket.contains(netparser.compToNetList.get(j).nameOfComp)){
	            gain++;
	          }
	          else
	            gain--;
	        }
	      }
	    }
	    compGainList.add(new ComponentGain(netparser.netList.get(i).compName, gain));
	  }
	  for(int i=0;i<compGainList.size();i++){
	    System.out.println("FmH Component: "+compGainList.get(i).nameOfComp+" FmH Gain: "+compGainList.get(i).gain);	
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
	    if(leftBucket.contains(compGainList.get(i).nameOfComp)){
	      if((leftBucket.size()-1)>=areaConstraintMin){
	        leftBucket.remove(compGainList.get(i).nameOfComp);
	        rightBucket.add(compGainList.get(i).nameOfComp);
	        break;
	      }          
	    }
	    else{
	      if((rightBucket.size()-1)>=areaConstraintMin){
	        rightBucket.remove(compGainList.get(i).nameOfComp);
	        leftBucket.add(compGainList.get(i).nameOfComp);
	        break;
	      }	
	    }      
      }
	  /*calculate total gain for current combination*/
	  for(int i=0;i<compGainList.size();i++){
	    totalCurrentGain = totalCurrentGain + compGainList.get(i).gain;
      }
    }//while loop ends
  }//FmVerticalPartitioner() ends
}