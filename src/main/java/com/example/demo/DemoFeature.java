package com.example.demo;

import org.graalvm.nativeimage.hosted.Feature;
import org.graalvm.nativeimage.hosted.RuntimeForeignAccess;

/**
 * registers the printf call for Graalvm native images
 */
public class DemoFeature implements Feature {

    @Override
    public void duringSetup(DuringSetupAccess access) {
        System.out.println("registering the PRINTF function for downcalls");
        RuntimeForeignAccess.registerForDowncall(DemoApplication.PRINTF_FUNCTION_DESCRIPTOR);
    }
}
