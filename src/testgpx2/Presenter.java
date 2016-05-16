/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testgpx2;

import java.io.File;
import javafx.collections.ObservableList;
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

/**
 *
 * @author JoseManuel
 */
public class Presenter {

    private ActivityView view;
    private File file;
    private TrackData trackData;
    private ObservableList<Chunk> chunks;

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

    public XYChart.Series getProfileSerie(boolean distanceAsBase) {
        if (distanceAsBase) {
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
        } else {
            return null;
        }
    }

    XYChart.Series getVelocitySerie(boolean distanceAsBase) {
        if (!distanceAsBase) {
            XYChart.Series velocitySerie = new XYChart.Series();
            double distance = 0.0;
            for (int i = 0; i < chunks.size(); i++) {
                velocitySerie.getData().add(new XYChart.Data(distance, chunks.get(i).getSpeed()));
                distance += chunks.get(i).getDistance();
            }
            return velocitySerie;
        } else {
            System.out.print("AUDYHJNAOPFLKÀOSDKÀKD");
            XYChart.Series velocitySerie = new XYChart.Series();
            double time = 0;
            for (int i = 0; i < chunks.size(); i++) {
                velocitySerie.getData().add(new XYChart.Data(time, chunks.get(i).getSpeed()));
                time += ((double)chunks.get(i).getDuration().getSeconds()/3600.0);
            }
            return velocitySerie;
        }
    }

}
