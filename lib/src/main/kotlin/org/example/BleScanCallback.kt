package org.example

import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.util.Log

class BleScanCallback : ScanCallback()
{
    private var result: ScanResult? = null
    override fun onScanResult(callbackType: Int, result: ScanResult?)
    {
        this.result = result
    }

    override fun onScanFailed(errorCode: Int)
    {
        Log.e("BLE", "Scan failed: $errorCode")
    }

    fun getResult(): ScanResult?
    {
        return this.result
    }
}