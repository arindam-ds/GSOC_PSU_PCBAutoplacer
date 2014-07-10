package edu.pdx.placer;

/**
 * @author Arindam Banerjee
 *
 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import edu.pdx.parser.*;
import edu.pdx.pcbparser.*;

public class Placer {

	/**
	 * @param args
	 */
  public static void main(String[] args) {
	  
    if (args.length != 2) {
	  System.err.println("Invalid command line, exactly two argument required to specify input file");
	  System.exit(1);
	}
    try{
    //FmVerticalPartitioner(args[0], args[1]);
    FmHorizontalPartitioner(args[0], args[1]);
    }catch(Exception e){
    	System.err.print(e.getMessage());
    }
  }
  public static void FmVerticalPartitioner(String args0, String args1){
    NetlistParser netparser = new NetlistParser(args0);
    netparser.parse();
    PcbParser pcbparser = new PcbParser(args1);
    pcbparser.pcbParse();
    Integer[] index = new Integer[netparser.netList.size()];
    int totalGain = 0;
    List<String> leftBucket = new ArrayList<String>();
    List<String> rightBucket = new ArrayList<String>();
    List<ComponentGain> compgain = new ArrayList<ComponentGain>();
    int gain=0;
    int areaConstraintMin = (netparser.numberOfComponents)*(40/100);
    //int areaConstraintMax = (netparser.numberOfComponents)*(60/100);
    //float verticalCutX = pcbparser.pcbBoardXmin +((pcbparser.pcbBoardXmax-pcbparser.pcbBoardXmin)/2);
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
    for(int j=0;j<netparser.netList.size();j++){
      index[j]=0;
    }
    for(int i=0;i<netparser.netList.size();i++){
      gain=0;
      if(index[i]==0){
        index[i]=1;
        for(int j=i+1;j<netparser.netList.size();j++){
    	  if(netparser.netList.get(j).compName.equals(netparser.netList.get(i).compName)&&(netparser.netList.get(j).netId==netparser.netList.get(i).netId)){
            index[j]=1;   		
    	  }
          if(!(netparser.netList.get(j).compName.equals(netparser.netList.get(i).compName))&&(netparser.netList.get(j).netId==netparser.netList.get(i).netId)){
            if((leftBucket.contains(netparser.netList.get(i).compName))&&(rightBucket.contains(netparser.netList.get(j).compName))){
        	  gain++;  
            }
            else if((leftBucket.contains(netparser.netList.get(j).compName))&&(rightBucket.contains(netparser.netList.get(i).compName))){
        	  gain++;  
            }
            else
              gain--;
          }
        }
        compgain.add(new ComponentGain(netparser.netList.get(i).compName, gain));
      }
    }
    for(int i=0;i<compgain.size();i++){
      System.out.println("Component: "+compgain.get(i).nameOfComp+" Gain: "+compgain.get(i).gain);	
    }
  }
  public static void FmHorizontalPartitioner(String args0, String args1){
    NetlistParser netparser = new NetlistParser(args0);
    netparser.parse();  
    int totalPrevGain = 999, totalCurrentGain=0;
    int numberOfPass=1;
    int gain=0;
    int areaConstraintMin = (netparser.numberOfComponents)*(40/100);
    //int areaConstraintMax = (netparser.numberOfComponents)*(60/100);
    List<String> leftBucket = new ArrayList<String>();
    List<String> rightBucket = new ArrayList<String>();
    List<ComponentGain> compGainList = new ArrayList<ComponentGain>();
    //float verticalCutX = pcbparser.pcbBoardXmin +((pcbparser.pcbBoardXmax-pcbparser.pcbBoardXmin)/2);
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
        System.out.println("Component: "+compGainList.get(i).nameOfComp+" Gain: "+compGainList.get(i).gain);	
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
  }
}
