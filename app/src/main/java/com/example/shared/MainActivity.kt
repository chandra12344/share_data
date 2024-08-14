package com.example.shared

import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbDeviceConnection
import android.hardware.usb.UsbManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.shared.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    private lateinit var usbManager: UsbManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMainBinding.inflate(layoutInflater)
        init()
        setContentView(binding.root)
    }


    private  fun init(){
        usbManager = getSystemService(USB_SERVICE) as UsbManager
        val deviceList = usbManager.deviceList
        for (device in deviceList.values) {
            Log.d("USBDevice", "Found device: ${device.deviceName}")
            binding.textView.text="Found device: ${usbManager}"
            val connection = usbManager.openDevice(device)
            if (connection != null) {

                communicateWithDevice(connection, device)
            } else {
                binding.textView.text="Could not open connection"
                Log.d("USBDevice", "Could not open connection")
            }
        }
        binding.textView.text="Could not open connection ${usbManager.accessoryList}"
    }

    private fun communicateWithDevice(connection: UsbDeviceConnection, device: UsbDevice) {
        val usbInterface = device.getInterface(0)
        val endpointIn = usbInterface.getEndpoint(0) // IN endpoint
        val endpointOut = usbInterface.getEndpoint(1) // OUT endpoint

        connection.claimInterface(usbInterface, true)

        // Send data to the device
        val dataToSend = "Hello, USB!".toByteArray()
        connection.bulkTransfer(endpointOut, dataToSend, dataToSend.size, 1000)

        // Read data from the device
        val buffer = ByteArray(1024)
        val receivedLength = connection.bulkTransfer(endpointIn, buffer, buffer.size, 1000)
        if (receivedLength > 0) {
            val receivedData = String(buffer, 0, receivedLength)
            Log.d("USBDevice", "Received data: $receivedData")
        } else {
            Log.d("USBDevice", "No data received")
        }

        connection.releaseInterface(usbInterface)
        connection.close()
    }
}