package edu.pdx.placer;

import edu.pdx.parser.*;

public class Placer {

	/**
	 * @param args
	 */
  public static void main(String[] args) {
	  
    if (args.length != 1) {
	  System.err.println("Invalid command line, exactly one argument required to specify input file");
	  System.exit(1);
	}		
    NetlistParser parser = new NetlistParser(args[0]);
    parser.parse();
  }
}
