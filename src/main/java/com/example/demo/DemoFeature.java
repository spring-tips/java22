package com.example.demo;

import org.graalvm.nativeimage.hosted.Feature;
import org.graalvm.nativeimage.hosted.RuntimeForeignAccess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * registers the printf call for Graalvm native images
 */
public class DemoFeature implements Feature {

    private final Logger log = LoggerFactory.getLogger(getClass().getName());

    @Override
    public void duringSetup(DuringSetupAccess access) {
        log.info("registering the PRINTF function for downcalls during compilation.");
        RuntimeForeignAccess.registerForDowncall(DemoApplication.PRINTF_FUNCTION_DESCRIPTOR);
    }
}
