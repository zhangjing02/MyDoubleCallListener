package com.senssun.myquickrunning;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;

public class MainActivity extends AppCompatActivity {
   private TelephonyManager mTelephonyManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//         mTelephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
//        mTelephonyManager.listen(new PhoneStateListener(), PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
//
//
//        SubscriptionManager mSubscriptionManager = SubscriptionManager.from(this);
//
//        SubscriptionInfo sub0 = mSubscriptionManager.getActiveSubscriptionInfoForSimSlotIndex(0);
//        if (null!=sub0){
//            Sim1SignalStrengthsListener mSim1SignalStrengthsListener = new Sim1SignalStrengthsListener(sub0.getSubscriptionId());
//            //开始监听
//            mTelephonyManager.listen(mSim1SignalStrengthsListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
//
//        }
//        SubscriptionInfo sub1 = mSubscriptionManager.getActiveSubscriptionInfoForSimSlotIndex(1);
//        if (null!=sub1){
//            Sim1SignalStrengthsListener mSim1SignalStrengthsListener = new Sim1SignalStrengthsListener(sub1.getSubscriptionId());
//            //开始监听
//            mTelephonyManager.listen(mSim1SignalStrengthsListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
//        }

    }




}
