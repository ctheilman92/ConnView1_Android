package com.example.milkymac.connview_main;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import org.w3c.dom.Text;


/*TODO:
*
*       Change device details page to inflate fragment instead of standard activity.
* */
public class DeviceDetailsActivity extends AppCompatActivity {

    private TextView deviceDetailsTemp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_details);

        deviceDetailsTemp = (TextView) findViewById(R.id.tvDeviceDetails);
    }
}
