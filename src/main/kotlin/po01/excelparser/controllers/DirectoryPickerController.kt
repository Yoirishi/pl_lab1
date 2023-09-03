package po01.excelparser.controllers

import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.layout.HBox
import javafx.stage.DirectoryChooser
import po01.excelparser.events.DirectoryPickerEvent
import java.io.File
import java.nio.file.Path
import java.nio.file.Paths

class DirectoryPickerController : HBox() {
    @FXML
    private lateinit var directoryPathTextField: TextField

    @FXML
    private lateinit var selectButton: Button

    @FXML
    private lateinit var directoryPurpose: Label

    private var lastDirectory: Path? = null

    init {
        val fxmlLoader = FXMLLoader(javaClass.getResource("/fxml/DirectoryPicker.fxml"))
        fxmlLoader.setRoot(this)
        fxmlLoader.setController(this)
        try {
            fxmlLoader.load<Any>()
        } catch (exception: Exception) {
            throw RuntimeException(exception)
        }
    }

    @FXML
    fun initialize() {
        selectButton.setOnAction {
            val directoryChooser = DirectoryChooser()
            lastDirectory?.let { directoryChooser.initialDirectory = it.toFile() }
            val selectedDirectory: File? = try {
                 directoryChooser.showDialog(null)
            } catch (_: Exception) {
                directoryChooser.initialDirectory = null
                directoryChooser.showDialog(null)
            }
            if (selectedDirectory != null) {
                setDirectoryPathText(selectedDirectory.absolutePath)
                lastDirectory = Paths.get(selectedDirectory.absolutePath)
                handleDirectorySelected(selectedDirectory.absolutePath)
            }
        }
    }

    fun setDirectoryPurposeText(newText: String) {
        directoryPurpose.text = newText
    }

    fun setDirectoryPathText(newText: String) {
        directoryPathTextField.text = newText
    }

    fun getOutputPath(): String {
        return directoryPathTextField.text
    }

    fun handleDirectorySelected(directoryPath: String) {
        val event = DirectoryPickerEvent(DirectoryPickerEvent.OUTPUT_DIRECTORY_SELECTED, directoryPath)
        fireEvent(event)
    }
}