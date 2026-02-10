package org.example

import java.util.UUID

class BleValues
{
    companion object
    {
        val SERVICE_UUID: UUID = UUID.fromString("70787d4e-af36-4bfb-901b-37133b5191bb")
        val CHARACTERISTIC_UUID: UUID = UUID.fromString("0b53a515-bf15-44c8-a814-516de5f8a613")
        val CCCD_UUID: UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")
    }
}
