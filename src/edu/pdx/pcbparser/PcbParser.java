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

import java.io.*;
import java.util.*;

public class PcbParser {
  public String pcbInputFile, strLine="", layerName="", layerType="", netName="", moduleType="", moduleLayer="",moduleName="";
  public String startIndex="";
  char endChar='a';
  public List<PcbLayers> layerList = new ArrayList<PcbLayers>();
  public List<PcbNets> netList = new ArrayList<PcbNets>();
  public List<PcbModules> moduleList = new ArrayList<PcbModules>();
  public List<Float> dimensionX = new ArrayList<Float>(); 
  public List<Float> dimensionY = new ArrayList<Float>();
  public int layerId=0, numberOfNets=0, netId=0, moduleId=0, angleZ=0, numIndex=0, padAngleZ=0, padId=0, READFLAG=1;
  public float positionX=0.00f, positionY=0.00f, componentWidth=0.00f,componentHeight=0.00f, padX=0.00f, padY=0.00f,padWidth=0.00f, padHeight=0.00f;
  public float pcbBoardXmin, pcbBoardXmax, pcbBoardYmin, pcbBoardYmax, radiusX=0, radiusY=0;
  boolean isLayer = true, isNets = true;
  BufferedReader br;
  FileInputStream fstream;
  public PcbParser(String pcbInputFile) {
    this.pcbInputFile =  pcbInputFile;     	
  }
  public PcbParser() {    	
  }
	
  public void pcbParse(){
    try{
      fstream = new FileInputStream(pcbInputFile);
      br = new BufferedReader(new InputStreamReader(fstream));  
      while ((strLine = br.readLine()) != null) {
        if (strLine.contains("(nets ")){
          startIndex = "(nets ";
          endChar = ')';
          numIndex = 6;
          numberOfNets = Integer.parseInt(getSubstring(startIndex,endChar,numIndex));
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
        if (strLine.contains("(gr_line ")){
          getDimensions(); 
          getPcbDimensions();
        }
      }
      br.close();
      //testing
      /*for(int j=0;j<moduleList.size();j++)
          System.out.println(moduleList.get(j).moduleName+" "+moduleList.get(j).positionX+" "+moduleList.get(j).positionY+"\n");
        System.out.println("numberOfNets  ::"+numberOfNets);
        System.out.println("pcbBoardX1_: "+pcbBoardXmin+" "+pcbBoardXmax+" "+"pcbBoardY1_: "+pcbBoardYmin+" "+pcbBoardYmax);*/
      }catch (Exception e){
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
      }catch (Exception e) {
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
      }catch (Exception e) {
        e.printStackTrace();
      }
    }
  }
  public void getModules(){
    try {
      PcbModules modules = new PcbModules();
      List<Float> widths = new ArrayList<Float>();
      List<Float> heights = new ArrayList<Float>();
      Pads pads;
      modules.moduleId = moduleId;
      moduleId++;
      modules.moduleType = getSubstring("(module ",' ',8);
      modules.moduleLayer = getSubstring("(layer ",')',7); 
      strLine = br.readLine();
      String[] coords = getSubstring("(at ", ')', 4).split(" ");
      String[] padToNet;
      modules.positionX = Float.parseFloat(coords[0]);
      modules.positionY = Float.parseFloat(coords[1]);
      if(coords.length==3)
        modules.angleZ = Integer.parseInt(coords[2]);
      modules.pad = new ArrayList<Pads>();
      while(true){
        String[] position;
		if(READFLAG==1)
          strLine = br.readLine();
        if(strLine.contains("fp_text reference")){
          modules.moduleName = getSubstring("reference ",' ', 10);
        }
        /*if(strLine.contains("fp_text value")){
          modules.moduleNameValue = getSubstring("value ",' ', 6);
        }*/ //future use
        if(strLine.contains("fp_line")){
          position = getSubstring("(start ",')', 7).split(" ");
          widths.add(Float.parseFloat(position[0]));
          heights.add(Float.parseFloat(position[1]));
          position = getSubstring("(end ",')', 5).split(" ");
          widths.add(Float.parseFloat(position[0]));
          heights.add(Float.parseFloat(position[1]));
        }
        if(strLine.contains("fp_circle")){
          position = getSubstring("(end ",')', 5).split(" ");
          radiusX=Float.parseFloat(position[0]);
          radiusY=Float.parseFloat(position[1]);
          radiusX=radiusX*radiusX;
          radiusY=radiusY*radiusY;
          widths.add((float) (2*(Math.sqrt(radiusX+radiusY))));
          heights.add((float) (2*(Math.sqrt(radiusX+radiusY))));
        }
        if(strLine.contains("pad")){
          pads = new Pads();
          pads.setPadId(Integer.parseInt(getSubstring("(pad ", ' ', 5)));
          coords = getSubstring("(at ", ')', 4).split(" ");
          pads.setPadX(Float.parseFloat(coords[0]));
          pads.setPadY(Float.parseFloat(coords[1]));
          if(coords.length==3)
            pads.setPadAngleZ(Integer.parseInt(coords[2]));
          coords = getSubstring("(size ", ')', 6).split(" ");
          pads.setPadWidth(Float.parseFloat(coords[0]));
          pads.setPadHeight(Float.parseFloat(coords[1]));
          strLine = br.readLine();
          strLine = br.readLine();
          padToNet = getSubstring("(net ", ')', 5).split(" ");
          netId = Integer.parseInt(padToNet[0]);
          netName = padToNet[1];
          strLine = br.readLine();
          strLine = br.readLine();
          if(!strLine.contains("pad")){
            pads.setNet(new PcbNets(netId, netName));
            modules.pad.add(pads);
            break;
          }
          else
            READFLAG=0;
          pads.setNet(new PcbNets(netId, netName));
          modules.pad.add(pads);
        }
      }//end while(true)		
      Collections.sort(widths);
      Collections.sort(heights);
	  //System.out.println("Max: "+widths.get(widths.size()-1));   // Last element
	  //System.out.println("Min: "+widths.get(0));
      if(widths.get(widths.size()-1) == widths.get(0)){
        modules.componentWidth = widths.get(0);
      }
      else
        modules.componentWidth = widths.get(widths.size()-1) - widths.get(0);
      if(heights.get(heights.size()-1) == heights.get(0)){
        modules.componentHeight = heights.get(0);
      }
      else
        modules.componentHeight = heights.get(heights.size()-1) - heights.get(0);		
      moduleList.add(modules);
      READFLAG=1;
	}catch (Exception e) {
      e.getCause();
      System.err.println("Error_: " + e.getMessage());
      e.printStackTrace();
	}
  }
  public void getDimensions(){
    /*This method gets the dimension data from file and feeds to data structure*/
    try{
      String[] coord;
      coord = getSubstring("(start ", ')', 7).split(" ");
      dimensionX.add(Float.parseFloat(coord[0]));
      dimensionY.add(Float.parseFloat(coord[1]));
      coord = getSubstring("(end ", ')', 5).split(" ");
      dimensionX.add(Float.parseFloat(coord[0]));
      dimensionY.add(Float.parseFloat(coord[1]));
    }catch (Exception e){
      System.err.println("Error_: " + e.getMessage());
      e.printStackTrace();
    }
  }
  public void getPcbDimensions(){
    /*this method computes the total dimension of the PCB*/
    Collections.sort(dimensionX);
    Collections.sort(dimensionY);
    pcbBoardXmin = dimensionX.get(0);
    pcbBoardXmax = dimensionX.get(dimensionX.size()-1);
    pcbBoardYmin = dimensionY.get(0);
    pcbBoardYmax = dimensionY.get(dimensionY.size()-1);
  }
  public String getSubstring(String startIndex, char endChar, int numIndex){
    int start = strLine.indexOf(startIndex) + numIndex;
    int end = start;
    for (final char c : strLine.substring(start).toCharArray()) {
      if (c == endChar) {
        break;
      }
      ++end;
    }				
    return (strLine.substring(start, end));
  }
}
