package downUI.view;

import downUI.model.ResourceForUI;
import downUI.Main;

import javafx.beans.property.SimpleStringProperty;
import javafx.util.Callback;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.Pos;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.EventListenerProxy;

/**
 * Created by Administrator on 2017/1/5.
 */
public class MainController
{
    @FXML
    private TableView<ResourceForUI> tableDownloads;
    @FXML
    private TableColumn<ResourceForUI, String> colName;
    @FXML
    private TableColumn<ResourceForUI, String> colStatus;
    @FXML
    private TableColumn<ResourceForUI, Number> colProgress;
    @FXML
    private TableColumn<ResourceForUI, Number> colSpeed;
    @FXML
    private Button btnNew;
    @FXML
    private Button btnPause;
    @FXML
    private Button btnResume;
    @FXML
    private Button btnStop;
    @FXML
    private Button btnDelete;

    private ChangeListener<Object> changelistener = new ChangeListener<Object>()
    {
        @Override
        public void changed(ObservableValue observable, Object oldValue, Object newValue)
        {
            changeButtonState((String)newValue);
            //System.out.println("selected status changed");
            //System.out.println(newValue);
        }
    };

    private Main mainApp;

    public MainController()
    {

    }
    @FXML
    private void initialize()
    {
        // Initialize
        tableDownloads.setRowFactory( table ->
        {
            return new TableRow<ResourceForUI>()
            {
                private Tooltip tooltip = new Tooltip();

                @Override
                public void updateItem(ResourceForUI item, boolean empty)
                {
                    super.updateItem(item, empty);
                    if(empty)
                    {
                        setGraphic(null);
                    }
                    else if (item != null)
                    {
                        float m = 0.0f;
                        int k = item.getFileSize()/1024;
                        String size;
                        if(k >= 1000)
                        {
                            m = k/1000;
                            k = k%1000;
                        }
                        if(m > 1)
                            size = m + "M";
                        else
                            size = k + "K";

                        tooltip.setText("URL: " + item.getUrl() + "\n\n" +
                                        "Size: " + size + "\n\n" +
                                        "ThreadNum: " + item.getThreadNum());
                        setTooltip(tooltip);
                    }
                }
            };
        });
        colName.setCellValueFactory(cellData -> cellData.getValue().fileNameProperty());
        colStatus.setCellValueFactory(cellData -> cellData.getValue().statusProperty());
        colSpeed.setCellValueFactory(cellData -> cellData.getValue().speedProperty());
        colSpeed.setCellFactory( cell ->
        {
            return new TableCell<ResourceForUI, Number>()
            {
                @Override
                public void updateItem(Number item, boolean empty)
                {
                    super.updateItem(item, empty);
                    if (empty)
                    {
                        setText(null);
                    }
                    else if (item != null)
                    {
                        setText(item + "KB/S");
                        setContentDisplay(ContentDisplay.TEXT_ONLY);
                    }
                }
            };

        });
        colProgress.setCellValueFactory(cellData -> cellData.getValue().progressProperty());
        //colProgress.setCellValueFactory(new PropertyValueFactory<ResourceForUI, Number>("progress"));

        colProgress.setCellFactory ( cell ->
        {
            return new TableCell<ResourceForUI, Number>()
            {
                private ProgressBar pb = new ProgressBar();

                @Override
                public void updateItem(Number item, boolean empty)
                {
                    super.updateItem(item, empty);
                    if(empty)
                    {
                        setGraphic(null);
                    }
                    //System.out.println(empty);
                    else if (item != null)
                    {
                        pb.setPrefWidth(140);
                        pb.setProgress(item.doubleValue());
                        setGraphic(pb);
                        setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                    }

                }
            };
        });

        /*
        Callback<TableColumn<ResourceForUI, Double>, TableCell<ResourceForUI, Double>> cellFactory =
                new Callback<TableColumn<ResourceForUI, Double>, TableCell<ResourceForUI, Double>>() {
                    public TableCell call(TableColumn<ResourceForUI, Double> p) {
                        return new TableCell<ResourceForUI, Double>() {
                            private ProgressBar pb = new ProgressBar();
                            @Override
                            public void updateItem(Double item, boolean empty) {
                                super.updateItem(item, empty);
                                if(item != null)
                                {
                                    pb.setProgress(item);
                                    setGraphic(pb);
                                    setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                                }
                            }
                        };
                    }
                };
        colProgress.setCellFactory(cellFactory);
        */
        tableDownloads.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldSelected, newSelected) ->
                {
                    if (newSelected != null)
                    {
                        if(oldSelected != null)
                            oldSelected.getStatusProperty().removeListener(changelistener);
                        newSelected.getStatusProperty().addListener(changelistener);

                        changeButtonState(newSelected.getStatus());
                    }
                });


    }
    private void changeButtonState(String status)
    {
        btnPause.setDisable(false);
        btnResume.setDisable(false);
        btnStop.setDisable(false);
        btnDelete.setDisable(true);
        if(status == "paused")
            btnPause.setDisable(true);
        if(status == "downloading")
            btnResume.setDisable(true);
        if(status == "stop")
        {
            btnDelete.setDisable(false);
            btnStop.setDisable(true);
        }
        if(status == "finished")
        {
            btnPause.setDisable(true);
            btnResume.setDisable(true);
            btnDelete.setDisable(false);
            btnStop.setDisable(true);
        }
        if(status == "waiting ...")
        {
            btnDelete.setDisable(false);
        }
        if(status == "deleted")
        {
            btnPause.setDisable(true);
            btnResume.setDisable(true);
            btnDelete.setDisable(true);
            btnStop.setDisable(true);
        }
    }
    @FXML
    private void onNew()
    {
        this.mainApp.newDialog();
    }
    @FXML
    private void onResume()
    {
        int index = tableDownloads.getSelectionModel().getSelectedIndex();
        if(index >= 0)
        {
            tableDownloads.getItems().get(index).resumeDownload();
        }
        //System.out.println("you pressed Resume button " + index);

    }
    @FXML
    private void onPause()
    {
        int index = tableDownloads.getSelectionModel().getSelectedIndex();
        if(index >= 0)
        {
            tableDownloads.getItems().get(index).pauseDownload();
        }
        //System.out.println("you pressed Pause button " + index);
    }
    @FXML
    private void onStop()
    {
        int index = tableDownloads.getSelectionModel().getSelectedIndex();
        if(index >= 0)
        {
            tableDownloads.getItems().get(index).stopDownload();
        }
        //System.out.println("you pressed Stop button " + index);
    }
    @FXML
    private void onDelete()
    {
        int index = tableDownloads.getSelectionModel().getSelectedIndex();
        if(index >= 0)
        {
            //remove progress bar and tooltip

            tableDownloads.getItems().get(index).setStatus("deleted");
            tableDownloads.getItems().remove(index);
        }
    }
    public void setMainApp(Main app)
    {
        this.mainApp = app;
        tableDownloads.setItems(mainApp.getFilesData());
    }

}
