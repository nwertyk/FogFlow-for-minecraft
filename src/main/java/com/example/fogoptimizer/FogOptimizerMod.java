/*
 * MIT License
 *
 * Copyright (c) 2026 Manus AI
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.example.fogoptimizer;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.MinecraftClient;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.fogoptimizer.mixin.MinecraftClientAccessor;

public class FogOptimizerMod implements ClientModInitializer {
    public static final String MOD_ID = "fogoptimizer";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private static float currentFogMultiplier = 1.0f;
    private static float targetFogMultiplier = 1.0f;
    private static final float LERP_SPEED = 0.05f;
    
    // Configurable values (could be moved to a config class)
    public static int targetFps = 60;
    public static float minFogMultiplier = 0.2f;
    public static float maxFogMultiplier = 1.0f;

    @Override
    public void onInitializeClient() {
        LOGGER.info("Fog Dynamic Optimizer initialized!");

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.world == null || client.isPaused()) return;

            int currentFps = ((MinecraftClientAccessor)client).getFps();
            
            if (currentFps < targetFps) {
                // Decrease fog distance (pull fog closer)
                targetFogMultiplier = Math.max(minFogMultiplier, targetFogMultiplier - 0.01f);
            } else if (currentFps > targetFps + 10) {
                // Increase fog distance back to normal
                targetFogMultiplier = Math.min(maxFogMultiplier, targetFogMultiplier + 0.01f);
            }

            // Smooth transition
            currentFogMultiplier += (targetFogMultiplier - currentFogMultiplier) * LERP_SPEED;
        });
    }

    public static float getFogMultiplier() {
        return currentFogMultiplier;
    }
}
