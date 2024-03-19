package com.example.demo;

import org.graalvm.nativeimage.hosted.Feature;
import org.graalvm.nativeimage.hosted.RuntimeForeignAccess;

import static com.example.demo.ManualFfi.PRINTF_FUNCTION_DESCRIPTOR;

public class DemoFeature implements Feature {

	@Override
	public void duringSetup(DuringSetupAccess access) {
		RuntimeForeignAccess.registerForDowncall(PRINTF_FUNCTION_DESCRIPTOR);
	}

}
