/**
 * @author kjell
 */

package ProjectMovieCollection.gui.controller;

import ProjectMovieCollection.be.Movie;
import ProjectMovieCollection.be.MovieSearchResult;
import ProjectMovieCollection.bll.AlertManager;
import ProjectMovieCollection.bll.MovieManager;
import ProjectMovieCollection.gui.model.EditMetadataModel;
import ProjectMovieCollection.utils.exception.MovieDAOException;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;


public class EditMetadataController implements Initializable {

    @FXML
    private JFXTextField movieID;
    @FXML
    private VBox VBox;
    @FXML
    private JFXListView<MovieSearchResult> relatedMovieList;

    private AlertManager am;
    private EditMetadataModel editMetadataModel;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        am = new AlertManager();
        try {
            editMetadataModel = new EditMetadataModel();
        } catch (IOException e) {
            am.displayError(e);
        }

        relatedMovieList.setItems(editMetadataModel.getObservableChoices());
    }

    public void cancelButton(ActionEvent actionEvent) {
        Stage stage = (Stage) VBox.getScene().getWindow();
        stage.close();
    }

    public void confirmButton(ActionEvent actionEvent) {
        //TODO: Implementation needed
        if (relatedMovieList.getSelectionModel().getSelectedItem() == null) {
            am.displayError("No Movie Selected", "Please select a movie");
        } else {
            try {
                editMetadataModel.updateFromMovieResult(relatedMovieList.getSelectionModel().getSelectedItem());
            } catch (MovieDAOException e) {
                am.displayError(e);
            }
            Stage stage = (Stage) VBox.getScene().getWindow();
            stage.close();
        }

    }

    public void searchForMovies(ActionEvent actionEvent) {
        editMetadataModel.search(movieID.getText());
    }

    public void setMovieManager(MovieManager manager) {
        editMetadataModel.setMovieManager(manager);
    }

    public void setMovie(Movie m) {
        editMetadataModel.setMovie(m);
    }
}