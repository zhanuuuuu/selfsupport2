package com.hlyf.selfsupport.utilPrint;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;

import com.hlyf.selfsupport.usbsdk.UsbDriver;

public class UtilsPrint {


    private Context context;
    private UsbDriver mUsbDriver;
    private UsbDevice mUsbDev1;		//打印机1
    private UsbDevice mUsbDev2;		//打印机2
    private UsbDevice mUsbDev;

    private final static int PID11 = 8211;
    private final static int PID13 = 8213;
    private final static int PID15 = 8215;
    private final static int VENDORID = 1305;

    private static final String ACTION_USB_PERMISSION =  "com.usb.sample.USB_PERMISSION";
    private UsbManager mUsbManager;
    private UsbDevice m_Device;

    private UsbReceiver mUsbReceiver;

    private static UtilsPrint instance;

    public UtilsPrint(Context context) {
        this.context = context;
    }

    public static UtilsPrint getInstance(Context context) {
        if (instance == null) {
            instance = new UtilsPrint(context);
        }
        return instance;
    }

    public UsbDriver getmUsbDriver() {
        return mUsbDriver;
    }

    public void setmUsbDriver(UsbDriver mUsbDriver) {
        this.mUsbDriver = mUsbDriver;
    }

    public UsbDevice getmUsbDev1() {
        return mUsbDev1;
    }

    public void setmUsbDev1(UsbDevice mUsbDev1) {
        this.mUsbDev1 = mUsbDev1;
    }

    public UsbDevice getmUsbDev2() {
        return mUsbDev2;
    }

    public void setmUsbDev2(UsbDevice mUsbDev2) {
        this.mUsbDev2 = mUsbDev2;
    }

    public UsbDevice getmUsbDev() {
        return mUsbDev;
    }

    public void setmUsbDev(UsbDevice mUsbDev) {
        this.mUsbDev = mUsbDev;
    }

    public UsbManager getmUsbManager() {
        return mUsbManager;
    }

    public void setmUsbManager(UsbManager mUsbManager) {
        this.mUsbManager = mUsbManager;
    }

    public UsbDevice getM_Device() {
        return m_Device;
    }

    public void setM_Device(UsbDevice m_Device) {
        this.m_Device = m_Device;
    }

    public UsbReceiver getmUsbReceiver() {
        return mUsbReceiver;
    }

    public void setmUsbReceiver(UsbReceiver mUsbReceiver) {
        this.mUsbReceiver = mUsbReceiver;
    }

    public void getUsbDriverService(){
        mUsbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
        mUsbDriver = new UsbDriver(mUsbManager, context);
        PendingIntent permissionIntent1 = PendingIntent.getBroadcast(context, 0,
                new Intent(ACTION_USB_PERMISSION), 0);
        mUsbDriver.setPermissionIntent(permissionIntent1);
        // Broadcast listen for new devices

        mUsbReceiver = new UsbReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_USB_PERMISSION);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        context.registerReceiver(mUsbReceiver, filter);
    }

    // Get UsbDriver(UsbManager) service
    public boolean printConnStatus() {
        boolean blnRtn = false;
        try {
            if (!mUsbDriver.isConnected()) {
                // USB线已经连接
                for (UsbDevice device : mUsbManager.getDeviceList().values()) {
                    if ((device.getProductId() == PID11 && device.getVendorId() == VENDORID)
                            || (device.getProductId() == PID13 && device.getVendorId() == VENDORID)
                            || (device.getProductId() == PID15 && device.getVendorId() == VENDORID)) {
//						if (!mUsbManager.hasPermission(device)) {
//							break;
//						}
                        blnRtn = mUsbDriver.usbAttached(device);
                        if (blnRtn == false) {
                            break;
                        }
                        blnRtn = mUsbDriver.openUsbDevice(device);

                        // 打开设备
                        if (blnRtn) {
                            if (device.getProductId() == PID11) {
                                mUsbDev1 = device;
                                mUsbDev = mUsbDev1;
                            } else {
                                mUsbDev2 = device;
                                mUsbDev = mUsbDev2;
                            }
                            //T.showShort(context, "驱动连接成功");
                            break;
                        } else {
                            T.showShort(context, "驱动连接失败");
                            break;
                        }
                    }
                }
            } else {
                blnRtn = true;
            }
        } catch (Exception e) {
            T.showShort(context, e.getMessage());
        }
        return blnRtn;
    }

    /*
     *  BroadcastReceiver when insert/remove the device USB plug into/from a USB port
     *  创建一个广播接收器接收USB插拔信息：当插入USB插头插到一个USB端口，或从一个USB端口，移除装置的USB插头
     */
    class UsbReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
                if(mUsbDriver.usbAttached(intent))
                {
                    UsbDevice device = (UsbDevice) intent
                            .getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if ((device.getProductId() == PID11 && device.getVendorId() == VENDORID)
                            || (device.getProductId() == PID13 && device.getVendorId() == VENDORID)
                            || (device.getProductId() == PID15 && device.getVendorId() == VENDORID))
                    {
                        if(mUsbDriver.openUsbDevice(device))
                        {
                            if(device.getProductId()==PID11){
                                mUsbDev1 = device;
                                mUsbDev = mUsbDev1;
                            } else {
                                mUsbDev2 = device;
                                mUsbDev = mUsbDev2;
                            }
                        }
                    }
                }
            } else if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
                UsbDevice device = (UsbDevice) intent
                        .getParcelableExtra(UsbManager.EXTRA_DEVICE);
                if ((device.getProductId() == PID11 && device.getVendorId() == VENDORID)
                        || (device.getProductId() == PID13 && device.getVendorId() == VENDORID)
                        || (device.getProductId() == PID15 && device.getVendorId() == VENDORID))
                {
                    mUsbDriver.closeUsbDevice(device);
                    if(device.getProductId()==PID11)
                        mUsbDev1 = null;
                    else
                        mUsbDev2 = null;
                }
            } else if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this)
                {
                    UsbDevice device = (UsbDevice)intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false))
                    {
                        if ((device.getProductId() == PID11 && device.getVendorId() == VENDORID)
                                || (device.getProductId() == PID13 && device.getVendorId() == VENDORID)
                                || (device.getProductId() == PID15 && device.getVendorId() == VENDORID))
                        {
                            if (mUsbDriver.openUsbDevice(device)) {
                                if (device.getProductId() == PID11) {
                                    mUsbDev1 = device;
                                    mUsbDev = mUsbDev1;
                                } else {
                                    mUsbDev2 = device;
                                    mUsbDev = mUsbDev2;
                                }
                            }
                        }
                    }
                    else {
                        T.showShort(context, "permission denied for device");
                        //Log.d(TAG, "permission denied for device " + device);
                    }
                }
            }
        }
    };
}
