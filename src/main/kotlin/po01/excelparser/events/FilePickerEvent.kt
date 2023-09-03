package po01.excelparser.events

import javafx.event.Event
import javafx.event.EventType

class FilePickerEvent(type: EventType<out Event>, val filePath: String, val fileDirectoryPath: String) : Event(type) {
    companion object {
        val FILE_SELECTED: EventType<FilePickerEvent> = EventType("FILE_SELECTED")
    }
}