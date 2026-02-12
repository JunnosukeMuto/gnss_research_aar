package org.example

import android.Manifest
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.util.Log
import androidx.annotation.RequiresPermission
import com.unity3d.player.UnityPlayer

class BleScanCallback(
    private val scanner: BluetoothLeScanner,
    private val ctx: Context,
    private val gameObjectName: String,
    private val onConnect: String,
    private val onGnssData: String,
    private val onInfo: String,
    private val setGatt: (BluetoothGatt) -> Unit
) : ScanCallback()
{
    @RequiresPermission(allOf = [Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT])
    override fun onScanResult(callbackType: Int, result: ScanResult?)
    {
        scanner.stopScan(this)

        if (result == null)
        {
            UnityPlayer.UnitySendMessage(gameObjectName, onConnect, "error: result is null")
            return
        }

        // https://developer.android.com/develop/connectivity/bluetooth/ble/connect-gatt-server
        // Connect to GATT server
        val gattCallback = BleGattCallback(gameObjectName, onGnssData, onInfo)
        val gatt = result.device.connectGatt(ctx, false, gattCallback, BluetoothDevice.TRANSPORT_LE)
        UnityPlayer.UnitySendMessage(gameObjectName, onConnect, "true")

        setGatt(gatt)
    }

    override fun onScanFailed(errorCode: Int)
    {
        UnityPlayer.UnitySendMessage(gameObjectName, onConnect, "error: scan failed: $errorCode")
    }
}