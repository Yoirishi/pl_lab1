package po01.excelparser

import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.stage.Stage
import po01.excelparser.controllers.MainController


class Main : Application() {
    override fun start(primaryStage: Stage?) {
        val loader = FXMLLoader(javaClass.getResource("/fxml/Main.fxml"))
        val root: Parent = loader.load()

        val scene = Scene(root)

        val controller = loader.getController() as MainController

        primaryStage?.setOnCloseRequest {
            controller.handleClose()
        }

        primaryStage?.scene = scene
        primaryStage?.show()
    }
}