package com.example.demo;

import org.graalvm.nativeimage.hosted.Feature;
import org.graalvm.nativeimage.hosted.RuntimeForeignAccess;

import java.lang.foreign.FunctionDescriptor;
import java.lang.foreign.Linker;

import static com.example.demo.DemoApplication.PRINTF_FUNCTION_DESCRIPTOR;
import static java.lang.foreign.ValueLayout.*;

/**
 * registers the printf call for Graalvm native images
 */
public class DemoFeature implements Feature {

    @Override
    public void duringSetup(DuringSetupAccess access) {
        System.out.println("registering the PRINTF function for downcalls during compilation.");
        RuntimeForeignAccess.registerForDowncall(PRINTF_FUNCTION_DESCRIPTOR);
        System.out.println("registered PRINTF functions");

    }
}
