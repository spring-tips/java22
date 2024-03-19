package com.example.demo;

import org.graalvm.nativeimage.hosted.Feature;
import org.graalvm.nativeimage.hosted.RuntimeForeignAccess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.foreign.FunctionDescriptor;

import static java.lang.foreign.ValueLayout.*;

/**
 * registers the printf call for Graalvm native images
 */
public class DemoFeature implements Feature {

    private final Logger log = LoggerFactory.getLogger(getClass().getName());

    @Override
    public void duringSetup(DuringSetupAccess access) {
        log.info("registering the PRINTF function for downcalls during compilation.");
        RuntimeForeignAccess.registerForDowncall(
                FunctionDescriptor.of(JAVA_INT, JAVA_LONG,JAVA_LONG)
        );
    }
}
