package com.example.testapp

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.Toast
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat

class WhatsappaccessibilityService: AccessibilityService() {


    //the system calls back this method when it detects an AccessibilityEvent
    override fun onAccessibilityEvent(event: AccessibilityEvent) {

        // Retrieves the root node of the active window's view hierarchy.
        val rootInActiveWindow = rootInActiveWindow ?: return
        val rootCompat = AccessibilityNodeInfoCompat.wrap(rootInActiveWindow)

        //Searches for UI elements with the specified view ID.
        val sendMessageNodeInfoList = rootCompat.findAccessibilityNodeInfosByViewId("com.whatsapp:id/send")
        if (sendMessageNodeInfoList.isNullOrEmpty()) {
            return
        }

        val sendMessageButton = sendMessageNodeInfoList[0]
        if (!sendMessageButton.isVisibleToUser) {
            return
        }
        Thread.sleep(1000)
        sendMessageButton.performAction(AccessibilityNodeInfo.ACTION_CLICK)
        try {
            Thread.sleep(2000)
            performGlobalAction(GLOBAL_ACTION_BACK)
            Thread.sleep(500)
        } catch (ignored: InterruptedException) {
        }
        performGlobalAction(GLOBAL_ACTION_BACK)
        Thread.sleep(2000)
        Toast.makeText(this, "message sent!", Toast.LENGTH_SHORT).show()
    }

    //the system calls this method when the system wants to interrupt the feedback your service is providing
    override fun onInterrupt() {
        // Handle accessibility service interruption
    }
}