package po01.excelparser.controllers

import io.micronaut.context.BeanContext
import jakarta.inject.Singleton
import javafx.application.Platform
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.Alert
import javafx.scene.control.Button
import javafx.scene.control.CheckBox
import po01.excelparser.ExcelParser
import po01.excelparser.SettingsManager
import po01.excelparser.events.DirectoryPickerEvent
import po01.excelparser.events.FilePickerEvent
import java.awt.Desktop
import java.io.File
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

    @FXML
    lateinit var practicePicker: FilePickerController

    @FXML
    lateinit var openDirectoryAfterGeneratingCheckbox: CheckBox

    @FXML
    lateinit var parsePracticeWithMainWPCheckbox: CheckBox

    private val beanContext: BeanContext = BeanContext.run()


    override fun initialize(location: URL?, resources: ResourceBundle?) {
        gcPicker.setFilePurposeText("Аттестационный лист студента")
        wpPicker.setFilePurposeText("Учебный план")
        practicePicker.setFilePurposeText("План практик")
        outputDirectory.setDirectoryPurposeText("Директория для генерации документа")

        val settingsManager = beanContext.getBean(SettingsManager::class.java)
        val lastOutputDirectory = settingsManager.getLastOutputDirectory() ?: ""
        val lastWpTable = settingsManager.getLastWpTable() ?: ""
        val lastGcDirectory = settingsManager.getLastGcDirectory()
        val lastPracticeTable = settingsManager.getLastPracticeTable() ?: ""

        gcPicker.setLastDirectory(lastGcDirectory)
        wpPicker.setFilePathText(lastWpTable)
        practicePicker.setFilePathText(lastPracticeTable)
        outputDirectory.setDirectoryPathText(lastOutputDirectory)
        openDirectoryAfterGeneratingCheckbox.isSelected = settingsManager.getOpenDirectoryAfterGeneratingCheckboxValue()
        parsePracticeWithMainWPCheckbox.isSelected = settingsManager.getParsePracticeWithMainWPCheckboxValue()

        practicePicker.isVisible = !parsePracticeWithMainWPCheckbox.isSelected

        generateButton.setOnAction {
            val pathToGc = gcPicker.getPath()
            val pathToWp = wpPicker.getPath()
            val pathToPractice = practicePicker.getPath()
            val outputPath = outputDirectory.getOutputPath()
            val excelParser  = beanContext.getBean(ExcelParser::class.java)
            try {
                val pathOfPlan = if (parsePracticeWithMainWPCheckbox.isSelected) {
                    listOf(pathToWp)
                } else {
                    listOf(pathToWp, pathToPractice)
                }
                excelParser.start(pathToGc, pathOfPlan, outputPath)
                showSuccessDialog("Файл сгенерирован успешно")
                if (openDirectoryAfterGeneratingCheckbox.isSelected) openDirectory(outputPath)
            } catch (e: Exception) {
                e.printStackTrace()
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
        practicePicker.addEventHandler(FilePickerEvent.FILE_SELECTED) {event ->
            val filePath = event.filePath
            settingsManager.setLastPracticeTable(filePath)
        }
        outputDirectory.addEventHandler(DirectoryPickerEvent.OUTPUT_DIRECTORY_SELECTED) {event ->
            val directoryPath = (event as DirectoryPickerEvent).directoryPath
            settingsManager.setLastOutputDirectory(directoryPath)
        }

        openDirectoryAfterGeneratingCheckbox.setOnAction {
            settingsManager.setOpenDirectoryAfterGeneratingCheckboxValue(openDirectoryAfterGeneratingCheckbox.isSelected)
        }

        parsePracticeWithMainWPCheckbox.setOnAction {
            settingsManager.setParsePracticeWithMainWPCheckboxValue(parsePracticeWithMainWPCheckbox.isSelected)
            practicePicker.isVisible = !parsePracticeWithMainWPCheckbox.isSelected
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

    private fun openDirectory(path: String) {
        if (Desktop.isDesktopSupported()) {
            Platform.runLater {
                val file = File(path)
                if (file.exists() && file.isDirectory) {
                    Desktop.getDesktop().open(file)
                }
            }
        }
    }
}