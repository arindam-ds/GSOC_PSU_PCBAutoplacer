package edu.pdx.pcbparser;

import java.io.*;
import java.util.*;

public class PcbParser {
  String pcbInputFile, strLine="", layerName="", layerType="", netName="", moduleType="", moduleLayer="",moduleName="";
  String startIndex="";
  char endChar='a';
  List<PcbLayers> layerList = new ArrayList<PcbLayers>();
  List<PcbNets> netList = new ArrayList<PcbNets>();
  List<PcbModules> moduleList = new ArrayList<PcbModules>();
  List<Float> dimensionX = new ArrayList<Float>(); 
  List<Float> dimensionY = new ArrayList<Float>();
  int layerId=0, numberOfNets=0, netId=0, moduleId=0, angleZ=0, numIndex=0, padAngleZ=0, padId=0, READFLAG=1;
  float positionX=0.00f, positionY=0.00f, componentWidth=0.00f,componentHeight=0.00f, padX=0.00f, padY=0.00f,padWidth=0.00f, padHeight=0.00f;
  float pcbBoardX1, pcbBoardX2, pcbBoardY1, pcbBoardY2;
  boolean isLayer = true, isNets = true;
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
      for(int j=0;j<moduleList.size();j++)
        System.out.println(moduleList.get(j).moduleId+" "+moduleList.get(j).moduleName+" "+moduleList.get(j).pad.size()+"\n");
        //System.out.println("numberOfNets  ::"+numberOfNets);
      System.out.println("pcbBoardX1: "+pcbBoardX1+" "+pcbBoardX2+" "+pcbBoardY1+" "+pcbBoardY2);
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
          widths.add(Float.parseFloat(position[0]));
          heights.add(Float.parseFloat(position[1]));
        }
        if(strLine.contains("pad")){
          pads = new Pads();
          pads.padId = Integer.parseInt(getSubstring("(pad ", ' ', 5));
          coords = getSubstring("(at ", ')', 4).split(" ");
          pads.padX = Float.parseFloat(coords[0]);
          pads.padY = Float.parseFloat(coords[1]);
          if(coords.length==3)
            pads.padAngleZ = Integer.parseInt(coords[2]);
          coords = getSubstring("(size ", ')', 6).split(" ");
          pads.padWidth = Float.parseFloat(coords[0]);
          pads.padHeight = Float.parseFloat(coords[1]);
          strLine = br.readLine();
          strLine = br.readLine();
          padToNet = getSubstring("(net ", ')', 5).split(" ");
          netId = Integer.parseInt(padToNet[0]);
          netName = padToNet[1];
          strLine = br.readLine();
          strLine = br.readLine();
          if(!strLine.contains("pad")){
            pads.net = new PcbNets(netId, netName);
            modules.pad.add(pads);
            break;
          }
          else
            READFLAG=0;
          //PcbNets pcbNets = new PcbNets(netId, netName);
          pads.net = new PcbNets(netId, netName);
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
      if(widths.get(heights.size()-1) == heights.get(0)){
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
    Collections.sort(dimensionX);
    Collections.sort(dimensionY);
    pcbBoardX1 = dimensionX.get(0);
    pcbBoardX2 = dimensionX.get(dimensionX.size()-1);
    pcbBoardY1 = dimensionY.get(0);
    pcbBoardY2 = dimensionY.get(dimensionY.size()-1);
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
