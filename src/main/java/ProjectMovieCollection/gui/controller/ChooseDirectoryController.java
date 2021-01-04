/**
 * @author kjell
 */

package ProjectMovieCollection.gui.controller;

import java.io.File;
import java.io.IOException;

import ProjectMovieCollection.App;
import ProjectMovieCollection.bll.Directory;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

public class ChooseDirectoryController {

    @FXML
    private AnchorPane anchorPane;
    @FXML
    private TextField directoryTextField;

    private final DirectoryChooser dirchoose = new DirectoryChooser();
    private final Directory dir = new Directory();

    public void browseButtonAction(ActionEvent actionEvent) {
        File file = dirchoose.showDialog((Stage) anchorPane.getScene().getWindow());

        dir.setFilepath(file);

        if (dir.getFilepath() != null) {
            System.out.println(dir.getFilepath());
            directoryTextField.setText(dir.getFilepath());
        }
    }

    public void confirmButtonAction(ActionEvent actionEvent) throws IOException {
        if (dir.getFilepath() != null) {
            App.setRoot("view/primary");
        } else {
            // TODO: Implement UI prompt
            System.out.println("Choose a path");
        }
    }
}
