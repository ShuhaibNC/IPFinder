package com.shuhaib.ipf;

import android.app.Activity;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;
import android.widget.TextView;
import android.widget.Button;
import android.view.View;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.widget.Toast;

public class MainActivity extends Activity {

    public static TextView ipaddress;
    public static TextView warnt;
    public static TextView ifaceline;
    public static Button restart;
    public static Button copybtn;
    public static String currentIp;

    private View root;
    private TextView title;
    private TextView subtitle;
    private TextView hint;
    private TextView credit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        root = findViewById(R.id.root);
        title = findViewById(R.id.title);
        subtitle = findViewById(R.id.subtitle);
        hint = findViewById(R.id.hint);
        credit = findViewById(R.id.credit);
        ipaddress = findViewById(R.id.ipaddress);
        warnt = findViewById(R.id.warntext);
        ifaceline = findViewById(R.id.ifaceline);
        restart = findViewById(R.id.restart);
        copybtn = findViewById(R.id.copybtn);

        applyDynamicTheme();
        refreshIP();

        restart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refreshIP();
            }
        });

        View.OnClickListener copyListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                copyIp();
            }
        };
        copybtn.setOnClickListener(copyListener);
        ipaddress.setOnClickListener(copyListener);
    }

    public void copyIp() {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        String ip = currentIp != null ? currentIp : "127.0.0.1";
        ClipData clip = ClipData.newPlainText("ip", ip);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(this, "Copied " + ip, Toast.LENGTH_SHORT).show();
    }

    public void refreshIP() {        NetworkUtils.Result res = new NetworkUtils().scan();

        if (res.ip == null) {
            currentIp = "127.0.0.1";
            ipaddress.setText(currentIp);
            ifaceline.setText("");
            warnt.setText(R.string.no_uplink);
        } else {
            currentIp = res.ip;
            ipaddress.setText(currentIp);
            ifaceline.setText(res.iface);
            warnt.setText("");
        }
    }

    /**
     * Material Dynamic Theme: on Android 12+ the UI takes its colors from the
     * system's wallpaper-based palette (system_accent1_* / system_neutral1_*),
     * and light/dark follows the system theme via values-night. On older
     * Android versions it falls back to the static Material 3 baseline colors
     * in colors.xml.
     */
    private void applyDynamicTheme() {
        boolean night = (getResources().getConfiguration().uiMode
                & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES;

        int bg, onBg, secondary, primary, onPrimary, warn;
        if (night) {
            bg        = sysColor("system_neutral1_900", R.color.fallback_bg_night);
            onBg      = sysColor("system_neutral1_100", R.color.fallback_on_bg_night);
            secondary = sysColor("system_neutral1_400", R.color.fallback_secondary_night);
            primary   = sysColor("system_accent1_200",  R.color.fallback_primary_night);
            onPrimary = sysColor("system_neutral1_900", R.color.fallback_on_primary_night);
            warn      = colorOf(R.color.warn_night);
        } else {
            bg        = sysColor("system_neutral1_50",  R.color.fallback_bg);
            onBg      = sysColor("system_neutral1_900", R.color.fallback_on_bg);
            secondary = sysColor("system_neutral1_600", R.color.fallback_secondary);
            primary   = sysColor("system_accent1_600",  R.color.fallback_primary);
            onPrimary = colorOf(R.color.fallback_on_primary);
            warn      = colorOf(R.color.warn);
        }

        root.setBackgroundColor(bg);
        title.setTextColor(onBg);
        subtitle.setTextColor(secondary);
        ipaddress.setTextColor(primary);
        ifaceline.setTextColor(secondary);
        hint.setTextColor(secondary);
        warnt.setTextColor(warn);
        credit.setTextColor(secondary);

        copybtn.setBackgroundTintList(ColorStateList.valueOf(primary));
        copybtn.setTextColor(onPrimary);
        restart.setBackgroundTintList(ColorStateList.valueOf(primary));
        restart.setTextColor(onPrimary);
    }

    /** System dynamic color (Android 12+), or the given fallback color resource. */
    private int sysColor(String name, int fallbackRes) {
        if (Build.VERSION.SDK_INT >= 31) {
            int id = getResources().getIdentifier(name, "color", "android");
            if (id != 0) {
                if (Build.VERSION.SDK_INT >= 23) {
                    return getColor(id);
                }
                return getResources().getColor(id);
            }
        }
        return colorOf(fallbackRes);
    }

    private int colorOf(int resId) {
        if (Build.VERSION.SDK_INT >= 23) {
            return getColor(resId);
        }
        return getResources().getColor(resId);
    }
}

class NetworkUtils {

    static class Result {
        String ip;
        String iface;
    }

    private static final String[] TARGETS = {"wlan0", "ap0"};

    public Result scan() {
        Result res = new Result();
        try {
            List<NetworkInterface> interfaces =
                    Collections.list(NetworkInterface.getNetworkInterfaces());

            for (String target : TARGETS) {
                for (NetworkInterface ni : interfaces) {
                    if (ni.getName().equalsIgnoreCase(target)) {
                        List<InetAddress> addrs = Collections.list(ni.getInetAddresses());
                        for (InetAddress addr : addrs) {
                            if (addr instanceof Inet4Address) {
                                res.ip = addr.getHostAddress();
                                res.iface = target;
                                return res;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            return res;
        }
        return res;
    }
}
