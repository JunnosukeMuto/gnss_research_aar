package org.example

import android.Manifest
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.os.ParcelUuid
import android.util.Log
import androidx.annotation.RequiresPermission
import com.unity3d.player.UnityPlayer

// Library for accessing the gnss_research GATT server using Bluetooth with Meta Quest 3 and Unity
object BleCentral
{
    private var gatt: BluetoothGatt? = null

    @RequiresPermission(
        allOf = [Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT]
    )
    @JvmStatic
    fun start()
    {
        val ctx = UnityPlayer.currentActivity
        val bm = ctx.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        val adapter = bm.adapter

        if (adapter == null)
        {
            Log.e("BLE", "Device doesn't support Bluetooth")
            return
        }

        // https://developer.android.com/develop/connectivity/bluetooth/ble/find-ble-devices
        // Scanning
        val scanner = adapter.bluetoothLeScanner
        val scanSettings = ScanSettings.Builder()
            .setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES)
            .setReportDelay(10000)
            .setScanMode(ScanSettings.SCAN_MODE_BALANCED)
            .build()
        val scanFilters = listOf(
            ScanFilter.Builder()
                .setServiceUuid(ParcelUuid(BleValues.SERVICE_UUID))
                .build(),
        )
        val scanCallback = BleScanCallback()
        scanner.startScan(scanFilters, scanSettings, scanCallback)

        // https://developer.android.com/develop/connectivity/bluetooth/ble/connect-gatt-server
        // Connect to GATT server
        val result = scanCallback.getResult()
        val gattCallback = BleGattCallback()

        if (result == null)
        {
            Log.e("BLE", "Scan result is null")
            return
        }

        this.gatt = result.device.connectGatt(ctx, false, gattCallback)
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    @JvmStatic
    fun stop()
    {
        this.gatt?.close()
        this.gatt = null
    }
}
