package com.razorpay.razorpay_flutter;

import android.util.Log;

import androidx.annotation.NonNull;

import java.util.Map;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;

/**
 * RazorpayFlutterPlugin - Updated to support Flutter V2 embedding.
 */
public class RazorpayFlutterPlugin implements FlutterPlugin, MethodCallHandler, ActivityAware {

    private RazorpayDelegate razorpayDelegate;
    private ActivityPluginBinding pluginBinding;
    private MethodChannel channel;
    private static final String CHANNEL_NAME = "razorpay_flutter";

    public RazorpayFlutterPlugin() {
    }

    @Override
    public void onAttachedToEngine(@NonNull FlutterPluginBinding binding) {
        channel = new MethodChannel(binding.getBinaryMessenger(), CHANNEL_NAME);
        channel.setMethodCallHandler(this);
    }

    @Override
    public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
        if (channel != null) {
            channel.setMethodCallHandler(null);
            channel = null;
        }
    }

    @Override
    public void onMethodCall(MethodCall call, Result result) {
        if (razorpayDelegate == null) {
            result.error("UNAVAILABLE", "Razorpay plugin is not initialized", null);
            return;
        }

        switch (call.method) {
            case "open":
                razorpayDelegate.openCheckout((Map<String, Object>) call.arguments, result);
                break;
            case "resync":
                razorpayDelegate.resync(result);
                break;
            default:
                result.notImplemented();
        }
    }

    @Override
    public void onAttachedToActivity(@NonNull ActivityPluginBinding binding) {
        razorpayDelegate = new RazorpayDelegate(binding.getActivity());
        this.pluginBinding = binding;
        razorpayDelegate.setPackageName(binding.getActivity().getPackageName());
        binding.addActivityResultListener(razorpayDelegate);
    }

    @Override
    public void onDetachedFromActivityForConfigChanges() {
        onDetachedFromActivity();
    }

    @Override
    public void onReattachedToActivityForConfigChanges(@NonNull ActivityPluginBinding binding) {
        onAttachedToActivity(binding);
    }

    @Override
    public void onDetachedFromActivity() {
        if (pluginBinding != null && razorpayDelegate != null) {
            pluginBinding.removeActivityResultListener(razorpayDelegate);
        }
        pluginBinding = null;
        razorpayDelegate = null;
    }
}
