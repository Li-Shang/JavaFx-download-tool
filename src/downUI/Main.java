package downUI;

import downUI.model.Resource;
import downUI.model.ResourceForUI;
import downUI.view.MainController;
import downUI.view.AddController;

import java.io.IOException;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;


public class Main extends Application {

    private Stage primaryStage;
    private BorderPane rootLayout;

    private ObservableList<ResourceForUI> filesData = FXCollections.observableArrayList();

    public Main()
    {
        filesData.add(new ResourceForUI(new Resource
                ("https://nodejs.org/dist/v6.9.3/node-v6.9.3-x64.msi","nodejs.msi", 2)));
        filesData.add(new ResourceForUI(new Resource
                ("https://img3.doubanio.com/view/thing_review/large/public/p399130.jpg","1.jpg", 2)));
    }

    @Override
    public void start(Stage primaryStage)
    {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Download Tools");
        this.primaryStage.getIcons().add(new Image("/icon.png"));

        //Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
        try
        {
            // Load root layout from fxml file.
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("view/main.fxml"));
            rootLayout = loader.load();

            // Show the scene containing the root layout.
            primaryStage.setScene(new Scene(rootLayout));
            primaryStage.setResizable(false);
            //primaryStage.initStyle(StageStyle.UNDECORATED);
            //primaryStage.setAlwaysOnTop(true);
            primaryStage.show();

            MainController controller = loader.getController();
            controller.setMainApp(this);

            primaryStage.setOnCloseRequest(event ->
                    {
                        Platform.exit();
                        System.exit(0);
                    }
            );
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

    }

    public void newDialog()
    {
        try
        {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("view/addURL.fxml"));
            AnchorPane addPane = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("New Download");
            dialogStage.setResizable(false);
            dialogStage.getIcons().add(new Image("/icon.png"));
            dialogStage.setScene(new Scene(addPane));

            AddController controller = loader.getController();
            controller.setMainApp(this);
            controller.setDialogStage(dialogStage);

            // Show the dialog and wait until the user closes it
            dialogStage.showAndWait();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    /**
     * Returns the main stage.
     * @return
     */
    public Stage getPrimaryStage()
    {
        return primaryStage;
    }
    public ObservableList<ResourceForUI> getFilesData()
    {
        return filesData;
    }
    public static void main(String[] args)
    {
        launch(args);
    }
}
