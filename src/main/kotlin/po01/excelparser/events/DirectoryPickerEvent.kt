package po01.excelparser.events

import javafx.event.Event
import javafx.event.EventType

class DirectoryPickerEvent(type: EventType<out Event>, val directoryPath: String): Event(type) {
    companion object {
        val OUTPUT_DIRECTORY_SELECTED: EventType<DirectoryPickerEvent> = EventType("OUTPUT_DIRECTORY_SELECTED")
    }
}