package po01.excelparser.controllers

import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.layout.HBox
import javafx.stage.FileChooser
import po01.excelparser.events.FilePickerEvent
import java.io.File
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.absolutePathString

class FilePickerController : HBox() {
    @FXML
    private lateinit var filePathTextField: TextField

    @FXML
    private lateinit var selectButton: Button

    @FXML
    private lateinit var filePurpose: Label

    private var lastDirectory: Path? = null

    init {
        val fxmlLoader = FXMLLoader(javaClass.getResource("/fxml/FilePicker.fxml"))
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
            val fileChooser = FileChooser()
            lastDirectory?.let { fileChooser.initialDirectory = it.toFile() }
            val selectedFile: File? = try {
                fileChooser.showOpenDialog(null)
            } catch (_: Exception) {
                fileChooser.initialDirectory = null
                fileChooser.showOpenDialog(null)
            }
            if (selectedFile != null) {
                setFilePathText(selectedFile.absolutePath)
                lastDirectory = Paths.get(selectedFile.parent)

                val lastDirectoryPath = lastDirectory?.absolutePathString() ?: ""
                val filePath = selectedFile.absolutePath

                handleFileSelected(filePath, lastDirectoryPath)
            }
        }
    }

    fun setFilePurposeText(newText: String) {
        filePurpose.text = newText
    }

    fun setFilePathText(newText: String) {
        filePathTextField.text = newText
    }

    fun setLastDirectory(newDirectory: String?) {
        lastDirectory = newDirectory?.let { Paths.get(it) }
    }

    fun getPath(): String {
        return filePathTextField.text
    }

    fun handleFileSelected(filePath: String, fileDirectory: String) {
        val event = FilePickerEvent(FilePickerEvent.FILE_SELECTED, filePath, fileDirectory)
        fireEvent(event)
    }
}