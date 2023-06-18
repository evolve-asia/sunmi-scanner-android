package com.evolve.sunmidemokotlin

import android.app.Service
import android.content.*
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.RemoteException
import android.util.Log
import android.view.KeyEvent
import com.evolve.sunmidemokotlin.SupporterManager.IScanListener

class SunmiScannerManager private constructor(private val activity: Context) : IScannerManager {
    private val handler = Handler(Looper.getMainLooper())
    private var serviceIntent: Intent? = null
    private var listener: IScanListener? = null
    private var singleScanFlag = false
    private val conn: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(componentName: ComponentName, service: IBinder) {
            if (listener != null) {
                listener!!.onScannerServiceConnected()
            } else {
                listener?.onScannerInitFail()
            }
            scanInterface = IScanInterface.Stub.asInterface(service)
        }

        override fun onServiceDisconnected(componentName: ComponentName) {
            if (listener != null) {
                listener?.onScannerServiceDisconnected()
            }
            scanInterface = null
        }
    }
    private val receiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            handler.post {
                val code = intent.getStringExtra(DATA)
                if (code != null && !code.isEmpty()) {
                    if (listener != null) {
                        listener?.onScannerResultChange(code)
                    }
                    if (singleScanFlag) {
                        singleScanFlag = false
                        try {
                            scanInterface?.stop()
                        } catch (e: RemoteException) {
                            e.printStackTrace()
                        }
                    }
                }
            }
        }
    }

    override fun init() {
        bindService()
        registerReceiver()
    }

    override fun recycle() {
        activity.stopService(serviceIntent)
        activity.unregisterReceiver(receiver)
        listener = null
    }

    override fun setScannerListener(listener: IScanListener) {
        this.listener = listener
    }

    override fun sendKeyEvent(key: KeyEvent?) {
        try {
            scanInterface?.sendKeyEvent(key)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override val scannerModel: Int?
        get() {
            try {
                return scanInterface?.scannerModel
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return -1
        }

    override fun scannerEnable(enable: Boolean) {
        Log.d("pda", "This device does not support the method!")
    }

    override fun setScanMode(mode: String?) {}
    override fun setDataTransferType(type: String?) {}
    override fun singleScan(bool: Boolean) {
        try {
            if (bool) {
                scanInterface?.scan()
                singleScanFlag = true
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @Synchronized
    override fun continuousScan(bool: Boolean) {
        try {
            if (bool) {
                scanInterface?.scan()
            } else {
                scanInterface?.stop()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun bindService() {
        serviceIntent = Intent()
        serviceIntent?.setPackage("com.sunmi.scanner")
        serviceIntent?.action = "com.sunmi.scanner.IScanInterface"
        activity.bindService(serviceIntent, conn, Service.BIND_AUTO_CREATE)
    }

    private fun registerReceiver() {
        val intentFilter = IntentFilter()
        intentFilter.addAction(ACTION_DATA_CODE_RECEIVED)
        activity.registerReceiver(receiver, intentFilter)
    }

    companion object {
        const val ACTION_DATA_CODE_RECEIVED = "com.sunmi.scanner.ACTION_DATA_CODE_RECEIVED"
        private const val DATA = "data"
        private var scanInterface: IScanInterface? = null
        private var instance: SunmiScannerManager? = null
        fun getInstance(context: Context): SunmiScannerManager? {
            if (instance == null) {
                synchronized(SunmiScannerManager::class.java) {
                    if (instance == null) {
                        instance = SunmiScannerManager(context)
                    }
                }
            }
            return instance
        }
    }
}