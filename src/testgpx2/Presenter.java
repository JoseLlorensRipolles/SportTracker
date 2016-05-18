/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testgpx2;

import java.io.File;
import java.text.DecimalFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
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
import jgpx.util.DateTimeUtils;

/**
 *
 * @author JoseManuel
 */
public class Presenter {

    private ActivityView view;
    private File file;
    private TrackData trackData;
    private ObservableList<Chunk> chunks;
    private DecimalFormat decimalFormatter = new DecimalFormat(".##");

    public Presenter() {
    }

    public Presenter(ActivityView view) {
        this.view = view;
    }

    public void load() {
        try {
            FileChooser fileChooser = new FileChooser();
            file = fileChooser.showOpenDialog(null);
            if (file == null) {
                return;
            }
            JAXBContext jaxbContext = JAXBContext.newInstance(GpxType.class,
                    TrackPointExtensionT.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            JAXBElement<Object> root = (JAXBElement<Object>) unmarshaller.unmarshal(file);
            GpxType gpx = (GpxType) root.getValue();
            trackData = new TrackData(new Track(gpx.getTrk().get(0)));
            chunks = trackData.getChunks();
        } catch (JAXBException e) {
            System.err.println("Error: " + e);
        } catch (Exception e) {
            System.err.println("Error de caracter general: " + e);

        }

    }

    public String getFileName() {
        String name = file.getName();
        name = name.replace('+', ' ');
        name = name.replace('_', ' ');
        name = name.substring(0, name.length() - 4);
        return name;

    }

    public XYChart.Series getProfileSerie(boolean timeAsBase) {
        if (timeAsBase) {

            XYChart.Series profileSerie = new XYChart.Series();
            double time = 0.0;
            double elevation = 0.0;
            for (int i = 0; i < chunks.size(); i++) {
                profileSerie.getData().add(new XYChart.Data(time, elevation));
                time += ((double) chunks.get(i).getDuration().getSeconds() / 60.0);
                elevation += chunks.get(i).getAscent();
                elevation -= chunks.get(i).getDescend();
            }
            return profileSerie;

        } else {
            XYChart.Series profileSerie = new XYChart.Series();
            double distance = 0.0;
            double elevation = 0.0;
            for (int i = 0; i < chunks.size(); i++) {
                profileSerie.getData().add(new XYChart.Data(distance, elevation));
                distance += chunks.get(i).getDistance();
                elevation += chunks.get(i).getAscent();
                elevation -= chunks.get(i).getDescend();
            }
            return profileSerie;
        }
    }

    public XYChart.Series getVelocitySerie(boolean timeAsBase) {
        if (timeAsBase) {
            XYChart.Series velocitySerie = new XYChart.Series();
            double time = 0;
            for (int i = 0; i < chunks.size(); i++) {
                velocitySerie.getData().add(new XYChart.Data(time, chunks.get(i).getSpeed()));
                time += ((double) chunks.get(i).getDuration().getSeconds() / 60.0);
            }
            return velocitySerie;

        } else {
            XYChart.Series velocitySerie = new XYChart.Series();
            double distance = 0.0;
            for (int i = 0; i < chunks.size(); i++) {
                velocitySerie.getData().add(new XYChart.Data(distance, chunks.get(i).getSpeed()));
                distance += chunks.get(i).getDistance();
            }
            return velocitySerie;
        }
    }

    public String getDate() {
        return DateTimeUtils.format(trackData.getStartTime());

    }

    public String getDuration() {
        Duration duration = trackData.getTotalDuration();
        long seconds = duration.getSeconds();
        long minutes = seconds / 60;
        long hours = seconds / 3600;
        return hours + ":" + (minutes - hours * 60) + ":" + (seconds - minutes * 60);
    }

    public String getTimeInMovement() {
        return DateTimeUtils.format(trackData.getMovingTime());
    }

    public String getTotalDistance() {
        return decimalFormatter.format(trackData.getTotalDistance()) + "m";
    }

    public String getCumulativeAltitude() {
        return "Ascenso: " + decimalFormatter.format(trackData.getTotalAscent()) + "m.  Descenso: " + decimalFormatter.format(trackData.getTotalDescend()) + "m.";
    }

    public String getMaxVelocity() {
        return decimalFormatter.format(trackData.getMaxSpeed()) + "m/s";
    }

    public String getMedVelocity() {
        return decimalFormatter.format(trackData.getAverageSpeed()) + "m/s";
    }

    public String getMaxFC() {
        return trackData.getMaxHeartrate() + "";
    }

    public String getMedFC() {
        return "" + trackData.getAverageHeartrate();
    }

    public String getMinFC() {
        return "" + trackData.getMinHeartRate();
    }

    public String getMaxCadence() {
        return "" + trackData.getMaxCadence();
    }

    public String getMedCandence() {
        return "" + trackData.getAverageCadence();
    }

    public XYChart.Series getFCSeries(boolean timeAsBase) {
        if (timeAsBase) {
            XYChart.Series fCSeries = new XYChart.Series();
            double time = 0.0;
            for (int i = 0; i < chunks.size(); i++) {
                fCSeries.getData().add(new XYChart.Data(time, chunks.get(i).getAvgHeartRate()));
                time += (double)chunks.get(i).getDuration().getSeconds() / 60.0;
            }
            return fCSeries;
        } else {
            XYChart.Series fCSeries = new XYChart.Series();
            double distance = 0.0;
            for (int i = 0; i < chunks.size(); i++) {
                fCSeries.getData().add(new XYChart.Data(distance, chunks.get(i).getAvgHeartRate()));
                distance += chunks.get(i).getDistance();
            }
            return fCSeries;
        }
    }

    public XYChart.Series getCadenceSeries(boolean timeAsBase) {
        if (timeAsBase) {
            XYChart.Series cadenceSeries = new XYChart.Series();
            double time = 0.0;
            for (int i = 0; i < chunks.size(); i++) {
                cadenceSeries.getData().add(new XYChart.Data(time, chunks.get(i).getAvgCadence()));
                time += (double)chunks.get(i).getDuration().getSeconds() / 60.0;
            }
            return cadenceSeries;
        } else {
            XYChart.Series cadenceSeries = new XYChart.Series();
            double distance = 0.0;
            for (int i = 0; i < chunks.size(); i++) {
                cadenceSeries.getData().add(new XYChart.Data(distance, chunks.get(i).getAvgCadence()));
                distance += chunks.get(i).getDistance();
            }
            return cadenceSeries;
        }
    }

    public ObservableList<PieChart.Data> getZonesData() {
        int z1 = 0, z2 = 0, z3 = 0, z4 = 0, z5 = 0;
        double hearthRate;
        double maxHearthRate = trackData.getMaxHeartrate();

        for (int i = 0; i < chunks.size(); i++) {
            hearthRate = chunks.get(i).getAvgHeartRate();
            if (hearthRate < (maxHearthRate * 0.6)) {
                z1++;
            } else if (hearthRate < (maxHearthRate * 0.7)) {
                z2++;
            } else if (hearthRate < (maxHearthRate * 0.8)) {
                z3++;
            } else if (hearthRate < (maxHearthRate * 0.9)) {
                z4++;
            } else {
                z5++;
            }
        }
        int totalObservations = z1 + z2 + z3 + z4 + z5;
        ObservableList<PieChart.Data> pieChartData
                = FXCollections.observableArrayList(
                        new PieChart.Data("Recuperación", ((double) z1) / ((double) totalObservations)),
                        new PieChart.Data("Fondo", ((double) z2) / ((double) totalObservations)),
                        new PieChart.Data("Tempo", ((double) z3) / ((double) totalObservations)),
                        new PieChart.Data("Umbral", ((double) z4) / ((double) totalObservations)),
                        new PieChart.Data("Anaeróbico", ((double) z5) / ((double) totalObservations))
                );
        return pieChartData;
    }

}
