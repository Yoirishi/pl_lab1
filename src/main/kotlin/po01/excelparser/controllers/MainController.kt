package po01.excelparser.controllers

import io.micronaut.context.BeanContext
import jakarta.inject.Singleton
import javafx.application.Platform
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.fxml.Initializable
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.Alert
import javafx.scene.control.Button
import javafx.scene.control.CheckBox
import javafx.scene.control.ComboBox
import javafx.scene.control.MenuItem
import javafx.stage.Stage
import javafx.util.StringConverter
import po01.excelparser.ExcelParser
import po01.excelparser.SettingsManager
import po01.excelparser.StudentParser
import po01.excelparser.enums.StudentProcess
import po01.excelparser.enums.TargetCourse
import po01.excelparser.events.DirectoryPickerEvent
import po01.excelparser.events.FilePickerEvent
import java.awt.Desktop
import java.io.File
import java.io.IOException
import java.lang.Exception
import java.net.URL
import java.util.*


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


    @FXML
    lateinit var targetCourseComboBox: ComboBox<TargetCourse>

    @FXML
    lateinit var processComboBox: ComboBox<StudentProcess>

    @FXML
    lateinit var protocolSettingsMenuItem: MenuItem

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
                excelParser.start(pathToGc, pathOfPlan, outputPath, processComboBox.value, targetCourseComboBox.value)
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
            val studentParser  = beanContext.getBean(StudentParser::class.java)
            try {
                val gradeControl = studentParser.parse(gcPicker.getPath())
                val maxCourse = gradeControl.size.toUInt()
                val availableCourses = TargetCourse.getListForIntegerValue(maxCourse)
                targetCourseComboBox.items = FXCollections.observableArrayList(availableCourses)
                targetCourseComboBox.selectionModel.selectLast()
            } catch (e: Exception) {
                e.printStackTrace()
                e.message?.let { showErrorDialog(it) } ?: run { showErrorDialog("Internal server error") }
            }
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

        processComboBox.items.addAll(StudentProcess.values().toList())
        processComboBox.selectionModel.selectFirst()
        processComboBox.converter = object : StringConverter<StudentProcess>() {
            override fun toString(processType: StudentProcess): String {
                return when (processType) {
                    StudentProcess.WithdrawalFromLeaveOfAbsence -> "Выход из академического отпуска"
                    StudentProcess.Transfer -> "Перевод"
                    StudentProcess.Reinstatement -> "Восстановление"
                }
            }

            override fun fromString(value: String): StudentProcess {
                return when (value) {
                    "Выход из академического отпуска" -> StudentProcess.WithdrawalFromLeaveOfAbsence
                    "Перевод" -> StudentProcess.Transfer
                    "Восстановление"  -> StudentProcess.Reinstatement
                    else -> throw IllegalArgumentException()
                }
            }
        }

        targetCourseComboBox.items.addAll(TargetCourse.values().toList())
        targetCourseComboBox.selectionModel.selectLast()
        targetCourseComboBox.converter = object : StringConverter<TargetCourse>() {
            override fun toString(tragetCourse: TargetCourse?): String {
                return when (tragetCourse) {
                    TargetCourse.FIRST -> "1"
                    TargetCourse.SECOND -> "2"
                    TargetCourse.THIRD -> "3"
                    TargetCourse.FOURTH -> "4"
                    TargetCourse.FIFTH -> "5"
                    TargetCourse.SIXTH -> "6"
                    TargetCourse.SEVENTH -> "7"
                    TargetCourse.EIGHTH -> "8"
                    null -> ""
                }
            }

            override fun fromString(value: String): TargetCourse {
                return when (value) {
                    "1" ->  TargetCourse.FIRST
                    "2" -> TargetCourse.SECOND
                    "3" ->  TargetCourse.THIRD
                    "4" -> TargetCourse.FOURTH
                    "5" ->  TargetCourse.FIFTH
                    "6" ->  TargetCourse.SIXTH
                    "7" ->TargetCourse.SEVENTH
                    "8" -> TargetCourse.EIGHTH
                    else -> throw IllegalArgumentException()
                }
            }
        }

        protocolSettingsMenuItem.setOnAction { openNewForm("/fxml/ProtocolSettings.fxml" ,"Protocol settings") }

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

    fun openNewForm(pathToForm: String, formTitle: String) {
        try {
            val loader = FXMLLoader(javaClass.getResource(pathToForm))
            val root: Parent = loader.load()

            val stage = Stage()
            stage.scene = Scene(root)

            stage.title = formTitle

            stage.show()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}