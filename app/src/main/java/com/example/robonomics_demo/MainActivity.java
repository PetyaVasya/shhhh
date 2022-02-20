package com.example.robonomics_demo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.os.Bundle;

import android.os.Environment;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.web3j.contracts.eip20.generated.ERC20;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.core.methods.response.Web3ClientVersion;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.Transfer;
import org.web3j.tx.gas.DefaultGasProvider;
import org.web3j.utils.Convert;
import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.Provider;
import java.security.Security;

import static android.os.Environment.DIRECTORY_DOWNLOADS;

public class MainActivity extends AppCompatActivity {

    Web3j web3;
    Credentials credentials;
    TextView txtaddress;
    ERC20 token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtaddress = findViewById(R.id.text_address);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        web3 = Web3j.build(new HttpService("https://sokol.poa.network"));

        setupBouncyCastle();
        connectToEthNetwork();

    }

    public void connectToEthNetwork() {
        try {
            Web3ClientVersion clientVersion = web3.web3ClientVersion().sendAsync().get();
        } catch (Exception e) {
            ShowToast(e.getMessage());
        }
    }


    public void retrieveBalance (View v)  {

        try {
            TextView txtbalance = findViewById(R.id.text_balance);
            txtbalance.setText(getString(R.string.your_balance) + token.balanceOf(credentials.getAddress()).send());
        }
        catch (Exception e){
            ShowToast("balance failed");

        }
    }

    public void createWallet(View v)  {

        try {
            EditText privateKey = findViewById(R.id.privatekey);

            credentials = Credentials.create(privateKey.getText().toString());
            setupToken();
            txtaddress.setText(getString(R.string.your_address) + credentials.getAddress());

        }
        catch(Exception e){
            ShowToast("failed");

        }
    }
    
    public void setupToken() {
        token = ERC20.load("0x29df02D4425A86a29636CF770a50335e7857a4B0", web3, credentials, new DefaultGasProvider());
    }


    public void makeTransaction(View v) throws Exception {
        EditText editValue = findViewById(R.id.ethvalue);
        EditText editReceiver = findViewById(R.id.ethreceiver);
        BigInteger value = new BigInteger(editValue.getText().toString());
        String receiver = editReceiver.getText().toString();
        try{
            token.transfer(receiver, value).sendAsync();
        }
        catch(Exception e){
            ShowToast("low balance");
        }
    }


    public void ShowToast(String message) {
        runOnUiThread(() -> {
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        });
    }

    private void setupBouncyCastle() {
        final Provider provider = Security.getProvider(BouncyCastleProvider.PROVIDER_NAME);
        if (provider == null) {
            return;
        }
        if (provider.getClass().equals(BouncyCastleProvider.class)) {
            return;
        }
        Security.removeProvider(BouncyCastleProvider.PROVIDER_NAME);
        Security.insertProviderAt(new BouncyCastleProvider(), 1);
    }

}