package edu.pdx.parser;

import java.io.*;
import java.util.*;

public class NetlistParser {
    int numberOfComponents, start, end, numOfPins, netId, pin;
    String netlistInputFile = "Input", componentName, partName, netComponentName;
    {
     try{
    	FileInputStream fstream = new FileInputStream(netlistInputFile);
     }
     catch(Exception e){
    	System.err.println("Error: " + e.getMessage());
     }
    
    }
}