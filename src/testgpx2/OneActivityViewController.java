/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testgpx2;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicBoolean;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import jgpx.model.analysis.Chunk;
import jgpx.model.analysis.TrackData;
import jgpx.model.gpx.Track;
import jgpx.model.jaxb.GpxType;
import jgpx.model.jaxb.TrackPointExtensionT;

/**
 * FXML Controller class
 *
 * @author JoseManuel
 */
public class OneActivityViewController implements Initializable, ActivityView {

    /**
     * Initializes the controller class.
     */
    private Presenter presenter;
    @FXML
    private Button loadButton;
    @FXML
    private Label dateLabel;
    @FXML
    private AreaChart<Number, Number> profileChart;

    private XYChart.Series profileSerie;
    private XYChart.Series velocitySerie;
    @FXML
    private LineChart<?, ?> velocityChart;
    @FXML
    private CheckBox timeAsBaseInVelocityCheckBox;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        presenter = DependencyInjector.getPresenter(this);
    }

    @FXML
    private void handleOnActionLoadButton(ActionEvent event) {

        presenter.load();

        Task<Void> taskProfile = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        profileChart.getScene().setCursor(Cursor.WAIT);

                    }
                });
                profileSerie = presenter.getProfileSerie(true);
                return null;
            }
        };
        taskProfile.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                profileChart.getData().add(profileSerie);
                profileChart.getScene().setCursor(Cursor.DEFAULT);

            }
        });

        Task<Void> taskVelocity = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        profileChart.getScene().setCursor(Cursor.WAIT);

                    }
                });
                velocitySerie = presenter.getVelocitySerie(timeAsBaseInVelocityCheckBox.isSelected());
                return null;
            }
        };
        taskVelocity.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                velocityChart.getData().add(velocitySerie);
                profileChart.getScene().setCursor(Cursor.DEFAULT);

            }
        });

        Thread profileThread = new Thread(taskProfile);
        profileThread.setDaemon(true);
        profileThread.start();
        
        Thread velocityThread = new Thread(taskVelocity);
        velocityThread.setDaemon(true);
        velocityThread.start();

    }

}
