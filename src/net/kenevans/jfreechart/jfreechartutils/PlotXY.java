package net.kenevans.jfreechart.jfreechartutils;

import java.awt.Color;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JFrame;
import javax.swing.UIManager;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 * This class is a convenience wrapper that manages a JFreeChart XYLineChart
 * containing arrays of doubles.
 * 
 * @author Kenneth Evans, Jr.
 */
public class PlotXY
{
    private static final String DEFAULT_TITLE = "XY Plot";
    protected XYSeriesCollection dataset = null;
    protected JFreeChart chart = null;
    ChartPanel panel = null;
    ChartFrame frame = null;

    /**
     * Constructor that creates a chart from arrays of x values and y values. If
     * xVals is null, then it uses an index for x. If xVals has only 1 array,
     * then that array is used for all the y arrays. Otherwise, each y array has
     * its own x array, and the number of x and y arrays must be the same.
     * 
     * @param title The chart title. May be null.
     * @param xLabel The chart x label. May be null.
     * @param yLabel The chart y label. May be null.
     * @param xVals The arrays of x values. May be null, a single array, or have
     *            the same number of arrays as the y values.
     * @param yVals The arrays of y values.
     */
    public PlotXY(String title, String xLabel, String yLabel, double[][] xVals,
        double[][] yVals) {
        createChart(title, xLabel, yLabel, xVals, yVals);
    }

    /**
     * Constructor that creates a chart from single arrays of x and y values.
     * If xVals is null, then it uses an index for x.
     * 
     * @param title The chart title. May be null.
     * @param xLabel The chart x label. May be null.
     * @param yLabel The chart y label. May be null.
     * @param xVals The array of x values. May be null.
     * @param yVals The array of y values.
     */
    public PlotXY(String title, String xLabel, String yLabel, double[] xVals,
        double[] yVals) {
        int nPoints = yVals.length;
        double[][] yVals1 = new double[1][nPoints];
        double[][] xVals1 = new double[1][nPoints];
        for(int i = 0; i < nPoints; i++) {
            if(xVals == null) {
                xVals1[0][i] = i;
            } else {
                xVals1[0][i] = xVals[i];
            }
            yVals1[0][i] = yVals[i];
        }
        createChart(title, xLabel, yLabel, xVals1, yVals1);
    }

    /**
     * Creates the chart for the class.
     * 
     * @param title The chart title. May be null.
     * @param xLabel The chart x label. May be null.
     * @param yLabel The chart y label. May be null.
     * @param xVals The arrays of x values. May be null, a single array, or have
     *            the same number of arrays as the y values.
     * @param yVals The arrays of y values.
     */
    private void createChart(String title, String xLabel, String yLabel,
        double[][] xVals, double[][] yVals) {
        dataset = new XYSeriesCollection();
        // Create the series
        int nSeries = yVals.length;
        int nXArrays = 0;
        if(xVals != null) {
            nXArrays = xVals.length;
        }
        int nPoints;
        double x;
        XYSeries[] seriesArray = new XYSeries[nSeries];
        for(int i = 0; i < nSeries; i++) {
            XYSeries series = seriesArray[i];
            series = new XYSeries("Series " + (i + 1));
            nPoints = yVals[i].length;
            for(int j = 0; j < nPoints; j++) {
                if(xVals == null)
                    x = j;
                else if(nXArrays == 1)
                    x = xVals[0][j];
                else
                    x = xVals[i][j];
                series.add(x, yVals[i][j]);
            }
            dataset.addSeries(series);
        }

        // Generate the graph
        chart = ChartFactory.createXYLineChart(title, xLabel, yLabel, dataset,
            PlotOrientation.VERTICAL, // BasicImagePlot Orientation
            false, // Show Legend
            false, // Use tooltips
            false // Configure chart to generate URLs?
        );
        XYPlot plot = (XYPlot)chart.getPlot();
        plot.setBackgroundPaint(Color.white);
        plot.setDomainGridlinePaint(Color.GRAY);
        plot.setRangeGridlinePaint(Color.GRAY);
    }

    /**
     * Creates a ChartPanel from the chart.
     * 
     * @return
     */
    public ChartPanel createPanel() {
        panel = new ChartPanel(chart);
        return panel;
    }

    /**
     * Adds a legend to the chart.
     */
    public void createLegend() {
        JFreeChartUtils.createLegend(chart);
    }

    /**
     * Removes the legend from the chart.
     */
    public void removeLegend() {
        if(chart != null) chart.removeLegend();
    }

    /**
     * Changes the key for a series. The key is the name that typically shows in
     * the legend.
     * 
     * @param n Index of the series. Nothing will be done if it is invalid.
     * @param name Name of the key.
     */
    public void setSeriesKey(int n, String name) {
        if(dataset == null) return;
        int nSeries = dataset.getSeriesCount();
        if(n < 0 || n >= nSeries) return;
        XYSeries series = dataset.getSeries(n);
        series.setKey(name);
    }

    /**
     * Changes the color for a series.
     * 
     * @param n Index of the series. Nothing will be done if it is invalid.
     * @param name Name of the key.
     */
    public void setSeriesColor(int n, Color color) {
        if(dataset == null) return;
        int nSeries = dataset.getSeriesCount();
        if(n < 0 || n >= nSeries) return;
        XYItemRenderer renderer = chart.getXYPlot().getRenderer();
        renderer.setSeriesPaint(n, color);
        // renderer.setSeriesShapesVisible(0, false);
        // renderer.setSeriesShapesVisible(1, false);
    }

    /**
     * Runs this chart as a new ChartFrame. The frame title is the same as the
     * chart title or is a default title if there is no chart title.
     * 
     */
    public void run() {
        if(chart == null) {
            Utils.errMsg("Chart is invalid");
            return;
        }
        String title = DEFAULT_TITLE;
        TextTitle textTitle = chart.getTitle();
        if(textTitle != null && textTitle.getText() != null) {
            title = textTitle.getText();
        }
        run(title);
    }

    /**
     * Runs this chart as a new ChartFrame.
     * 
     * @param frameTitle The frame title, not the chart title.
     */
    public void run(String frameTitle) {
        try {
            if(chart == null) {
                Utils.errMsg("Chart is invalid");
                return;
            }
            frame = new ChartFrame(frameTitle, chart);
            frame.setLocationRelativeTo(null);
            frame.pack();
            frame.setVisible(true);
        } catch(Throwable t) {
            t.printStackTrace();
        }
    }

    /**
     * Adds a new series to the chart.
     * 
     * @param name The series name.
     * @param xVals Array of x values. May be null, in which case the index is
     *            used.
     * @param yVals Array of y values.
     * @return The series created.
     */
    public XYSeries addSeries(String name, double[] xVals, double[] yVals) {
        int nPoints = yVals.length;
        double x;
        if(name == null) {
            int nSeries = dataset.getSeriesCount();
            name = "Series " + (nSeries + 1);
        }
        XYSeries series = new XYSeries(name);
        for(int j = 0; j < nPoints; j++) {
            if(xVals == null) x = j;
            x = xVals[j];
            series.add(x, yVals[j]);
        }
        dataset.addSeries(series);
        return series;
    }

    // Getters and setters

    /**
     * @return The value of chart.
     */
    public JFreeChart getChart() {
        return chart;
    }

    /**
     * @param chart The new value for chart.
     */
    public void setChart(JFreeChart chart) {
        this.chart = chart;
    }

    /**
     * @return The value of dataset.
     */
    public XYSeriesCollection getDataset() {
        return dataset;
    }

    /**
     * @return The value of panel.
     */
    public ChartPanel getPanel() {
        return panel;
    }

    /**
     * @return The value of frame.
     */
    public ChartFrame getFrame() {
        return frame;
    }

    /**
     * A test function.
     * 
     * @param args
     */
    public static void main(String[] args) {
        // This has to be done here or plot is not native colors.
        try {
            JFrame.setDefaultLookAndFeelDecorated(true);
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch(Throwable t) {
            t.printStackTrace();
        }

        // Generate some data
        int nPoints = 100;
        int nPoints2 = 75;
        double[][] xVals = new double[1][nPoints];
        double[][] yVals = new double[2][nPoints];
        double[] xValsExtra = new double[nPoints2];
        double[] yValsExtra = new double[nPoints2];
        double del = 2. * Math.PI / (nPoints - 1);
        double x, y;
        for(int i = 0; i < nPoints; i++) {
            x = i * del;
            y = Math.sin(x);
            xVals[0][i] = x;
            yVals[0][i] = y;
            y = Math.cos(x);
            yVals[1][i] = y;
            if(i < nPoints2) {
                x = x + +Math.PI / 4;
                y = -.5 * Math.cos(x);
                xValsExtra[i] = x;
                yValsExtra[i] = y;
            }
        }

        // Run the plot
        final PlotXY app = new PlotXY("Trignometric Functions", "x", null,
            xVals, yVals);
        app.run("XY Plot Test");

        // Add a legend after the chart is running
        final Timer timer1 = new Timer();
        timer1.schedule(new TimerTask() {
            public void run() {
                app.createLegend();
                timer1.cancel();
            }
        }, 3000);

        // Add a new array after the chart is running
        final Timer timer2 = new Timer();
        timer2.schedule(new TimerTask() {
            public void run() {
                int nPoints = 2;
                double[] xVals = new double[nPoints];
                double[] yVals = new double[nPoints];
                xVals[0] = Math.PI / 2.;
                xVals[1] = 3. * Math.PI / 2.;
                yVals[0] = yVals[1] = 0;
                XYSeries series = app.addSeries(null, xVals, yVals);

                // Rename two of the series
                series.setKey("Third Series");
                app.setSeriesKey(1, "Second Series");
                timer2.cancel();
            }
        }, 6000);

        // Delete the new array and remove the legend
        final Timer timer3 = new Timer();
        timer3.schedule(new TimerTask() {
            public void run() {
                app.removeLegend();
                app.getDataset().removeSeries(2);
                timer3.cancel();
            }
        }, 9000);
    }

}
