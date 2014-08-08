package edu.pdx.placer;

import java.util.*;
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
  public void Placer(){
    GetComponentSize(moduleList);
    ArrangeComponents();
    PcbParser pp = new PcbParser(pcbFile);
    pp.pcbParse();
    SetCenterOfPcb(pp);
    SetDimensionOfPcb(pp);
    grid = new int[(int)(pcbBoardXmax)+1][(int)(pcbBoardYmax)+1];
    switch(PLACEMENT){
      case 1:
        PlaceTopLeftPartition();
      case 2:
        PlaceTopRightPartition();
      case 3:
        PlaceBottomLeftPartition();
      case 4:
        PlaceBottomRightPartition();
    }
  }
  public void GetComponentSize(List<PcbModules>moduleList){
  /*This method will calculate component width, height from module list and will add that in component size list*/ 
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
          compSizeList.add(cs);
          break;
        }
      }
    }
  }
  public void ArrangeComponents(){ 
  /* This method will arrange components in ComponentSize list in decreasing order of their size*/
  /*sorting component gain to get maximum gain */
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
  }
  public void SetCenterOfPcb(PcbParser pp){
    centerOfPcbX = (float) Math.floor(((pp.pcbBoardXmax - pp.pcbBoardXmin)/2) + pp.pcbBoardXmin);
    centerOfPcbY = (float) Math.floor(((pp.pcbBoardYmax - pp.pcbBoardYmin)/2) + pp.pcbBoardYmin);
  }
  public void SetDimensionOfPcb(PcbParser pp){
    pcbBoardXmax = (float) Math.floor(pp.pcbBoardXmax);
    pcbBoardXmin = (float) Math.floor(pp.pcbBoardXmin);
    pcbBoardYmax = (float) Math.floor(pp.pcbBoardYmax);
    pcbBoardYmin = (float) Math.floor(pp.pcbBoardYmin);
  }
  public void PlaceTopLeftPartition(){
	int startX = (int)centerOfPcbX, endX = (int)pcbBoardXmin, startY = (int)centerOfPcbY, endY = (int)pcbBoardYmin; 
	int count = 0, PLACED, ROTATE=0;
	boolean ret;
    while(!compSizeList.isEmpty()){
      PLACED=0;
      for(int y=startY; y>=endY; y--){
        for(int x=startX; x>=endX; x--){
          ret = false;
          if(grid[y][x]==0){
            ret = checkAvailablityTopLeft(x,y,endX,endY,compSizeList.get(count).width,compSizeList.get(count).height);
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
      count++;
      if(PLACED==1 || ROTATE==1){
        compSizeList.remove(count);
        ROTATE=0;
      }
      else{
        if(ROTATE==0){
          ROTATE=1;
          count--;
          compSizeList.get(count).height = compSizeList.get(count).height + compSizeList.get(count).width;
          compSizeList.get(count).width = compSizeList.get(count).height - compSizeList.get(count).width;
          compSizeList.get(count).height = compSizeList.get(count).height - compSizeList.get(count).width;
        }
      }
    }
  }
  boolean checkAvailablityTopLeft(int x, int y, int endX, int endY, int width, int height){
    int count = 0;
    if((y-width+1)>=endY && (x-height+1)>=endX){
      for(int i=y;i>(y-width);i--){
        for(int j=x;j>(x-height);j--){
          if(grid[i][j]==0)
            count++;
        }
      }
    }
    if((width*height)==count){
      //make occupied
      for(int i=y;i>(y-width);i--)
        for(int j=x;j>(x-height);j--)
          grid[i][j]=1;
      return true;
    }
    else
      return false;
  }
  public void PlaceTopRightPartition(){
	  
  }
  public void PlaceBottomLeftPartition(){
	  
  }
  public void PlaceBottomRightPartition(){
	  
  }
}
