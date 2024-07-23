import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import updateStrategies.*
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

    @Test
    fun testTrackingSimulatorFindNonExistingShipment() {
        val shipment = TrackingSimulator.findShipment("non_existing_id")
        assertNull(shipment)
    }

    @Test
    fun testTrackingSimulatorProcessUpdateCreated() {
        val updateString = "created,12,1678886400000"
        TrackingSimulator.processUpdate(updateString)
        val shipment = TrackingSimulator.findShipment("12")
        assertNotNull(shipment)
        assertEquals("created", shipment?.getStatus())
        assertEquals(1678886400000, shipment?.timestamp)
    }

    @Test
    fun testTrackingSimulatorProcessUpdateShipped() {
        val createdString = "created,13,1678886400000"
        TrackingSimulator.processUpdate(createdString)
        val expectedDelivery = Instant.now().plusMillis(100000).toEpochMilli()
        val updateString = "shipped,13,1678896400000,$expectedDelivery"
        TrackingSimulator.processUpdate(updateString)
        val shipment = TrackingSimulator.findShipment("13")
        assertNotNull(shipment)
        assertEquals("shipped", shipment?.getStatus())
        assertEquals(expectedDelivery, shipment?.expectedDeliverDateTimestamp)
    }

    @Test
    fun testTrackingSimulatorProcessUpdateLocation() {
        val createdString = "created,14,1678886400000"
        TrackingSimulator.processUpdate(createdString)
        val updateString = "location,14,0,Warehouse B"
        TrackingSimulator.processUpdate(updateString)
        val shipment = TrackingSimulator.findShipment("14")
        assertNotNull(shipment)
        assertEquals("Warehouse B", shipment?.currentLocation)
    }

    @Test
    fun testTrackingSimulatorProcessUpdateDelayed() {
        val createdString = "created,15,1678886400000"
        TrackingSimulator.processUpdate(createdString)
        val expectedDelivery = Instant.now().plusMillis(200000).toEpochMilli()
        val updateString = "delayed,15,0,$expectedDelivery"
        TrackingSimulator.processUpdate(updateString)
        val shipment = TrackingSimulator.findShipment("15")
        assertNotNull(shipment)
        assertEquals("delayed", shipment?.getStatus())
        assertEquals(expectedDelivery, shipment?.expectedDeliverDateTimestamp)
    }

    @Test
    fun testTrackingSimulatorProcessUpdateNoteAdded() {
        val createdString = "created,16,1678886400000"
        TrackingSimulator.processUpdate(createdString)
        val updateString = "noteadded,16,0,Handle with extreme care"
        TrackingSimulator.processUpdate(updateString)
        val shipment = TrackingSimulator.findShipment("16")
        assertNotNull(shipment)
        assertTrue(shipment?.getNotes()?.contains("Handle with extreme care") ?: false)
    }

    @Test
    fun testTrackingSimulatorProcessUpdateLost() {
        val createdString = "created,17,1678886400000"
        TrackingSimulator.processUpdate(createdString)
        val updateString = "lost,17,0,"
        TrackingSimulator.processUpdate(updateString)
        val shipment = TrackingSimulator.findShipment("17")
        assertNotNull(shipment)
        assertEquals("lost", shipment?.getStatus())
    }

    @Test
    fun testTrackingSimulatorProcessUpdateCanceled() {
        val createdString = "created,18,1678886400000"
        TrackingSimulator.processUpdate(createdString)
        val updateString = "canceled,18,0,"
        TrackingSimulator.processUpdate(updateString)
        val shipment = TrackingSimulator.findShipment("18")
        assertNotNull(shipment)
        assertEquals("canceled", shipment?.getStatus())
    }

    @Test
    fun testTrackingSimulatorProcessUpdateDelivered() {
        val createdString = "created,19,1678886400000"
        TrackingSimulator.processUpdate(createdString)
        val updateString = "delivered,19,1678999999999,"
        TrackingSimulator.processUpdate(updateString)
        val shipment = TrackingSimulator.findShipment("19")
        assertNotNull(shipment)
        assertEquals("delivered", shipment?.getStatus())
    }


    @Test
    fun testTrackingSimulatorProcessUpdateInvalidType() {
        val updateString = "invalidtype,20,1678886400000,extra"
        TrackingSimulator.processUpdate(updateString)
    }

    //covert timestamp to formatted date string
    private fun formatTime(time: Long?): String {
        return if (time != null) {
            val dateTime = Instant.ofEpochMilli(time).atZone(ZoneId.systemDefault()).toLocalDateTime()
            dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        } else {
            "N/A"
        }
    }
}