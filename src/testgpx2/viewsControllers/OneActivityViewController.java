/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testgpx2.viewsControllers;

import java.io.File;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicBoolean;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.TextArea;
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
import testgpx2.ActivityView;
import testgpx2.DependencyInjector;
import testgpx2.Presenter;

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
    private XYChart.Series fCSeries;
    private XYChart.Series cadenceSeries;

    private XYChart.Series combinedVelocitySerie = null;
    private XYChart.Series combinedFCSeries = null;
    private XYChart.Series combinedCadenceSeries = null;

    private ObservableList<PieChart.Data> pieChartZonesData;
    @FXML
    private LineChart<Number, Number> velocityChart;
    @FXML
    private CheckBox timeAsBaseInVelocityCheckBox;
    @FXML
    private Label durationLabel;
    @FXML
    private Label timeInMovementLabel;
    @FXML
    private Label totalDistanceLabel;
    @FXML
    private Label cumulativeAltitudeLabel;
    @FXML
    private Label velocityLabel;
    @FXML
    private Label cardiacFrecuenceLabel;
    @FXML
    private Label cadenceLabel;

    private DecimalFormat decimalFormatter;
    @FXML
    private LineChart<Number, Number> FCLineChart;
    @FXML
    private CheckBox FCTimeAsBaseCheckBox;
    @FXML
    private LineChart<Number, Number> cadenceLineChart;
    @FXML
    private CheckBox CadenceBaseAsTimeCheckBox;
    @FXML
    private PieChart cardiacZonesPieChart;
    @FXML
    private TextArea cardiacZonesTextArea;
    @FXML
    private Label titlename;
    @FXML
    private CheckBox timeAsBaseInConbinedChartCheckBox;

    @FXML
    private MenuButton chartsToShowMenuButton;
    @FXML
    private CheckMenuItem hearthRateCheckMenuItem;
    @FXML
    private CheckMenuItem cadenceCheckMenuItem;
    @FXML
    private CheckMenuItem speedCheckMenuItem;
    @FXML
    private LineChart<Number, Number> combinedChart;
    @FXML
    private CheckBox timeAsBaseInProfileChart;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        presenter = DependencyInjector.getPresenter(this);
        decimalFormatter = new DecimalFormat(".##");
    }

    @FXML
    private void handleOnActionLoadButton(ActionEvent event) {

        //new loading stage (so it doesn't froze).
        presenter.load();
        //Close loading stage 

        titlename.setText(presenter.getFileName());

        //CREATING TAKS FOR CHARTS AND RESUME.
        Task<Void> taskProfile = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                profileSerie = presenter.getProfileSerie(timeAsBaseInProfileChart.isSelected());
                return null;
            }
        };
        taskProfile.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                profileChart.getData().add(profileSerie);

            }
        });

        Task<Void> taskVelocity = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                velocitySerie = presenter.getVelocitySerie(timeAsBaseInVelocityCheckBox.isSelected());

                return null;
            }
        };
        taskVelocity.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                velocityChart.getData().add(velocitySerie);

            }
        });

        Task<Void> resumeTask = new Task<Void>() {

            @Override
            protected Void call() throws Exception {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        dateLabel.setText(dateLabel.getText() + " " + presenter.getDate());
                        durationLabel.setText(durationLabel.getText() + " " + presenter.getDuration());
                        timeInMovementLabel.setText(timeInMovementLabel.getText() + " " + presenter.getTimeInMovement());
                        totalDistanceLabel.setText(totalDistanceLabel.getText() + " " + presenter.getTotalDistance());
                        cumulativeAltitudeLabel.setText(cumulativeAltitudeLabel.getText() + " " + presenter.getCumulativeAltitude());
                        velocityLabel.setText(velocityLabel.getText() + " " + presenter.getMaxVelocity() + " y " + presenter.getMedVelocity());
                        cardiacFrecuenceLabel.setText(cardiacFrecuenceLabel.getText() + " " + presenter.getMaxFC() + "/" + presenter.getMedFC() + "/" + presenter.getMinFC());
                        cadenceLabel.setText(cadenceLabel.getText() + " " + presenter.getMaxCadence() + "/" + presenter.getMedCandence());

                    }
                });

                return null;

            }
        };

        Task<Void> FCTask = new Task<Void>() {

            @Override
            protected Void call() throws Exception {
                fCSeries = presenter.getFCSeries(FCTimeAsBaseCheckBox.isSelected());
                return null;
            }

        };

        FCTask.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                FCLineChart.getData().add(fCSeries);

            }
        });

        Task<Void> CadenceTask = new Task<Void>() {

            @Override
            protected Void call() throws Exception {
                cadenceSeries = presenter.getCadenceSeries(CadenceBaseAsTimeCheckBox.isSelected());
                return null;
            }

        };

        CadenceTask.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                cadenceLineChart.getData().add(cadenceSeries);

            }
        });

        Task<Void> ZonesTask = new Task<Void>() {

            @Override
            protected Void call() throws Exception {
                pieChartZonesData = presenter.getZonesData();
                return null;
            }

        };

        ZonesTask.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                cardiacZonesPieChart.setData(pieChartZonesData);
                cardiacZonesTextArea.appendText("\nZona Recuperación: " + decimalFormatter.format(pieChartZonesData.get(0).getPieValue() * 100) + "%\n\n");
                cardiacZonesTextArea.appendText("Zona Fondo: " + decimalFormatter.format(pieChartZonesData.get(1).getPieValue() * 100) + "%\n\n");
                cardiacZonesTextArea.appendText("Zona Tempo: " + decimalFormatter.format(pieChartZonesData.get(2).getPieValue() * 100) + "%\n\n");
                cardiacZonesTextArea.appendText("Zona Umbral: " + decimalFormatter.format(pieChartZonesData.get(3).getPieValue() * 100) + "%\n\n");
                cardiacZonesTextArea.appendText("Zona Anaeróbico: " + decimalFormatter.format(pieChartZonesData.get(4).getPieValue() * 100) + "%\n\n");

            }
        });

        //RUNING THOSE TAKS.
        Thread profileThread = new Thread(taskProfile);
        profileThread.setDaemon(true);
        profileThread.start();

        Thread velocityThread = new Thread(taskVelocity);
        velocityThread.setDaemon(true);
        velocityThread.start();

        Thread resumeThread = new Thread(resumeTask);
        resumeThread.setDaemon(true);
        resumeThread.start();

        Thread hearthRateThread = new Thread(FCTask);
        hearthRateThread.setDaemon(true);
        hearthRateThread.start();

        Thread cadenceThread = new Thread(CadenceTask);
        cadenceThread.setDaemon(true);
        cadenceThread.start();

        Thread zonesPieChartThread = new Thread(ZonesTask);
        zonesPieChartThread.setDaemon(true);
        zonesPieChartThread.start();

    }

    @FXML
    private void HandleFCTimeAsBaseCheckBoxAction(ActionEvent event) {

        Task<Void> fCTask = new Task<Void>() {

            @Override
            protected Void call() throws Exception {
                fCSeries = presenter.getFCSeries(FCTimeAsBaseCheckBox.isSelected());
                return null;
            }

        };

        fCTask.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                FCLineChart.getData().add(fCSeries);
            }
        });
        FCLineChart.getData().remove(fCSeries);
        Thread fCThread = new Thread(fCTask);
        fCThread.setDaemon(true);
        fCThread.start();

    }

    @FXML
    private void HandleCadenceBaseAsTimeCheckBoxAction(ActionEvent event) {
        Task<Void> cadenceTask = new Task<Void>() {

            @Override
            protected Void call() throws Exception {
                cadenceSeries = presenter.getCadenceSeries(CadenceBaseAsTimeCheckBox.isSelected());
                return null;
            }

        };

        cadenceTask.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {

                cadenceLineChart.getData().add(cadenceSeries);

            }
        });

        cadenceLineChart.getData().remove(0);
        Thread cadenceThread = new Thread(cadenceTask);
        cadenceThread.setDaemon(true);
        cadenceThread.start();
    }

    @FXML
    private void handleTimeAsBaseInProfileChartAction(ActionEvent event) {
        Task<Void> profileTask = new Task<Void>() {

            @Override
            protected Void call() throws Exception {
                profileSerie = presenter.getProfileSerie(timeAsBaseInProfileChart.isSelected());
                return null;
            }

        };

        profileTask.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {

                profileChart.getData().add(profileSerie);

            }
        });

        profileChart.getData().remove(0);
        Thread profileThread = new Thread(profileTask);
        profileThread.setDaemon(true);
        profileThread.start();
    }

    @FXML
    private void handleTimeAsBaseInVelocityCheckBoxAction(ActionEvent event) {
        Task<Void> velocityTask = new Task<Void>() {

            @Override
            protected Void call() throws Exception {
                velocitySerie = presenter.getVelocitySerie(timeAsBaseInVelocityCheckBox.isSelected());
                return null;
            }

        };

        velocityTask.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {

                velocityChart.getData().add(velocitySerie);

            }
        });

        velocityChart.getData().remove(0);
        Thread velocityThread = new Thread(velocityTask);
        velocityThread.setDaemon(true);
        velocityThread.start();
    }

    @FXML
    private void handleHearthRateCheckMenuItemAction(ActionEvent event) {

        if (hearthRateCheckMenuItem.isSelected()) {


                //Task
                Task<Void> fcTask = new Task<Void>() {

                    @Override
                    protected Void call() throws Exception {
                        combinedFCSeries = presenter.getFCSeries(timeAsBaseInConbinedChartCheckBox.isSelected());
                        return null;
                    }

                };
                fcTask.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
                    @Override
                    public void handle(WorkerStateEvent event) {

                        combinedChart.getData().add(combinedFCSeries);

                    }
                });
                Thread fcThread = new Thread(fcTask);
                fcThread.setDaemon(true);
                fcThread.start();


        } else {

            combinedChart.getData().remove(combinedFCSeries);

        }
    }

    @FXML
    private void handleCadenceCheckMenuItemAction(ActionEvent event) {

        if (cadenceCheckMenuItem.isSelected()) {
                Task<Void> cadenceTask = new Task<Void>() {

                    @Override
                    protected Void call() throws Exception {
                        combinedCadenceSeries = presenter.getCadenceSeries(timeAsBaseInConbinedChartCheckBox.isSelected());
                        return null;
                    }

                };
                cadenceTask.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
                    @Override
                    public void handle(WorkerStateEvent event) {

                        combinedChart.getData().add(combinedCadenceSeries);

                    }
                });
                Thread cadenceThread = new Thread(cadenceTask);
                cadenceThread.setDaemon(true);
                cadenceThread.start();


        } else {

            combinedChart.getData().remove(combinedCadenceSeries);

        }

    }

    @FXML
    private void handleSpeedCheckMenuItemAction(ActionEvent event) {

        if (speedCheckMenuItem.isSelected()) {
                Task<Void> speedTask = new Task<Void>() {

                    @Override
                    protected Void call() throws Exception {
                        combinedVelocitySerie = presenter.getVelocitySerie(timeAsBaseInConbinedChartCheckBox.isSelected());
                        return null;
                    }

                };
                speedTask.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
                    @Override
                    public void handle(WorkerStateEvent event) {

                        combinedChart.getData().add(combinedVelocitySerie);

                    }
                });
                Thread speedThread = new Thread(speedTask);
                speedThread.setDaemon(true);
                speedThread.start();

        } else {

            combinedChart.getData().remove(combinedVelocitySerie);

        }

    }

    @FXML
    private void handleTimeAsBaseInConbinedChartCheckBoxAction(ActionEvent event) {
        combinedChart.getData().removeAll(combinedFCSeries, combinedCadenceSeries, combinedVelocitySerie);

        if (hearthRateCheckMenuItem.isSelected()) {

            Task<Void> fCTask = new Task<Void>() {

                @Override
                protected Void call() throws Exception {
                    combinedFCSeries = presenter.getFCSeries(timeAsBaseInConbinedChartCheckBox.isSelected());
                    return null;
                }

            };

            fCTask.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
                @Override
                public void handle(WorkerStateEvent event) {
                    combinedChart.getData().add(combinedFCSeries);

                }
            });

            Thread fCThread = new Thread(fCTask);
            fCThread.setDaemon(true);
            fCThread.start();

        }

        if (cadenceCheckMenuItem.isSelected()) {
            Task<Void> cadenceTask = new Task<Void>() {

                @Override
                protected Void call() throws Exception {
                    combinedCadenceSeries = presenter.getCadenceSeries(timeAsBaseInConbinedChartCheckBox.isSelected());
                    return null;
                }

            };

            cadenceTask.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
                @Override
                public void handle(WorkerStateEvent event) {

                    combinedChart.getData().add(combinedCadenceSeries);

                }
            });

            Thread cadenceThread = new Thread(cadenceTask);
            cadenceThread.setDaemon(true);
            cadenceThread.start();
        }

        if (speedCheckMenuItem.isSelected()) {
            Task<Void> speedTask = new Task<Void>() {

                @Override
                protected Void call() throws Exception {
                    combinedVelocitySerie = presenter.getVelocitySerie(timeAsBaseInConbinedChartCheckBox.isSelected());
                    return null;
                }

            };

            speedTask.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
                @Override
                public void handle(WorkerStateEvent event) {

                    combinedChart.getData().add(combinedVelocitySerie);

                }
            });

            Thread velocityThread = new Thread(speedTask);
            velocityThread.setDaemon(true);
            velocityThread.start();
        }
    }
}
