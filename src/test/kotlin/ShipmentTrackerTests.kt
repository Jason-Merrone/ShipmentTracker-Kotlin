import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class ShipmentTrackerTests {

    @Test
    fun testCanceledUpdatePattern() {
        val shipment = Shipment("in transit", "1", 12345)
        TrackingSimulator.addShipment(shipment)
        val updatePattern = CanceledUpdatePattern()
        updatePattern.updateShipment("1", null, 0, null)
        assertEquals("canceled", shipment.getStatus())
    }

    @Test
    fun testCreatedUpdatePattern() {
        val updatePattern = CreatedUpdatePattern()
        updatePattern.updateShipment("2", null, 12345, null)
        val shipment = TrackingSimulator.findShipment("2")
        assertNotNull(shipment)
        assertEquals("created", shipment?.getStatus())
        assertEquals(12345, shipment?.timestamp)
    }

    @Test
    fun testDelayedUpdatePattern() {
        val shipment = Shipment("in transit", "3", 12345)
        TrackingSimulator.addShipment(shipment)
        val updatePattern = DelayedUpdatePattern()
        val expectedDelivery = Instant.now().plusMillis(100000).toEpochMilli()
        updatePattern.updateShipment("3", null, 0, expectedDelivery.toString())
        assertEquals("delayed", shipment.getStatus())
        assertEquals(expectedDelivery, shipment.expectedDeliverDateTimestamp)
    }

    @Test
    fun testDeliveredUpdatePattern() {
        val shipment = Shipment("in transit", "4", 12345)
        TrackingSimulator.addShipment(shipment)
        val updatePattern = DeliveredUpdatePattern()
        updatePattern.updateShipment("4", null, 0, null)
        assertEquals("delivered", shipment.getStatus())
    }

    @Test
    fun testLocationUpdatePattern() {
        val shipment = Shipment("in transit", "5", 12345)
        TrackingSimulator.addShipment(shipment)
        val updatePattern = LocationUpdatePattern()
        updatePattern.updateShipment("5", null, 0, "Warehouse A")
        assertEquals("Warehouse A", shipment.currentLocation)
    }

    @Test
    fun testLostUpdatePattern() {
        val shipment = Shipment("in transit", "6", 12345)
        TrackingSimulator.addShipment(shipment)
        val updatePattern = LostUpdatePattern()
        updatePattern.updateShipment("6", null, 0, null)
        assertEquals("lost", shipment.getStatus())
    }

    @Test
    fun testNoteAddedUpdatePattern() {
        val shipment = Shipment("in transit", "7", 12345)
        TrackingSimulator.addShipment(shipment)
        val updatePattern = NoteAddedUpdatePattern()
        updatePattern.updateShipment("7", null, 0, "Fragile")
        assertTrue(shipment.getNotes().contains("Fragile"))
    }

    @Test
    fun testShippedUpdatePattern() {
        val shipment = Shipment("created", "8", 12345)
        TrackingSimulator.addShipment(shipment)
        val updatePattern = ShippedUpdatePattern()
        val expectedDelivery = Instant.now().plusMillis(100000).toEpochMilli()
        updatePattern.updateShipment("8", null, 0, expectedDelivery.toString())
        assertEquals("shipped", shipment.getStatus())
        assertEquals(expectedDelivery, shipment.expectedDeliverDateTimestamp)
    }

    @Test
    fun testShipment() {
        val shipment = Shipment("created", "9", 12345)
        assertEquals("created", shipment.getStatus())
        assertEquals("9", shipment.id)
        assertEquals(12345, shipment.timestamp)
        shipment.updateStatus("shipped")
        assertEquals("shipped", shipment.getStatus())
        shipment.addNote("Handle with care")
        assertTrue(shipment.getNotes().contains("Handle with care"))
        assertEquals(listOf("created", "shipped"), shipment.getUpdateHistory())
    }

    @Test
    fun testTrackerViewHelper() {
        val tracker = TrackerViewHelper()
        val shipment = Shipment("created", "10", 12345)
        tracker.trackShipment(shipment)
        shipment.updateStatus("shipped")
        shipment.notifyObservers()
        val index = 0
        assertEquals(listOf("created", "shipped"), tracker.updateHistory[index])
        assertEquals("shipped", tracker.status[index])
        tracker.stopTracking(shipment)
        assertTrue(tracker.updateHistory.isEmpty())
    }

    @Test
    fun testTrackingSimulator() {
        val shipment = Shipment("created", "11", 12345)
        TrackingSimulator.addShipment(shipment)
        val retrievedShipment = TrackingSimulator.findShipment("11")
        assertEquals(shipment, retrievedShipment)
    }

    // Helper function to convert timestamp to formatted date string
    private fun formatTime(time: Long?): String {
        return if (time != null) {
            val dateTime = Instant.ofEpochMilli(time).atZone(ZoneId.systemDefault()).toLocalDateTime()
            dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        } else {
            "N/A"
        }
    }
}