package sample;

import cern.extjfx.chart.NumericAxis;
import cern.extjfx.chart.XYChartPane;
import cern.extjfx.chart.data.DataReducingObservableList;
import cern.extjfx.chart.data.LinearDataReducer;
import cern.extjfx.chart.data.ListData;
import cern.extjfx.chart.plugins.CrosshairIndicator;
import cern.extjfx.chart.plugins.Panner;
import cern.extjfx.chart.plugins.Zoomer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    protected static final Logger LOGGER = LogManager.getLogger(Controller.class);
    private static final int ONE_MILLION = 1000*1000;
    private static final int N_SAMPLES = ONE_MILLION*10; // 8GB memory

    @FXML
    private Pane plotPane;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        plotPane.getChildren().add(new Label("help me today"));

        NumericAxis xAxis = new NumericAxis();
        xAxis.setAnimated(false);
        NumericAxis yAxis = new NumericAxis();
        yAxis.setAnimated(false);
        LineChart<Number, Number> lineChart = new LineChart<>(xAxis, yAxis);
        XYChartPane<Number, Number> chartPane = new XYChartPane<>(lineChart);
        chartPane.getPlugins().addAll(new Zoomer(), new Panner(), new CrosshairIndicator<>());
        lineChart.setTitle("Test data");
        lineChart.setStyle(".chart-series-line { -fx-stroke-width: 1px; }");

        final DataReducingObservableList<Number, Number> reducedData = new DataReducingObservableList<>(xAxis);
        reducedData.setDataReducer(new LinearDataReducer<>());
        reducedData.setMaxPointsCount(1000);
        lineChart.getData().add(new XYChart.Series<>("Random data", reducedData));

        final ObservableList<XYChart.Data<Number, Number>> sourceData = FXCollections.observableArrayList();

        Controller.generateData(100, .1, N_SAMPLES).forEach(item -> {
            sourceData.add(new XYChart.Data<Number, Number>(item.getXValue().longValue(), item.getYValue().doubleValue()));
        });
        LOGGER.debug("adding items to plot");
        reducedData.setData(new ListData<Number, Number>(sourceData));
        LOGGER.debug(String.format("finished adding %d items to plot",sourceData.size()));

        plotPane.getChildren().add(chartPane);
        lineChart.setPrefWidth(800.0);
        lineChart.setPrefHeight(800.0);

        VBox.setVgrow(lineChart, Priority.ALWAYS);
        AnchorPane.setTopAnchor(lineChart, 0d);
        AnchorPane.setBottomAnchor(lineChart, 0d);
        AnchorPane.setLeftAnchor(lineChart, 0d);
        AnchorPane.setRightAnchor(lineChart, 0d);
    }

    public static ObservableList<XYChart.Data<Number, Number>> generateData(double firstValue, double variance, int size) {
        List<XYChart.Data<Number, Number>> data = new ArrayList<>(size);
        Random rnd = new Random();
        data.add(new XYChart.Data<>(0, firstValue));

        for (int x = 1; x < size; x++) {
            int sign = rnd.nextBoolean() ? 1 : -1;
            double y = data.get(x - 1).getYValue().doubleValue() + variance * rnd.nextDouble() * sign;
            data.add(new XYChart.Data<>(x, y));
        }
        return FXCollections.observableArrayList(data);
    }
}
