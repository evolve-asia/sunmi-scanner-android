package com.evolve.sunmidemokotlin

import android.content.Context
import android.os.Build
import android.view.KeyEvent

class SupporterManager(context: Context, listener: IScanListener) {
    private var scannerManager: SunmiScannerManager? = null

    enum class ScannerSupporter {
        SUNMI
    }

    private val supporter: ScannerSupporter
        get() {
            for (supporter in ScannerSupporter.values()) {
                if (supporter.name == Build.MODEL) {
                    return supporter
                }
            }
            for (supporter in ScannerSupporter.values()) {
                if (supporter.name == Build.MANUFACTURER) {
                    return supporter
                }
            }
            return ScannerSupporter.SUNMI
        }

    init {
        val scannerSupporter = supporter
        scannerManager = SunmiScannerManager.getInstance(context)
        scannerManager?.setScannerListener(listener)
        scannerManager?.init()
    }

    fun recycle() {
        scannerManager?.recycle()
    }

    fun setScannerListener(listener: IScanListener) {
        scannerManager?.setScannerListener(listener)
    }

    fun sendKeyEvent(key: KeyEvent?) {
        scannerManager?.sendKeyEvent(key)
    }

    val scannerModel: Int?
        get() = scannerManager?.scannerModel

    fun scannerEnable(enable: Boolean?) {
        scannerManager?.scannerEnable(enable!!)
    }

    fun setScanMode(mode: String?) {
        scannerManager?.setScanMode(mode)
    }

    fun setDataTransferType(type: String?) {
        scannerManager?.setDataTransferType(type)
    }

    fun singleScan(bool: Boolean?) {
        scannerManager?.singleScan(bool!!)
    }

    fun continuousScan(bool: Boolean?) {
        scannerManager?.continuousScan(bool!!)
    }

    interface IScanListener {
        fun onScannerResultChange(result: String?)
        fun onScannerServiceConnected()
        fun onScannerServiceDisconnected()
        fun onScannerInitFail()
    }

    companion object {
        private val instance: SupporterManager? = null
    }
}