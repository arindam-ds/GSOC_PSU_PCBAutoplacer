package edu.pdx.pcbparser;

import java.io.*;
import java.util.*;

public class PcbParser {
	private String pcbInputFile, strLine="", layerName="", layerType="", netName="", moduleType="", moduleLayer="",moduleName="";
	private List<PcbLayers> layerList = new ArrayList<PcbLayers>();
	private List<PcbNets> netList = new ArrayList<PcbNets>();
	private int layerId=0, numberOfNets=0, netId=0, moduleId=0, angleZ=0;
	private float positionX=0.00f, positionY=0.00f, componentWidth=0.00f,componentHeight=0.00f;
	private boolean isLayer = true, isNets = true;
	BufferedReader br;
	FileInputStream fstream;
	
	public PcbParser(String pcbInputFile) {
      this.pcbInputFile =  pcbInputFile;     	
	}
	
	public void pcbParse(){
      try{
        fstream = new FileInputStream(pcbInputFile);
        br = new BufferedReader(new InputStreamReader(fstream));  
        while ((strLine = br.readLine()) != null) {
          if (strLine.contains("(nets ")){
            int start = strLine.indexOf("(nets ") + 6;
            int end = start;
            for (final char c : strLine.substring(start).toCharArray()) {
              if (c == ')') {
                break;
              }
              ++end;
            }				
            numberOfNets = Integer.parseInt(strLine.substring(start, end));         
          }
          if (strLine.contains("(layers") && isLayer){
            getLayers();         
          }
          if (strLine.contains("(net 0") && isNets){
            getNets();         
          }
          if (strLine.contains("(module ")){
            getModules();         
          }
        }
        br.close();
        //testing
        for(int j=0;j<netList.size();j++)
            System.out.println(netList.get(j).netId+" "+netList.get(j).netName+" "+"\n");
        //System.out.println("numberOfNets  ::"+numberOfNets);
      }
      catch (Exception e){
    	  System.err.println("Error: " + e.getMessage());
      }
	}
	
	public void getLayers(){
      isLayer = false;
      for(int i=0; i<15; i++){
        try {
          strLine = br.readLine();
          StringTokenizer stringTokenizer = new StringTokenizer(strLine, "() ");
          while(stringTokenizer.hasMoreTokens()){
            layerId = Integer.parseInt(stringTokenizer.nextElement().toString());
            layerName = stringTokenizer.nextElement().toString();
            layerType = stringTokenizer.nextElement().toString();
            layerList.add(new PcbLayers(layerId, layerName, layerType));
          }
        }
		catch (Exception e) {
			e.printStackTrace();
		}
	   }//for loop
	}
	public void getNets(){
      isNets = false;
      for(int i=0; i<numberOfNets; i++){
        try {
          StringTokenizer stringTokenizer = new StringTokenizer(strLine, "() ");
          while(stringTokenizer.hasMoreTokens()){
        	String net = stringTokenizer.nextElement().toString();
            netId = Integer.parseInt(stringTokenizer.nextElement().toString());
            netName = stringTokenizer.nextElement().toString();
            netList.add(new PcbNets(netId, netName));
          }
          strLine = br.readLine();
        }
    	catch (Exception e) {
          e.printStackTrace();
        }
      }
	}
	public void getModules(){
    
    }
}
