package edu.pdx.placer;

/**
 * @author Arindam Banerjee
 *
 */

import java.util.ArrayList;
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
    System.out.println(args[0]);
    System.out.println(args[1]);
    FmVerticalPartitioner(args[0], args[1]);
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
    List<String> leftBucket = new ArrayList<String>();
    List<String> rightBucket = new ArrayList<String>();
    List<ComponentGain> compgain = new ArrayList<ComponentGain>();
    int gain=0;
    int areaConstraintMin = (netparser.numberOfComponents)*(40/100);
    int areaConstraintMax = (netparser.numberOfComponents)*(60/100);
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
	PcbParser pcbparser = new PcbParser(args1);
	pcbparser.pcbParse();
	Integer[] index = new Integer[netparser.netList.size()];
	List<String> upBucket = new ArrayList<String>();
	List<String> downBucket = new ArrayList<String>();
	List<ComponentGain> compgain = new ArrayList<ComponentGain>();
	int gain=0;
	int areaConstraintMin = (netparser.numberOfComponents)*(40/100);
	int areaConstraintMax = (netparser.numberOfComponents)*(60/100);
	//float verticalCutX = pcbparser.pcbBoardXmin +((pcbparser.pcbBoardXmax-pcbparser.pcbBoardXmin)/2);
	for(int i=0;i<(netparser.numberOfComponents/2);i++){
	  upBucket.add(netparser.compList.get(i).nameOfComp);	
	}
	for(int i=(netparser.numberOfComponents/2);i<netparser.numberOfComponents;i++){
	  downBucket.add(netparser.compList.get(i).nameOfComp);	
	}  
	for(int j=0;j<upBucket.size();j++)
	  System.out.println("test left "+upBucket.get(j));
	for(int j=0;j<downBucket.size();j++)
	  System.out.println("test right "+downBucket.get(j));
	for(int j=0;j<netparser.netList.size();j++){
	  index[j]=0;
    }
	for(int i=0;i<netparser.netList.size();i++){
	  if(index[i]==0){
	    index[i]=1;
	    for(int j=i+1;j<netparser.netList.size();j++){
	      if(netparser.netList.get(j).compName.equals(netparser.netList.get(i).compName)&&(netparser.netList.get(j).netId==netparser.netList.get(i).netId)){
	        index[j]=1;   		
	      }
	      if(!(netparser.netList.get(j).compName.equals(netparser.netList.get(i).compName))&&(netparser.netList.get(j).netId==netparser.netList.get(i).netId)){
	        if((upBucket.contains(netparser.netList.get(i).compName))&&(downBucket.contains(netparser.netList.get(j).compName))){
	    	  gain++;  
	        }
	        else if((upBucket.contains(netparser.netList.get(j).compName))&&(downBucket.contains(netparser.netList.get(i).compName))){
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
}
