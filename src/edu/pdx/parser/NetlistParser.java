package edu.pdx.parser;

import java.io.*;
import java.util.*;

public class NetlistParser {
	
  private int numberOfComponents, start, end, numOfPins, netId, pin;
  private List<Components> compList = new ArrayList<Components>();
  private List<Nets> netList = new ArrayList<Nets>();
  private String netlistInputFile, strLine, compName = null, partName = null, libPartName = null, netCompName = null;
    
  public NetlistParser(String netlistInputFile) {
    this.netlistInputFile =  netlistInputFile;     	
  }
  
  public void parse() {
    try {
      FileInputStream fstream = new FileInputStream(netlistInputFile);
      BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
      while ((strLine = br.readLine()) != null) {
        if (strLine.contains("comp ")){
          numberOfComponents++;
          start = strLine.indexOf("ref ") + 4;
          end = start;
          for (final char c : strLine.substring(start).toCharArray()) {
            if (c == ')') {
              break;
            }
            ++end;
          }				
          compName = strLine.substring(start, end);
          for (int i=0;i<2;i++)
            strLine = br.readLine();
				
          if (strLine.contains("part ")) {
            start = strLine.indexOf("part ") + 5;
            end = start;
            for (final char c : strLine.substring(start).toCharArray()) {
              if (c == ')') {
                break;
              }
              ++end;
            }
           partName = strLine.substring(start, end);
           compList.add(new Components(compName,partName,0));
          }//if contains part
        }	//if contains comp

        if (strLine.contains("libpart ")) {
          start = strLine.indexOf("(part ") + 6;
          end = start;
          for (final char c : strLine.substring(start).toCharArray()) {
            if (c == ')') {
              break;
            }
            ++end;
          }
          //System.out.println(strLine.substring(start, end));
          libPartName = strLine.substring(start, end);	
          numOfPins=0;
        }
        if (strLine.contains("pin (")) {
          numOfPins++;
        }
        for (int j=0;j<compList.size();j++) {
          if (compList.get(j).nameOfCompPart.equalsIgnoreCase(libPartName) && numOfPins>0) {
            compList.get(j).numOfPin = numOfPins;
          }
         }
         if (strLine.contains("(net ")) {
           netId++;
         }
         if (strLine.contains("(node ")) {
           start = strLine.indexOf("(ref ") + 5;
           end = start;
           for (final char c : strLine.substring(start).toCharArray()) {
             if (c == ')') {
               break;
             }
             ++end;
           }
		   //System.out.println(strLine.substring(start, end));
           netCompName = strLine.substring(start, end);
           start = strLine.indexOf("(pin ") + 5;
           end = start;
           for (final char c : strLine.substring(start).toCharArray()) {
             if (c == ')') {
               break;
             }
            ++end;
           }
           pin = Integer.parseInt(strLine.substring(start, end));
           netList.add(new Nets(netId, netCompName, pin));
         }
       }//end of while ((strLine = br.readLine()) != null)
       //Close the input stream
       br.close();			
       /*testing
       System.out.println (numberOfComponents);
       for(int j=0;j<compList.size();j++)
         System.out.println(compList.get(j).nameOfComp+" "+compList.get(j).nameOfCompPart+" "+compList.get(j).numOfPin);
			
       for(int j=0;j<netList.size();j++)
         System.out.println(netList.get(j).netId+" "+netList.get(j).compName+" "+netList.get(j).pin);
       */
    } catch (Exception e) {
        System.err.println("Error: " + e.getMessage());
      }
   }
}   