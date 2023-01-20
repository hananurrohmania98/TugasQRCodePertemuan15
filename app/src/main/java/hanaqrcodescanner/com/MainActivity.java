package hanaqrcodescanner.com;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.SyncStateContract;
import android.telecom.Call;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.util.AndroidException;
import android.util.Patterns;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.Result;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.invoke.ConstantCallSite;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    //view object
    private Button buttonScan;
    private TextView textViewName, textViewClass, textViewId;
    //qr object
    private IntentIntegrator qrScan;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //View objects
        buttonScan = (Button) findViewById(R.id.buttonScan);
        textViewName = (TextView) findViewById(R.id.textViewNama);
        textViewClass = (TextView) findViewById(R.id.textViewKelas);
        textViewId = (TextView) findViewById(R.id.textViewNim);
        //scan obj
        qrScan = new IntentIntegrator(this);
        //imp onclick list
        buttonScan.setOnClickListener(this);
    }
    // scan result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent
            data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode,
                resultCode, data);
        if (result != null) {
            //if qr don't exist
            if (result.getContents() == null) {
                Toast.makeText(this, "Qr code was cancelled",
                        Toast.LENGTH_LONG).show();
                //if qr code exist >>>
            } // web url
            if (Patterns.WEB_URL.matcher(result.getContents()).matches()) {
                Intent OpenBrowser = new Intent(Intent.ACTION_VIEW,
                        Uri.parse(result.getContents()));
                startActivity(OpenBrowser);
            }
//phone call
            String number;
            number = new String(result.getContents());
            if(number.matches("^[0-9]*$") && number.length() > 11){
                Intent dial = new Intent(Intent.ACTION_DIAL);
                dial.setData(Uri.parse("tel:" + number));
                startActivity(dial);
            } else {

           //Email
                String alamat = result.getContents();
                String at = "@gmail";
                if (alamat.contains(at)) {
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    String[] recipients = {alamat.replace("http://", "")};
                    intent.putExtra(Intent.EXTRA_EMAIL, recipients);
                    intent.putExtra(Intent.EXTRA_SUBJECT, "Subject Email");
                    intent.putExtra(Intent.EXTRA_TEXT, "Type Here");
                    intent.putExtra(Intent.EXTRA_CC, "");
                    intent.setType("text/html");
                    intent.setPackage("com.google.android.gm");
                    startActivity(Intent.createChooser(intent, "Send mail"));
                }
                

                try {
                    //json
                    JSONObject obj = new JSONObject(result.getContents());
                    //set result
                    textViewName.setText(obj.getString("nama"));
                    textViewClass.setText(obj.getString("kelas"));
                    textViewId.setText(obj.getString("nim"));
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(this, result.getContents(),
                            Toast.LENGTH_LONG).show();
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
    @Override
    public void onClick(View view) {
        qrScan.initiateScan();
    }

}