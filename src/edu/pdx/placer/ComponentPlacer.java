/*
 * Copyright 2014 Arindam Bannerjee
 * This work is distributed under the terms of the "MIT license". Please see the file
 * LICENSE in this distribution for license terms.
 *
 */

package edu.pdx.placer;

import java.util.*;
import java.io.*;
import java.lang.*;

import edu.pdx.*;
import edu.pdx.pcbparser.*;

public class ComponentPlacer {
  List<String> componentList;
  List<PcbModules> moduleList;
  List<ComponentSize> compSizeList = new ArrayList<ComponentSize>();
  ComponentSize cs;
  int PLACEMENT;
  int grid[][];
  float centerOfPcbX, centerOfPcbY;
  float pcbBoardXmax, pcbBoardXmin, pcbBoardYmax, pcbBoardYmin;
  String pcbFile;
  public ComponentPlacer(String pcbFile, List<String> componentList, List<PcbModules>moduleList, int PLACEMENT){
    this.pcbFile = pcbFile;
    this.componentList = componentList;
    this.moduleList = moduleList;
    this.PLACEMENT = PLACEMENT;
  }
  public void placer(){
    try{
      //System.out.println(grid.length);
      GetComponentSize(moduleList);
      ArrangeComponents();
      PcbParser pp = new PcbParser(pcbFile);
      pp.pcbParse();
      SetCenterOfPcb(pp);
      SetDimensionOfPcb(pp);
      grid = new int[(int)(pcbBoardXmax-pcbBoardXmin)+1][(int)(pcbBoardYmax-pcbBoardYmin)+1];
      switch(PLACEMENT){
        case 1:
          PlaceTopLeftPartition();
          break;
        case 2:
          PlaceTopRightPartition();
          break;
        case 3:
          PlaceBottomLeftPartition();
          break;
        case 4:
          PlaceBottomRightPartition();
          break;
      }
      if(PLACEMENT==4){
        GenerateFinalPlacementFile(moduleList);
      }
    }catch(Exception e){
      System.err.println("Error_: " + e.getMessage());
    }
  }
  public void GetComponentSize(List<PcbModules>moduleList){
  /*This method will calculate component width, height from module list and will add that in component size list*/ 
    try{
      compSizeList.clear();
      for(int i=0; i<componentList.size(); i++){
        cs = new ComponentSize();
        for(int j=0; j<moduleList.size();j++){
          if(componentList.get(i).equals(moduleList.get(j).moduleName)){
            cs.compName = componentList.get(i);
            cs.width = (int) Math.ceil(moduleList.get(j).componentWidth) + 2;//2 is added to spare white space
            cs.height = (int) Math.ceil(moduleList.get(j).componentHeight) + 2;
            cs.size = cs.width * cs.height;
            cs.compCenterX = cs.compCenterY = 0.00f;
            cs.angleZ = moduleList.get(j).angleZ;
            compSizeList.add(cs);
            break;
          }
        }
      }
    }catch(Exception e){
      System.err.println("Error_: " + e.getMessage());
    }
  }
  public void ArrangeComponents(){ 
  /* This method will arrange components in ComponentSize list in decreasing order of their size*/
  /*sorting component gain to get maximum gain */
    try{
      ComponentSize temp = new ComponentSize();
      for(int pass=compSizeList.size()-1;pass>=0;pass--){
        for(int i=0;i<pass;i++){
          if(compSizeList.get(i).size<compSizeList.get(i+1).size){
            temp.compName = compSizeList.get(i).compName;
  		  temp.width = compSizeList.get(i).width;
  		  temp.height = compSizeList.get(i).height;
  		  temp.size = compSizeList.get(i).size;
  		  compSizeList.get(i).compName = compSizeList.get(i+1).compName;
  		  compSizeList.get(i).width = compSizeList.get(i+1).width;
  		  compSizeList.get(i).height = compSizeList.get(i+1).height;
  		  compSizeList.get(i).size = compSizeList.get(i+1).size;
  		  compSizeList.get(i+1).compName = temp.compName;
  		  compSizeList.get(i+1).width = temp.width;
  		  compSizeList.get(i+1).height = temp.height;
  		  compSizeList.get(i+1).size = temp.size;
  		}
        }
      }
    }catch(Exception e){
      System.err.println("Error_: " + e.getMessage());
    }
  }
  public void SetCenterOfPcb(PcbParser pp){
    try{
      centerOfPcbX = (float) Math.floor(((pp.pcbBoardXmax - pp.pcbBoardXmin)/2) + pp.pcbBoardXmin);
      centerOfPcbY = (float) Math.floor(((pp.pcbBoardYmax - pp.pcbBoardYmin)/2) + pp.pcbBoardYmin);
    }catch(Exception e){
      System.err.println("Error_: " + e.getMessage());
    }
  }
  public void SetDimensionOfPcb(PcbParser pp){
    try{
      pcbBoardXmax = (float) Math.floor(pp.pcbBoardXmax);
      pcbBoardXmin = (float) Math.floor(pp.pcbBoardXmin);
      pcbBoardYmax = (float) Math.floor(pp.pcbBoardYmax);
      pcbBoardYmin = (float) Math.floor(pp.pcbBoardYmin);
    }catch(Exception e){
      System.err.println("Error_: " + e.getMessage());
    }
  }
  public void PlaceTopLeftPartition(){
	int startX = (int)centerOfPcbX, endX = (int)pcbBoardXmin, startY = (int)centerOfPcbY, endY = (int)pcbBoardYmin; 
	int count = 0, PLACED, ROTATE, a, b;
	boolean ret;
	try{
      while(!compSizeList.isEmpty()){
        PLACED=0;
        ROTATE=0;
        for(int y=startY; y>=endY; y--){
          for(int x=startX; x>=endX; x--){
            ret = false;
            a=x-endX;
            b=y-endY;
            if(grid[a][b]==0){
              ret = checkAvailablityTopLeft(a,b,compSizeList.get(count).width,compSizeList.get(count).height);
            }
            if(ret == true){
              compSizeList.get(count).compCenterX = (x - compSizeList.get(count).width/2);
              compSizeList.get(count).compCenterY = (y - compSizeList.get(count).height/2);
              PLACED=1;
              break;
            }
          }
          if(PLACED==1)
            break;
        }
        if(PLACED==1 || ROTATE==1){
          if(PLACED==0 && ROTATE==1){
            ROTATE=0;
          }
          else{
            for(int m=0; m<moduleList.size(); m++)
            {
              if(compSizeList.get(count).compName.equals(moduleList.get(m).moduleName)){
                moduleList.get(m).positionX = compSizeList.get(count).compCenterX;
                moduleList.get(m).positionY = compSizeList.get(count).compCenterY;
                moduleList.get(m).angleZ = moduleList.get(m).angleZ+compSizeList.get(count).angleZ;
                break;
              }
            }
            compSizeList.remove(count);
            ROTATE=0;	
          }
        }
        else{
          if(ROTATE==0){
            ROTATE=1;
            compSizeList.get(count).height = compSizeList.get(count).height + compSizeList.get(count).width;
            compSizeList.get(count).width = compSizeList.get(count).height - compSizeList.get(count).width;
            compSizeList.get(count).height = compSizeList.get(count).height - compSizeList.get(count).width;
            compSizeList.get(count).angleZ = compSizeList.get(count).angleZ + 90;
          }
        }
      }
	}catch(Exception e){
      System.err.println("Error_: " + e.getMessage());
    }
  }
  boolean checkAvailablityTopLeft(int a, int b, int width, int height){
    int count = 0;
    try{
      if((b-height+1)>=0 && (a-width+1)>=0){
        for(int i=b;i>(b-height);i--){
          for(int j=a;j>(a-width);j--){
            if(grid[j][i]==0)
              count++;
          }
        }
      }
      if((width*height)==count){
        //make occupied
        for(int i=b;i>(b-height);i--)
          for(int j=a;j>(a-width);j--)
            grid[j][i]=1;
        return true;
      }
      else
        return false;
    }catch(Exception e){
      System.err.println("Error_: " + e.getMessage());
      return false;
    }
  }
  public void PlaceTopRightPartition(){
    int startX = 1+(int)centerOfPcbX, endX = (int)pcbBoardXmax, startY = (int)centerOfPcbY, endY = (int)pcbBoardYmin; 
    int count = 0, PLACED, ROTATE, a, b;
    boolean ret;
    try{
  	  while(!compSizeList.isEmpty()){
        PLACED=0;
        ROTATE=0;
        for(int y=startY; y>=endY; y--){
          for(int x=startX; x<=endX; x++){
            ret = false;
            a=x-(int)pcbBoardXmin;
            b=y-endY;
            if(grid[a][b]==0){
              ret = checkAvailablityTopRight(a,b,compSizeList.get(count).width,compSizeList.get(count).height);
            }
            if(ret == true){
              compSizeList.get(count).compCenterX = (x + compSizeList.get(count).width/2);
              compSizeList.get(count).compCenterY = (y - compSizeList.get(count).height/2);
              PLACED=1;
              break;
            }
          }
          if(PLACED==1)
            break;
        }
        if(PLACED==1 || ROTATE==1){
          if(PLACED==0 && ROTATE==1){
            ROTATE=0;
          }
          else{
            for(int m=0; m<moduleList.size(); m++)
            {
              if(compSizeList.get(count).compName.equals(moduleList.get(m).moduleName)){
                moduleList.get(m).positionX = compSizeList.get(count).compCenterX;
                moduleList.get(m).positionY = compSizeList.get(count).compCenterY;
                moduleList.get(m).angleZ = moduleList.get(m).angleZ+compSizeList.get(count).angleZ;
                break;
              }
            }
            compSizeList.remove(count);
            ROTATE=0;	
          }
        }
        else{
          if(ROTATE==0){
            ROTATE=1;
            compSizeList.get(count).height = compSizeList.get(count).height + compSizeList.get(count).width;
            compSizeList.get(count).width = compSizeList.get(count).height - compSizeList.get(count).width;
            compSizeList.get(count).height = compSizeList.get(count).height - compSizeList.get(count).width;
            compSizeList.get(count).angleZ = compSizeList.get(count).angleZ + 90;
          }
        }
      }	
    }catch(Exception e){
      System.err.println("Error_: " + e.getMessage());
    }
 }
  boolean checkAvailablityTopRight(int a, int b, int width, int height){
    int count = 0;
    try{
      if((b-height+1)>=0 && (a+width+1)<=(pcbBoardXmax-pcbBoardXmin)){
        for(int i=b;i>(b-height);i--){
          for(int j=a;j<(a+width);j++){
            if(grid[j][i]==0)
              count++;
          }
        }
      }
      if((width*height)==count){
        //make occupied
        for(int i=b;i>(b-height);i--)
          for(int j=a;j<(a+width);j++)
            grid[j][i]=1;
        return true;
      }
      else
        return false;
    }catch(Exception e){
      System.err.println("Error_: " + e.getMessage());
      return false;
    }
  }
  public void PlaceBottomLeftPartition(){
    int startX = (int)centerOfPcbX-1, endX = (int)pcbBoardXmin, startY = 1+(int)centerOfPcbY, endY = (int)pcbBoardYmax;
    int count = 0, PLACED, ROTATE, a, b;
    boolean ret;
    try{
      while(!compSizeList.isEmpty()){
        PLACED=0;
        ROTATE=0;
        for(int y=startY; y<=endY; y++){
          for(int x=startX; x>=endX; x--){
            ret = false;
            a=x-endX;
            //b=endY-y;
            b=y-(int)pcbBoardYmin;
            if(grid[a][b]==0){
              ret = checkAvailablityBottomLeft(a,b,compSizeList.get(count).width,compSizeList.get(count).height);
            }
            if(ret == true){
              compSizeList.get(count).compCenterX = (x - compSizeList.get(count).width/2);
              compSizeList.get(count).compCenterY = (y + compSizeList.get(count).height/2);
              PLACED=1;
              break;
            }
          }
          if(PLACED==1)
            break;
        }
        if(PLACED==1 || ROTATE==1){
          if(PLACED==0 && ROTATE==1){
            ROTATE=0;
          }
          else{
            for(int m=0; m<moduleList.size(); m++)
            {
              if(compSizeList.get(count).compName.equals(moduleList.get(m).moduleName)){
                moduleList.get(m).positionX = compSizeList.get(count).compCenterX;
                moduleList.get(m).positionY = compSizeList.get(count).compCenterY;
                moduleList.get(m).angleZ = moduleList.get(m).angleZ+compSizeList.get(count).angleZ;
                break;
              }
            }
            compSizeList.remove(count);
            ROTATE=0; 
          }
        }
        else{
          if(ROTATE==0){
            ROTATE=1;
            compSizeList.get(count).height = compSizeList.get(count).height + compSizeList.get(count).width;
            compSizeList.get(count).width = compSizeList.get(count).height - compSizeList.get(count).width;
            compSizeList.get(count).height = compSizeList.get(count).height - compSizeList.get(count).width;
            compSizeList.get(count).angleZ = compSizeList.get(count).angleZ + 90;
          }
        }
      }   
    }catch(Exception e){
      System.err.println("Error_: " + e.getMessage());
    }
  }
  boolean checkAvailablityBottomLeft(int a, int b, int width, int height){
    int count = 0;
    try{
      if((b+height+1)<=(pcbBoardYmax-pcbBoardYmin) && (a-width+1)>=0){
        for(int i=b;i<(b+height);i++){
          for(int j=a;j>(a-width);j--){
            if(grid[j][i]==0)
              count++;
          }
        }
      }
      if((width*height)==count){
        //make occupied
        for(int i=b;i<(b+height);i++)
          for(int j=a;j>(a-width);j--)
            grid[j][i]=1;
        return true;
      }
      else
        return false;
    }catch(Exception e){
      System.err.println("Error_: " + e.getMessage());
      return false;
    }
  }
  public void PlaceBottomRightPartition(){
    int startX = 1+(int)centerOfPcbX, endX = (int)pcbBoardXmax, startY = 1+(int)centerOfPcbY, endY = (int)pcbBoardYmax;
    int count = 0, PLACED, ROTATE, a, b;
    boolean ret;
    try{
      while(!compSizeList.isEmpty()){
        PLACED=0;
        ROTATE=0;
        for(int y=startY; y<=endY; y++){
          for(int x=startX; x<=endX; x++){
            ret = false;
            a=x-(int)pcbBoardXmin;
            b=y-(int)pcbBoardYmin;;
            if(grid[a][b]==0){
              ret = checkAvailablityBottomRight(a,b,compSizeList.get(count).width,compSizeList.get(count).height);
            }
            if(ret == true){
              compSizeList.get(count).compCenterX = (x + compSizeList.get(count).width/2);
              compSizeList.get(count).compCenterY = (y + compSizeList.get(count).height/2);
              PLACED=1;
              break;
            }
          }
          if(PLACED==1)
            break;
        }
        if(PLACED==1 || ROTATE==1){
          if(PLACED==0 && ROTATE==1){
            ROTATE=0;
          }
          else{
            for(int m=0; m<moduleList.size(); m++)
            {
              if(compSizeList.get(count).compName.equals(moduleList.get(m).moduleName)){
                moduleList.get(m).positionX = compSizeList.get(count).compCenterX;
                moduleList.get(m).positionY = compSizeList.get(count).compCenterY;
                moduleList.get(m).angleZ = moduleList.get(m).angleZ+compSizeList.get(count).angleZ;
                break;
              }
            }
            compSizeList.remove(count);
            ROTATE=0; 
          }
        }
        else{
          if(ROTATE==0){
            ROTATE=1;
            compSizeList.get(count).height = compSizeList.get(count).height + compSizeList.get(count).width;
            compSizeList.get(count).width = compSizeList.get(count).height - compSizeList.get(count).width;
            compSizeList.get(count).height = compSizeList.get(count).height - compSizeList.get(count).width;
            compSizeList.get(count).angleZ = compSizeList.get(count).angleZ + 90;
          }
        }
      }	
    }catch(Exception e){
      System.err.println("Error_: " + e.getMessage());
    }
  }
  boolean checkAvailablityBottomRight(int a, int b, int width, int height){
    int count = 0;
    try{
      if((b+height+1)<=(pcbBoardYmax-pcbBoardYmin) && (a+width+1)<=(pcbBoardXmax-pcbBoardXmin)){
        for(int i=b;i<(b+height);i++){
          for(int j=a;j<(a+width);j++){
            if(grid[j][i]==0)
              count++;
          }
        }
      }
      if((width*height)==count){
        //make occupied
        for(int i=b;i<(b+height);i++)
          for(int j=a;j<(a+width);j++)
            grid[j][i]=1;
        return true;
      }
      else
        return false;
    }catch(Exception e){
      System.err.println("Error_: " + e.getMessage());
      return false;
    }
  }
  void GenerateFinalPlacementFile(List<PcbModules>moduleList){
    try{
      PcbParser pp = new PcbParser();
      BufferedReader br;
      FileInputStream fstream;
      Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("placed_"+pcbFile),"utf-8"));
      float posX=0, posY=0;
      int angleZ=0;
      String strLine=null,moduleName=null;
      List<String> temp = new ArrayList<String>();
      fstream = new FileInputStream(pcbFile);
      br = new BufferedReader(new InputStreamReader(fstream));  
      while ((strLine = br.readLine()) != null) {
        if(strLine.contains("(module ")){ 
          writer.write(strLine+"\n");
          br.readLine();
          while(true){
            strLine = br.readLine();
            temp.add(strLine);
            if(strLine.contains("fp_text reference")){
              pp.strLine = strLine;
              moduleName = pp.getSubstring("reference ",' ',10);            
              for(int i=0;i<moduleList.size();i++){
                if(moduleList.get(i).moduleName.equals(moduleName)){
                  posX=moduleList.get(i).positionX;
                  posY=moduleList.get(i).positionY;
                  if(moduleList.get(i).angleZ > 0)
                    angleZ = moduleList.get(i).angleZ;
                  break;
                }
              }
              break;
            }
          }
          if(angleZ>0){
            writer.write("    (at "+posX+" "+posY+" "+angleZ+")"+"\n");
            angleZ=0;
          }
          else
            writer.write("    (at "+posX+" "+posY+")"+"\n");
          while(!temp.isEmpty()){
            writer.write(temp.get(0)+"\n");
            temp.remove(0);
          }
        }
        else
          writer.write(strLine+"\n");  
      }
      br.close();
      writer.close();
    }catch (Exception e){
       System.err.println("Error: " + e.getMessage());
    }
  }
}
