package downUI.view;

import downUI.Main;
import downUI.model.Resource;
import downUI.model.ResourceForUI;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
/**
 * Created by Administrator on 2017/1/5.
 */
public class AddController
{
    @FXML
    private TextField urlField;
    @FXML
    private TextField nameField;
    @FXML
    private TextField numField;
    @FXML
    private Button btnOk;
    @FXML
    private Button btnCancel;

    private Main mainApp;
    private Stage dialogStage;

    public AddController()
    {

    }
    @FXML
    private void initialize()
    {
        // Initialize
    }
    @FXML
    private void onOk()
    {
        this.mainApp.getFilesData().add(new ResourceForUI(new Resource(
                urlField.getText(), nameField.getText(), Integer.parseInt(numField.getText())
        )));
        dialogStage.close();
        int index = this.mainApp.getFilesData().size() - 1;
        this.mainApp.getFilesData().get(index).resumeDownload();
    }
    @FXML
    private void onCancel()
    {
        dialogStage.close();
    }
    public void setMainApp(Main App)
    {
        this.mainApp = App;
    }
    public void setDialogStage(Stage dialogStage)
    {
        this.dialogStage = dialogStage;
    }
}
