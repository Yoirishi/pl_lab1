package po01.excelparser.controllers

import io.micronaut.context.BeanContext
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.TextField
import javafx.stage.Stage
import po01.excelparser.SettingsManager
import po01.excelparser.events.ControllerAcceptEvent
import java.net.URL
import java.util.*

class SettingsController: Initializable {


    @FXML
    lateinit var facultyFullNameTextField: TextField

    @FXML
    lateinit var facultyShortNameTextField: TextField

    @FXML
    lateinit var commissioner1PositionTextField: TextField

    @FXML
    lateinit var commissioner2PositionTextField: TextField

    @FXML
    lateinit var committeeChairmanPositionTextField: TextField

    @FXML
    lateinit var cancelButton: Button

    @FXML
    lateinit var acceptButton: Button

    private val beanContext: BeanContext = BeanContext.run()

    @FXML
    fun handleClose(event: ActionEvent) {
        val node = event.source as Node
        val stage = node.scene.window as Stage
        stage.close()
    }

    @FXML
    fun handleAccept(event: ActionEvent) {
        val properties = HashMap<String, String>()
        properties["facultyFullName"] = facultyFullNameTextField.text
        properties["facultyShortName"] = facultyShortNameTextField.text
        properties["committeeChairmanPosition"] = committeeChairmanPositionTextField.text
        properties["commissioner1Position"] = commissioner1PositionTextField.text
        properties["commissioner2Position"] = commissioner2PositionTextField.text

        acceptButton.fireEvent(ControllerAcceptEvent(this, properties))
        handleClose(event)
    }

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        val settingsManager = beanContext.getBean(SettingsManager::class.java)
        facultyFullNameTextField.text = settingsManager.getFacultyFullTitle() ?: ""
        facultyShortNameTextField.text = settingsManager.getFacultyShortTitle() ?: ""
        commissioner1PositionTextField.text = settingsManager.getCommissioner1Position() ?: ""
        commissioner2PositionTextField.text = settingsManager.getCommissioner2Position() ?: ""
        committeeChairmanPositionTextField.text = settingsManager.getCommitteeChairmanPosition() ?: ""
    }
}