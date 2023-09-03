package po01.excelparser.controllers

import io.micronaut.context.BeanContext
import jakarta.inject.Singleton
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.Alert
import javafx.scene.control.Button
import po01.excelparser.ExcelParser
import po01.excelparser.SettingsManager
import po01.excelparser.events.DirectoryPickerEvent
import po01.excelparser.events.FilePickerEvent
import java.lang.Exception
import java.net.URL
import java.util.*
import kotlin.contracts.contract


@Singleton
class MainController: Initializable {

    @FXML
    lateinit var generateButton: Button

    @FXML
    lateinit var outputDirectory: DirectoryPickerController

    @FXML
    lateinit var gcPicker: FilePickerController

    @FXML
    lateinit var wpPicker: FilePickerController

    private val beanContext: BeanContext = BeanContext.run()


    override fun initialize(location: URL?, resources: ResourceBundle?) {
        gcPicker.setFilePurposeText("Аттестационный лист студента")
        wpPicker.setFilePurposeText("Учебный план")
        outputDirectory.setDirectoryPurposeText("Директория для генерации документа")

        val settingsManager = beanContext.getBean(SettingsManager::class.java)
        val lastOutputDirectory = settingsManager.getLastOutputDirectory() ?: ""
        val lastWpTable = settingsManager.getLastWpTable() ?: ""
        val lastGcDirectory = settingsManager.getLastGcDirectory()

        gcPicker.setLastDirectory(lastGcDirectory)
        wpPicker.setFilePathText(lastWpTable)
        outputDirectory.setDirectoryPathText(lastOutputDirectory)


        generateButton.setOnAction {
            val pathToGc = gcPicker.getPath()
            val pathToWp = wpPicker.getPath()
            val outputPath = outputDirectory.getOutputPath()
            val excelParser  = beanContext.getBean(ExcelParser::class.java)
            try {
                excelParser.start(pathToGc, pathToWp, outputPath)
                showSuccessDialog("Файл сгенерирован успешно")
            } catch (e: Exception) {
                e.message?.let { showErrorDialog(it) } ?: run { showErrorDialog("Internal server error") }
            }
        }

        gcPicker.addEventHandler(FilePickerEvent.FILE_SELECTED) { event  ->
            val fileDirectory = event.fileDirectoryPath
            settingsManager.setLastGcDirectory(fileDirectory)
        }
        wpPicker.addEventHandler(FilePickerEvent.FILE_SELECTED) {event ->
            val filePath = event.filePath
            settingsManager.setLastWpTable(filePath)
        }
        outputDirectory.addEventHandler(DirectoryPickerEvent.OUTPUT_DIRECTORY_SELECTED) {event ->
            val directoryPath = (event as DirectoryPickerEvent).directoryPath
            settingsManager.setLastOutputDirectory(directoryPath)
        }

    }

    private fun showErrorDialog(errorMessage: String) {
        val alert = Alert(Alert.AlertType.ERROR)
        alert.title = "Error"
        alert.headerText = "An error occurred"
        alert.contentText = errorMessage
        alert.showAndWait()
    }

    private fun showSuccessDialog(successMessage: String) {
        val alert = Alert(Alert.AlertType.INFORMATION)
        alert.title = "Success"
        alert.headerText = "Operation Completed Successfully"
        alert.contentText = successMessage
        alert.showAndWait()
    }

    fun handleClose() {
        beanContext.close()
    }
}