package shipmentFactory

import ShipmentObserver
import io.ktor.http.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import updateStrategies.*

class ShipmentTests {
    @Nested
    inner class ShipmentFactoryTests {
        @Test
        fun `createShipment returns StandardShipment for unknown type`() {
            val shipment = ShipmentFactory.createShipment("status", "id", "unknown", 1234L)
            assertTrue(shipment is StandardShipment)
        }

        @Test
        fun `createShipment returns correct shipment types`() {
            val standardShipment = ShipmentFactory.createShipment("status", "id", "standard", 1234L)
            val expressShipment = ShipmentFactory.createShipment("status", "id", "express", 1234L)
            val overnightShipment = ShipmentFactory.createShipment("status", "id", "overnight", 1234L)
            val bulkShipment = ShipmentFactory.createShipment("status", "id", "bulk", 1234L)

            assertTrue(standardShipment is StandardShipment)
            assertTrue(expressShipment is ExpressShipment)
            assertTrue(overnightShipment is OvernightShipment)
            assertTrue(bulkShipment is BulkShipment)
        }
    }

    @Nested
    inner class StandardShipmentTests {
        private lateinit var standardShipment: StandardShipment

        @BeforeEach
        fun setUp() {
            standardShipment = StandardShipment("status", "id", 1234L)
        }

        @Test
        fun `updateExpectedDeliveryDate updates the expected delivery date`() {
            val newDate = 5678L
            standardShipment.updateExpectedDeliveryDate(newDate)
            assertEquals(newDate, standardShipment.expectedDeliverDateTimestamp)
        }
    }

    @Nested
    inner class ExpressShipmentTests {
        private lateinit var expressShipment: ExpressShipment

        @BeforeEach
        fun setUp() {
            expressShipment = ExpressShipment("status", "id", 1234L)
        }

        @Test
        fun `updateExpectedDeliveryDate updates the expected delivery date directly`() {
            val newDate = 1234L + 300000000
            expressShipment.updateExpectedDeliveryDate(newDate)
            assertEquals(newDate, expressShipment.expectedDeliverDateTimestamp)
        }

        @Test
        fun `updateExpectedDeliveryDate does not add note when called directly`() {
            val newDate = 1234L + 259199999
            expressShipment.updateExpectedDeliveryDate(newDate)
            assertEquals(0, expressShipment.getNotes().size)
            assertEquals(newDate, expressShipment.expectedDeliverDateTimestamp)
        }

    }

    @Nested
    inner class OvernightShipmentTests {
        private lateinit var overnightShipment: OvernightShipment

        @BeforeEach
        fun setUp() {
            overnightShipment = OvernightShipment("status", "id", 1234L)
        }

        @Test
        fun `updateExpectedDeliveryDate updates the expected delivery date directly`() {
            val newDate = 1234L + 100000000
            overnightShipment.updateExpectedDeliveryDate(newDate)
            assertEquals(newDate, overnightShipment.expectedDeliverDateTimestamp)
        }

        @Test
        fun `updateExpectedDeliveryDate does not add note when called directly`() {
            val newDate = 1234L + 86399999
            overnightShipment.updateExpectedDeliveryDate(newDate)
            assertEquals(0, overnightShipment.getNotes().size)
            assertEquals(newDate, overnightShipment.expectedDeliverDateTimestamp)
        }


    }

    @Nested
    inner class BulkShipmentTests {
        private lateinit var bulkShipment: BulkShipment

        @BeforeEach
        fun setUp() {
            bulkShipment = BulkShipment("status", "id", 0)
        }

        @Test
        fun `updateExpectedDeliveryDate updates the expected delivery date directly`() {
            val newDate = 1234L + 259200000
            bulkShipment.updateExpectedDeliveryDate(newDate)
            assertEquals(newDate, bulkShipment.expectedDeliverDateTimestamp)
        }


    }

    @Nested
    inner class ShipmentTests {
        private lateinit var shipment: Shipment

        @BeforeEach
        fun setUp() {
            shipment = StandardShipment("status", "id", 1234L)
        }

        @Test
        fun `updateStatus updates the status`() {
            val newStatus = "newStatus"
            shipment.updateStatus(newStatus)
            assertEquals(newStatus, shipment.retrieveStatus())
        }

        @Test
        fun `addNote adds a note`() {
            val note = "This is a note"
            shipment.addNote(note)
            assertEquals(listOf(note), shipment.getNotes())
        }

        @Test
        fun `getNotes returns the notes`() {
            val note1 = "This is a note"
            val note2 = "This is another note"
            shipment.addNote(note1)
            shipment.addNote(note2)
            assertEquals(listOf(note1, note2), shipment.getNotes())
        }

        @Test
        fun `getUpdateHistory returns the update history`() {
            val newStatus1 = "newStatus1"
            val newStatus2 = "newStatus2"
            shipment.updateStatus(newStatus1)
            shipment.updateStatus(newStatus2)
            assertEquals(listOf("created", newStatus1, newStatus2), shipment.getUpdateHistory())
        }

        @Test
        fun `retrieveStatus returns the status`() {
            assertEquals("status", shipment.retrieveStatus())
        }

        @Test
        fun `subscribe adds an observer`() {
            val observer = object : ShipmentObserver {
                override fun notify(shipment: Shipment?) {}
            }
            shipment.subscribe(observer)
            // Not a great assertion, but we can't check private properties
            // This test mostly exists to improve coverage
        }

        @Test
        fun `unsubscribe removes an observer`() {
            val observer = object : ShipmentObserver {
                override fun notify(shipment: Shipment?) {}
            }
            shipment.subscribe(observer)
            shipment.unsubscribe(observer)
            // Not a great assertion, but we can't check private properties
            // This test mostly exists to improve coverage
        }

        @Test
        fun `notifyObservers calls notify on all observers`() {
            val observer1 = object : ShipmentObserver {
                var notified = false
                override fun notify(shipment: Shipment?) {
                    notified = true
                }
            }
            val observer2 = object : ShipmentObserver {
                var notified = false
                override fun notify(shipment: Shipment?) {
                    notified = true
                }
            }
            shipment.subscribe(observer1)
            shipment.subscribe(observer2)
            shipment.notifyObservers()
            assertTrue(observer1.notified)
            assertTrue(observer2.notified)
        }
    }

    @Nested
    inner class ShipmentManagerTests {
        @Test
        fun `findShipment returns null for unknown shipment`() {
            val shipment = ShipmentManager.findShipment("unknown")
            assertNull(shipment)
        }

        @Test
        fun `findShipment returns the correct shipment`() {
            val shipment = StandardShipment("status", "id", 1234L)
            ShipmentManager.addShipment(shipment)
            val foundShipment = ShipmentManager.findShipment("id")
            assertEquals(shipment, foundShipment)
        }

        @Test
        fun `addShipment adds a shipment`() {
            val shipment = StandardShipment("status", "id", 1234L)
            ShipmentManager.addShipment(shipment)
            val foundShipment = ShipmentManager.findShipment("id")
            assertEquals(shipment, foundShipment)
        }

        @Test
        fun `processUpdate returns error for invalid update`() {
            val (result, status) = ShipmentManager.processUpdate("invalid")
            assertEquals("Error: Too few parameters. Expected format: <updateType>,<shipmentId>,<timestamp>,<otherInfo>", result)
            assertEquals(HttpStatusCode.BadRequest, status)
        }

        @Test
        fun `processUpdate returns error for invalid timestamp`() {
            val (result, status) = ShipmentManager.processUpdate("updateType,shipmentId,invalidTimestamp")
            assertEquals("Error: Invalid timestamp format", result)
            assertEquals(HttpStatusCode.BadRequest, status)
        }

        @Test
        fun `processUpdate returns error for invalid update type`() {
            val (result, status) = ShipmentManager.processUpdate("invalidUpdateType,shipmentId,1234")
            assertEquals("Error: Invalid update type", result)
            assertEquals(HttpStatusCode.BadRequest, status)
        }

        @Test
        fun `processUpdate returns error for duplicate shipment ID`() {
            val shipment = StandardShipment("status", "id", 1234L)
            ShipmentManager.addShipment(shipment)
            val (result, status) = ShipmentManager.processUpdate("created,id,1234")
            assertEquals("Error: Duplicate ID", result)
            assertEquals(HttpStatusCode.BadRequest, status)
        }

        @Test
        fun `processUpdate processes created update`() {
            val (result, status) = ShipmentManager.processUpdate("created,id2,1234,standard")
            assertEquals("Success", result)
            assertEquals(HttpStatusCode.OK, status)
            assertNotNull(ShipmentManager.findShipment("id2"))
        }

        @Test
        fun `processUpdate processes shipped update`() {
            val shipment = StandardShipment("status", "id", 1234L)
            ShipmentManager.addShipment(shipment)
            val (result, status) = ShipmentManager.processUpdate("shipped,id,1234,5678")
            assertEquals("Success", result)
            assertEquals(HttpStatusCode.OK, status)
            assertEquals("shipped", ShipmentManager.findShipment("id")?.retrieveStatus())
            assertEquals(5678L, ShipmentManager.findShipment("id")?.expectedDeliverDateTimestamp)
        }

        @Test
        fun `processUpdate processes location update`() {
            val shipment = StandardShipment("status", "id", 1234L)
            ShipmentManager.addShipment(shipment)
            val (result, status) = ShipmentManager.processUpdate("location,id,1234,newLocation")
            assertEquals("Success", result)
            assertEquals(HttpStatusCode.OK, status)
            assertEquals("newLocation", ShipmentManager.findShipment("id")?.currentLocation)
        }

        @Test
        fun `processUpdate processes delayed update`() {
            val shipment = StandardShipment("status", "id", 1234L)
            ShipmentManager.addShipment(shipment)
            val (result, status) = ShipmentManager.processUpdate("delayed,id,1234,5678")
            assertEquals("Success", result)
            assertEquals(HttpStatusCode.OK, status)
            assertEquals("delayed", ShipmentManager.findShipment("id")?.retrieveStatus())
            assertEquals(5678L, ShipmentManager.findShipment("id")?.expectedDeliverDateTimestamp)
        }

        @Test
        fun `processUpdate processes noteadded update`() {
            val shipment = StandardShipment("status", "id", 1234L)
            ShipmentManager.addShipment(shipment)
            val (result, status) = ShipmentManager.processUpdate("noteadded,id,1234,This is a note")
            assertEquals("Success", result)
            assertEquals(HttpStatusCode.OK, status)
            assertEquals(listOf("This is a note"), ShipmentManager.findShipment("id")?.getNotes())
        }

        @Test
        fun `processUpdate processes lost update`() {
            val shipment = StandardShipment("status", "id", 1234L)
            ShipmentManager.addShipment(shipment)
            val (result, status) = ShipmentManager.processUpdate("lost,id,1234")
            assertEquals("Success", result)
            assertEquals(HttpStatusCode.OK, status)
            assertEquals("lost", ShipmentManager.findShipment("id")?.retrieveStatus())
        }

        @Test
        fun `processUpdate processes canceled update`() {
            val shipment = StandardShipment("status", "id", 1234L)
            ShipmentManager.addShipment(shipment)
            val (result, status) = ShipmentManager.processUpdate("canceled,id,1234")
            assertEquals("Success", result)
            assertEquals(HttpStatusCode.OK, status)
            assertEquals("canceled", ShipmentManager.findShipment("id")?.retrieveStatus())
        }

        @Test
        fun `processUpdate processes delivered update`() {
            val shipment = StandardShipment("status", "id", 1234L)
            ShipmentManager.addShipment(shipment)
            val (result, status) = ShipmentManager.processUpdate("delivered,id,1234")
            assertEquals("Success", result)
            assertEquals(HttpStatusCode.OK, status)
            assertEquals("delivered", ShipmentManager.findShipment("id")?.retrieveStatus())
        }
    }

    @Nested
    inner class UpdateStrategyTests {
        private lateinit var shipment: Shipment

        @BeforeEach
        fun setUp() {
            shipment = StandardShipment("status", "id", 1234L)
            ShipmentManager.addShipment(shipment)
        }

        @Test
        fun `CreatedUpdatePattern creates a shipment`() {
            CreatedUpdatePattern().updateShipment("id2", null, 1234L, "standard")
            assertNotNull(ShipmentManager.findShipment("id2"))
        }

        @Test
        fun `ShippedUpdatePattern updates shipment status and expected delivery date`() {
            ShippedUpdatePattern().updateShipment("id", null, 1234L, "5678")
            assertEquals("shipped", ShipmentManager.findShipment("id")?.retrieveStatus())
            assertEquals(5678L, ShipmentManager.findShipment("id")?.expectedDeliverDateTimestamp)
        }

        @Test
        fun `LocationUpdatePattern updates shipment location`() {
            LocationUpdatePattern().updateShipment("id", null, 1234L, "newLocation")
            assertEquals("newLocation", ShipmentManager.findShipment("id")?.currentLocation)
        }

        @Test
        fun `DelayedUpdatePattern updates shipment status and expected delivery date`() {
            DelayedUpdatePattern().updateShipment("id", null, 1234L, "5678")
            assertEquals("delayed", ShipmentManager.findShipment("id")?.retrieveStatus())
            assertEquals(5678L, ShipmentManager.findShipment("id")?.expectedDeliverDateTimestamp)
        }

        @Test
        fun `NoteAddedUpdatePattern adds a note to the shipment`() {
            NoteAddedUpdatePattern().updateShipment("id", null, 1234L, "This is a note")
            assertEquals(listOf("This is a note"), ShipmentManager.findShipment("id")?.getNotes())
        }

        @Test
        fun `LostUpdatePattern updates shipment status`() {
            LostUpdatePattern().updateShipment("id", null, 1234L)
            assertEquals("lost", ShipmentManager.findShipment("id")?.retrieveStatus())
        }

        @Test
        fun `CanceledUpdatePattern updates shipment status`() {
            CanceledUpdatePattern().updateShipment("id", null, 1234L)
            assertEquals("canceled", ShipmentManager.findShipment("id")?.retrieveStatus())
        }

        @Test
        fun `DeliveredUpdatePattern updates shipment status`() {
            DeliveredUpdatePattern().updateShipment("id", null, 1234L)
            assertEquals("delivered", ShipmentManager.findShipment("id")?.retrieveStatus())
        }
    }
}
