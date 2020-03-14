import java.awt.Dimension;

import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.io.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.swing.*;

import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.jfree.chart.*;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.time.*;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.data.xy.XYDataset;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

public class Demo extends JFrame
{
	private Action exportFigure;
    private JFreeChart jfreechart;
	
    public Demo()
    {
        super("JFreeChart Batik demo");

		jfreechart = createChart();
        ChartPanel chartpanel = new ChartPanel(jfreechart, true, true, true, false, true);
        chartpanel.setPreferredSize(new Dimension(500, 270));
        setContentPane(chartpanel);
        
        exportFigure = new AbstractAction("Export figure") {
			public void actionPerformed(ActionEvent arg0) {
				exportFigure();
			}
        };
        
        this.setJMenuBar(createMenuBar());
    }
    
    private void exportFigure() {
    	// TODO: use a dialog to ask for the filename
    	File svgFile = new File("example_file.svg");
    	
    	// write it to file
    	try {
			exportChartAsSVG(jfreechart, 
				getContentPane().getBounds(), svgFile);

	    	// TODO: notify the user the file has been saved (e.g. status bar)
	    	System.out.println("Figured saved as " + svgFile.getAbsolutePath());
    	
    	} catch (IOException e) {
			System.err.println("Error saving file:\n" + e.getMessage());
		}
    }

    private JMenuBar createMenuBar() {
		JMenuBar result = new JMenuBar();
		JMenu fileMenu = new JMenu("File");
		fileMenu.add(exportFigure);
		
		result.add(fileMenu);
		
		return result;
	}

	private static JFreeChart createChart()
    {
        XYDataset xydataset = createPriceDataset();
        String s = "Example chart";
        JFreeChart jfreechart = ChartFactory.createTimeSeriesChart(s, "Date", "Price", xydataset, true, true, false);
        XYPlot xyplot = jfreechart.getXYPlot();
        NumberAxis numberaxis = (NumberAxis)xyplot.getRangeAxis();
        numberaxis.setLowerMargin(0.40000000000000002D);
        DecimalFormat decimalformat = new DecimalFormat("00.00");
        numberaxis.setNumberFormatOverride(decimalformat);
        XYItemRenderer xyitemrenderer = xyplot.getRenderer();
        xyitemrenderer.setToolTipGenerator(new StandardXYToolTipGenerator("{0}: ({1}, {2})", new SimpleDateFormat("d-MMM-yyyy"), new DecimalFormat("0.00")));
        NumberAxis numberaxis1 = new NumberAxis("Volume");
        numberaxis1.setUpperMargin(1.0D);
        xyplot.setRangeAxis(1, numberaxis1);
        xyplot.setDataset(1, createVolumeDataset());
        xyplot.setRangeAxis(1, numberaxis1);
        xyplot.mapDatasetToRangeAxis(1, 1);
        XYBarRenderer xybarrenderer = new XYBarRenderer(0.20000000000000001D);
        xybarrenderer.setToolTipGenerator(new StandardXYToolTipGenerator("{0}: ({1}, {2})", new SimpleDateFormat("d-MMM-yyyy"), new DecimalFormat("0,000.00")));
        xyplot.setRenderer(1, xybarrenderer);
        return jfreechart;
    }

    private static XYDataset createPriceDataset()
    {
        TimeSeries timeseries = new TimeSeries("Price", org.jfree.data.time.Day.class);
        Calendar today = Calendar.getInstance();
        // go back numdays in time
        today.add(Calendar.DAY_OF_MONTH, -numdays);
        
        for (int i = 0; i < numdays; i++) {
            timeseries.add(new Day(
            		today.get(Calendar.DAY_OF_MONTH),
            		today.get(Calendar.MONTH) + 1,
            		today.get(Calendar.YEAR))
            	, randomPrice());
        	// increase one day
        	today.add(Calendar.DAY_OF_MONTH, 1);
        }
        
        return new TimeSeriesCollection(timeseries);
    }
    
    private static double previousVolume = 41000D;
    private static final double volumeRange = 50000D;
    
    private static double randomVolume() {
    	previousVolume = (Math.random() - 0.5d) * volumeRange + previousVolume;
    	if (previousVolume < 0) previousVolume = 0;
    	return previousVolume;
    }
    
    private static final int numdays = 100;
    private static double previousPrice = 98D;
    private static final double priceRange = 1D;

    private static double randomPrice() {
    	previousPrice = (Math.random() - 0.5d) * priceRange + previousPrice;
    	if (previousPrice < 0) previousPrice = 0;
    	return previousPrice;
    }

    private static IntervalXYDataset createVolumeDataset()
    {
        TimeSeries timeseries = new TimeSeries("Volume", org.jfree.data.time.Day.class);
        
        Calendar today = Calendar.getInstance();
        // go back numdays in time
        today.add(Calendar.DAY_OF_MONTH, -numdays);
        
        for (int i = 0; i < numdays; i++) {
            timeseries.add(new Day(
            		today.get(Calendar.DAY_OF_MONTH),
            		today.get(Calendar.MONTH) + 1,
            		today.get(Calendar.YEAR))
            	, randomVolume());
        	// increase one day
        	today.add(Calendar.DAY_OF_MONTH, 1);
        }
        
        return new TimeSeriesCollection(timeseries);
    }

    public static JPanel createDemoPanel()
    {
        JFreeChart jfreechart = createChart();
        return new ChartPanel(jfreechart);
    }

    public static void main(String args[])
    {
        Demo pricevolumedemo1 = new Demo();
        pricevolumedemo1.pack();
        pricevolumedemo1.setVisible(true);
    }
    
	/**
	 * Exports a JFreeChart to a SVG file.
	 * 
	 * @param chart JFreeChart to export
	 * @param bounds the dimensions of the viewport
	 * @param svgFile the output file.
	 * @throws IOException if writing the svgFile fails.
	 */
	void exportChartAsSVG(JFreeChart chart, Rectangle bounds, File svgFile) throws IOException {
        // Get a DOMImplementation and create an XML document
        DOMImplementation domImpl =
            GenericDOMImplementation.getDOMImplementation();
        Document document = domImpl.createDocument(null, "svg", null);

        // Create an instance of the SVG Generator
        SVGGraphics2D svgGenerator = new SVGGraphics2D(document);

        // draw the chart in the SVG generator
        chart.draw(svgGenerator, bounds);

        // Write svg file
        OutputStream outputStream = new FileOutputStream(svgFile);
        Writer out = new OutputStreamWriter(outputStream, "UTF-8");
        svgGenerator.stream(out, true /* use css */);						
        outputStream.flush();
        outputStream.close();
	}    
}
