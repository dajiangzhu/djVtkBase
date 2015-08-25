/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uga.liulab.djVtkBase;

import com.xinapse.loadableimage.InvalidImageException;
import com.xinapse.loadableimage.ParameterNotSetException;
import com.xinapse.multisliceimage.MultiSliceImageException;
import java.io.IOException;
import java.util.List;
import org.apache.commons.cli.*;

/**
 * 
 * @author dj
 */
public class Main {
	private Options options;
	private CommandLine cmdLine;
	private HelpFormatter formatter;
	private CommandLineParser cmdParser;

	private void createOptions() {
		options = new Options();
		Option inputSur = OptionBuilder.withArgName("File").hasArg().isRequired(true).withDescription("input surface file name(*.vtk)").create("s");
		Option inputFiber = OptionBuilder.withArgName("File").hasArg().isRequired(true).withDescription("input fiber file name(*.vtk)").create("f");
		Option outputFiber = OptionBuilder.withArgName("File").hasArg().isRequired(true).withDescription("input output fiber file name(*.vtk)")
				.create("o");
		Option help = new Option("Help", "print this message");
		options.addOption(inputSur);
		options.addOption(inputFiber);
		options.addOption(outputFiber);
		options.addOption(help);
	}

	private void parseArgs(String[] strInputs) {
		try {
			cmdLine = this.cmdParser.parse(this.options, strInputs);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			formatter.printHelp("Fiber Extrator input error!", this.options);
			System.exit(0);
			// e.printStackTrace();
		}
	}

	public Main() {
		cmdParser = new GnuParser();
		formatter = new HelpFormatter();
	}

	/**
	 * 
	 * @param args
	 * @throws MultiSliceImageException
	 * @throws IOException
	 * @throws ParameterNotSetException
	 * @throws InvalidImageException
	 */
	public static void main(String[] args) throws MultiSliceImageException, IOException, ParameterNotSetException, InvalidImageException {
		// Construct options
		Main mianHandler = new Main();
		mianHandler.createOptions();
		mianHandler.parseArgs(args);
		if (mianHandler.cmdLine == null || mianHandler.cmdLine.hasOption("help")) {
			mianHandler.formatter.printHelp("Fiber Extrator From DJ", mianHandler.options);
			return;
		}
		String surName = mianHandler.cmdLine.getOptionValue("s");
		String fiberName = mianHandler.cmdLine.getOptionValue("f");
		String outPutName = mianHandler.cmdLine.getOptionValue("o");
		System.out.println("You input surface is :"+surName);
		System.out.println("You input fiber is :"+fiberName);
		System.out.println("You expect the output fiber is :"+outPutName);

		djVtkSurData surData = new djVtkSurData(surName);
		djVtkFiberData fiberData = new djVtkFiberData(fiberName);
		djVtkHybridData hybridData = new djVtkHybridData(surData, fiberData);
		hybridData.getFibersConnectToSurface().writeToVtkFileCompact(outPutName);
	}
}
