package org.example

import android.Manifest
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.content.pm.PackageManager
import android.os.Handler
import android.os.Looper
import android.os.ParcelUuid
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.core.app.ActivityCompat
import com.unity3d.player.UnityPlayer

// Library for accessing the gnss_research GATT server using Bluetooth with Meta Quest 3 and Unity
object BleCentral
{
    private var gatt: BluetoothGatt? = null

    @RequiresPermission(Manifest.permission.BLUETOOTH_SCAN)
    @JvmStatic
    fun start(gameObjectName: String, onConnect: String, onGnssData: String)
    {
        val ctx = UnityPlayer.currentActivity
        val bm = ctx.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        val adapter = bm.adapter

        if (adapter == null)
        {
            UnityPlayer.UnitySendMessage(gameObjectName, onConnect, "error: device doesn't support bluetooth")
            return
        }

        // https://developer.android.com/develop/connectivity/bluetooth/ble/find-ble-devices
        // Scanning
        val scanner = adapter.bluetoothLeScanner
        val scanSettings = ScanSettings.Builder()
            .setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES)
            .setReportDelay(2000)
            .setScanMode(ScanSettings.SCAN_MODE_BALANCED)
            .build()
        val scanFilters = listOf(
            ScanFilter.Builder()
                .setServiceUuid(ParcelUuid(BleValues.SERVICE_UUID))
                .build(),
        )
        val scanCallback = BleScanCallback(
            scanner,
            ctx,
            gameObjectName,
            onConnect,
            onGnssData,
            setGatt = { gatt -> this.gatt = gatt }
        )

        scanner.startScan(scanFilters, scanSettings, scanCallback)

        // timeout
        Handler(Looper.getMainLooper()).postDelayed({
            if (this.gatt == null)
            {
                scanner.stopScan(scanCallback)
                UnityPlayer.UnitySendMessage(gameObjectName, onConnect, "error: timeout")
            }
        }, 10000)
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    @JvmStatic
    fun stop(gameObjectName: String, onDisconnect: String)
    {
        this.gatt?.close()
        this.gatt = null
        UnityPlayer.UnitySendMessage(gameObjectName, onDisconnect, "true")
    }

    @JvmStatic
    fun request(gameObjectName: String, onRequest: String)
    {
        val ctx = UnityPlayer.currentActivity

        ActivityCompat.requestPermissions(
            ctx,
            arrayOf(
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT
            ),
            1
        )

        if (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED)
        {
            UnityPlayer.UnitySendMessage(gameObjectName, onRequest, "error: BLUETOOTH_SCAN")
            return
        }
        if (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED)
        {
            UnityPlayer.UnitySendMessage(gameObjectName, onRequest, "error: BLUETOOTH_CONNECT")
            return
        }

        UnityPlayer.UnitySendMessage(gameObjectName, onRequest, "true")
    }
}
