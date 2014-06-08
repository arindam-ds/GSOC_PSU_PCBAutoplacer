package edu.pdx.pcbparser;

import java.io.*;
import java.util.*;

public class PcbParser {
	private String pcbInputFile, strLine, layerName="", layerType="";
	List<PcbLayers> layerList = new ArrayList<PcbLayers>();
	int layerId=0;
	
	public PcbParser(String pcbInputFile) {
      this.pcbInputFile =  pcbInputFile;     	
	}
	
	public void pcbParse(){
      try{
        FileInputStream fstream = new FileInputStream(pcbInputFile);
        BufferedReader br = new BufferedReader(new InputStreamReader(fstream));  
        while ((strLine = br.readLine()) != null) {
        	if (strLine.contains("(layers")){
              for(int i=0; i<15; i++){
            	strLine = br.readLine();
            	StringTokenizer stringTokenizer = new StringTokenizer(strLine, "() ");
            	while(stringTokenizer.hasMoreTokens()){
        			layerId = Integer.parseInt(stringTokenizer.nextElement().toString());
        			layerName = stringTokenizer.nextElement().toString();
        			layerType = stringTokenizer.nextElement().toString();
        			layerList.add(new PcbLayers(layerId, layerName, layerType));
                }
              }
        	}
        }
        br.close();
        for(int j=0;j<layerList.size();j++)
            System.out.println(layerList.get(j).layerId+" "+layerList.get(j).layerName+" "+layerList.get(j).layerType+"\n");
        
      }
      catch (Exception e){
    	  System.err.println("Error: " + e.getMessage());
      }
	}
}
