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
import edu.pdx.partitioner.*;

public class Placer {

	/**
	 * @param args
	 */
  public static void main(String[] args) {
	  
    if (args.length != 2) {
	  System.err.println("Invalid command line, exactly two argument required to specify input file");
	  System.exit(1);
	}
    PcbParser p = new PcbParser(args[1]);
    p.pcbParse();
    FmHeuristic fmh = new FmHeuristic(args[0], args[1]);
    fmh.FmVerticalPartitioner();
    fmh.FmHorizontalPartitioner();
<<<<<<< HEAD
    float hpwl = fmh.CalculateHpwl();
    ComponentPlacer cp = new ComponentPlacer(args[1], fmh.topLeftBucket, p.moduleList, 1);
    cp.Placer();
=======
>>>>>>> 4015571de02be7d249807f5793ed93d3647e80dc
  }
}