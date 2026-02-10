package org.example

import android.Manifest
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothProfile
import android.util.Base64
import androidx.annotation.RequiresPermission
import com.unity3d.player.UnityPlayer

class BleGattCallback(private val gameObjectName: String, private val onGnssData: String) : BluetoothGattCallback()
{

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int)
    {
        if (newState == BluetoothProfile.STATE_CONNECTED)
        {
            gatt?.discoverServices()
        }
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int)
    {
        val service = gatt?.getService(BleValues.SERVICE_UUID)
        val characteristic = service?.getCharacteristic(BleValues.CHARACTERISTIC_UUID)

        // https://developer.android.com/develop/connectivity/bluetooth/ble/transfer-ble-data#notification
        // Enable notify
        gatt?.setCharacteristicNotification(characteristic, true)

        val descriptor = characteristic?.getDescriptor(BleValues.CCCD_UUID)
        descriptor?.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
        gatt?.writeDescriptor(descriptor)
    }

    override fun onCharacteristicChanged(
        gatt: BluetoothGatt?,
        characteristic: BluetoothGattCharacteristic?
    )
    {
        val value = characteristic?.value
        val base64 = Base64.encodeToString(value, Base64.NO_WRAP)
        UnityPlayer.UnitySendMessage(gameObjectName, onGnssData, base64)
    }
}