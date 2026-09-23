package com.shuhaib.ipf;

import android.app.Activity;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ipaddress = findViewById(R.id.ipaddress);
        warnt = findViewById(R.id.warntext);
        ifaceline = findViewById(R.id.ifaceline);
        restart = findViewById(R.id.restart);
        copybtn = findViewById(R.id.copybtn);

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

    public void refreshIP() {
        NetworkUtils.Result res = new NetworkUtils().scan();

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
