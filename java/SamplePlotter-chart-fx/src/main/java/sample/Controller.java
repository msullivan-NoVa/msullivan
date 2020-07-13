package sample;

import de.gsi.chart.axes.spi.DefaultNumericAxis;
import de.gsi.chart.XYChart;
import de.gsi.chart.plugins.Zoomer;
import de.gsi.chart.renderer.datareduction.RamanDouglasPeukerDataReducer;
import de.gsi.chart.renderer.spi.ErrorDataSetRenderer;
import de.gsi.chart.renderer.spi.ReducingLineRenderer;
import de.gsi.dataset.DataSet2D;
import de.gsi.dataset.spi.DoubleDataSet;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.util.Random;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    protected static final Logger LOGGER = LogManager.getLogger(Controller.class);
    private static final int ONE_MILLION = 1000*1000;
    private static final int N_SAMPLES = ONE_MILLION*10; // 8GB memory <1 GB for chart-fx

    @FXML
    private Pane plotPane;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        plotPane.getChildren().add(new Label("help me today"));

        final XYChart lineChart = new XYChart(new DefaultNumericAxis(), new DefaultNumericAxis());
        lineChart.getPlugins().add(new Zoomer());
        final DoubleDataSet dataSet1 = new DoubleDataSet("data set #1");
        final DoubleDataSet dataSet2 = new DoubleDataSet("data set #2");
        final DoubleDataSet dataSet3 = new DoubleDataSet("data set #3");
        lineChart.getDatasets().addAll(dataSet1, dataSet2, dataSet3); // two data sets

        final ReducingLineRenderer dataRenderer = new ReducingLineRenderer();
        lineChart.getRenderers().set(0, dataRenderer);

        final double[] xValues = new double[N_SAMPLES];
        final double[] yValues1 = new double[N_SAMPLES];
        final double[] yValues2 = new double[N_SAMPLES];
        final double[] yValues3 = new double[N_SAMPLES];
        for (int n = 0; n < N_SAMPLES; n++) {
            xValues[n] = n;
        }
        LOGGER.debug("adding items to plot");
        dataSet1.set(xValues, Controller.generateData(1.0,0.1,N_SAMPLES));
        dataSet2.set(xValues, Controller.generateData(1.0,0.1,N_SAMPLES));
        dataSet3.set(xValues, Controller.generateData(1.0,0.1,N_SAMPLES));
        LOGGER.debug(String.format("finished adding %d items to plot",N_SAMPLES));

        plotPane.getChildren().add(lineChart);
        lineChart.setPrefWidth(1000.0);
        lineChart.setPrefHeight(1000.0);

        VBox.setVgrow(lineChart, Priority.ALWAYS);
        AnchorPane.setTopAnchor(lineChart, 0d);
        AnchorPane.setBottomAnchor(lineChart, 0d);
        AnchorPane.setLeftAnchor(lineChart, 0d);
        AnchorPane.setRightAnchor(lineChart, 0d);
    }

    public static double[] generateData(double firstValue, double variance, int size) {
        double []data = new double[size];
        Random rnd = new Random();
        data[0] = firstValue;

        for (int x = 1; x < size; x++) {
            int sign = rnd.nextBoolean() ? 1 : -1;
            double y = data[x - 1] + variance * rnd.nextDouble() * sign;
            data[x] = y;
        }
        return data;
    }
}
