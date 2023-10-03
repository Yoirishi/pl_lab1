package po01.excelparser.events

import javafx.event.Event
import javafx.event.EventType

class ControllerAcceptEvent (source: Any?, val properties: HashMap<String, String>) : Event(source, null, ACCEPT_EVENT_TYPE) {

    companion object {
        @JvmStatic
        val ACCEPT_EVENT_TYPE: EventType<ControllerAcceptEvent> = EventType(EventType.ROOT, "ACCEPT")
    }
}