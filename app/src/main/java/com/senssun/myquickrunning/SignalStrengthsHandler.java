package com.senssun.myquickrunning;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.lang.reflect.Method;
import java.util.ArrayList;

/**
 * Created by zhangj on 2017/9/13.
 */

public class SignalStrengthsHandler {

    public static final String TAG = "SignalStrengthsManager";
    public static final int INDEX_SIM1 = 0;
    public static final int INDEX_SIM2 = 1;
    private static SignalStrengthsHandler mInstance = null;
    public static byte[] mLock = new byte[0];
    private final TelephonyManager mTelephonyManager;
    private final SubscriptionManager mSubscriptionManager;
    private final SimStateReceive mSimStateReceiver;

    private SimSignalInfo mSim1SignalInfo = new SimSignalInfo();
    private SimSignalInfo mSim2SignalInfo = new SimSignalInfo();

    private ArrayList<OnSignalStrengthsChangedListener> mOnSignalStrengthsChangedListeners = null;
    private Sim1SignalStrengthsListener mSim1SignalStrengthsListener;
    private Sim2SignalStrengthsListener mSim2SignalStrengthsListener;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP_MR1)
    private SignalStrengthsHandler(Context mContext) {
        mSubscriptionManager = SubscriptionManager.from(mContext);
        mTelephonyManager = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        initListeners();

        mSimStateReceiver = new SimStateReceive();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(SimStateReceive.ACTION_SIM_STATE_CHANGED);
        mContext.registerReceiver(mSimStateReceiver, intentFilter);
    }

    public static SignalStrengthsHandler getInstance(Context mContext) {
        if (null == mInstance) {
            synchronized (mLock) {
                if (null == mInstance) {
                    mInstance = new SignalStrengthsHandler(mContext);
                }
            }
        }
        return mInstance;
    }

    public void destroyInstance(Context mContext) {
        if (null != mInstance) {
            synchronized (mLock) {
                if (null != mInstance) {
                    if (null != mOnSignalStrengthsChangedListeners) {
                        mOnSignalStrengthsChangedListeners.clear();
                        mOnSignalStrengthsChangedListeners = null;
                    }
                    mContext.unregisterReceiver(mSimStateReceiver);
                    mInstance = null;
                }
            }
        }
    }

    private void initListeners() {
        listenSimSignalStrengths(SimCard.SIM_CARD_1);
        listenSimSignalStrengths(SimCard.SIM_CARD_2);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP_MR1)
    private void listenSimSignalStrengths(SimCard simCard) {
        if (simCard == SimCard.SIM_CARD_1) {
            SubscriptionInfo sub0 = mSubscriptionManager.getActiveSubscriptionInfoForSimSlotIndex(INDEX_SIM1);
            if (sub0 != null && null == mSim1SignalStrengthsListener) {
                mSim1SignalStrengthsListener = new Sim1SignalStrengthsListener(sub0.getSubscriptionId());
            }
           // mTelephonyManager.listen(mSim1SignalStrengthsListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);//这个是监听卡1的信号强度
            mTelephonyManager.listen(mSim1SignalStrengthsListener, PhoneStateListener.LISTEN_CALL_STATE);//这个是监听卡1来电的情况
        } else if (simCard == SimCard.SIM_CARD_2) {
            SubscriptionInfo sub1 = mSubscriptionManager.getActiveSubscriptionInfoForSimSlotIndex(INDEX_SIM2);
            if (sub1 != null && null == mSim2SignalStrengthsListener) {
                mSim2SignalStrengthsListener = new Sim2SignalStrengthsListener(sub1.getSubscriptionId());
            }
           // mTelephonyManager.listen(mSim2SignalStrengthsListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);//这个是监听卡2的信号强度
            mTelephonyManager.listen(mSim2SignalStrengthsListener, PhoneStateListener.LISTEN_CALL_STATE);//这个是监听卡2来电的情况
        }
    }

    private void unListenSimSignalStrengths(SimCard simCard) {
        if (simCard == SimCard.SIM_CARD_1) {
            mSim1SignalInfo.mIsActive = false;
            mSim1SignalInfo.mLevel = 0;
            if (null != mSim1SignalStrengthsListener) {
                mTelephonyManager.listen(mSim1SignalStrengthsListener, PhoneStateListener.LISTEN_NONE);
            }
        } else if (simCard == SimCard.SIM_CARD_2) {
            mSim2SignalInfo.mIsActive = false;
            mSim2SignalInfo.mLevel = 0;
            if (null != mSim2SignalStrengthsListener) {
                mTelephonyManager.listen(mSim2SignalStrengthsListener, PhoneStateListener.LISTEN_NONE);
            }
        }

    }

    /**
     * 添加监听sim卡信号强度
     *
     * @param listener
     */
    public void registerOnSignalStrengthsChangedListener(OnSignalStrengthsChangedListener listener) {
        if (null == mOnSignalStrengthsChangedListeners) {
            mOnSignalStrengthsChangedListeners = new ArrayList<>();
        }

        if (mOnSignalStrengthsChangedListeners.contains(listener)) {
            return;
        }

        if (null != listener) {
            mOnSignalStrengthsChangedListeners.add(listener);
        }
    }

    public void unregisterOnSignalStrengthsChangedListener(OnSignalStrengthsChangedListener listener) {
        if (null == mOnSignalStrengthsChangedListeners) {
            return;
        }

        if (null == listener) {
            return;
        }

        if (mOnSignalStrengthsChangedListeners.contains(listener)) {
            mOnSignalStrengthsChangedListeners.remove(listener);
        }
    }

    public void notyfyStateChange(boolean isSim1Exist, boolean isSim2Exist) {
        if (null != mOnSignalStrengthsChangedListeners && !mOnSignalStrengthsChangedListeners.isEmpty()) {
            for (int i = 0; i < mOnSignalStrengthsChangedListeners.size(); i++) {
                OnSignalStrengthsChangedListener listener = mOnSignalStrengthsChangedListeners.get(i);
                if (null != listener) {
                    listener.onSimStateChanged(isSim1Exist, isSim2Exist);
                }
            }
        }
    }

    public void notifyChange(SimCard simCard, int level) {
        if (null != mOnSignalStrengthsChangedListeners && !mOnSignalStrengthsChangedListeners.isEmpty()) {
            for (int i = 0; i < mOnSignalStrengthsChangedListeners.size(); i++) {
                OnSignalStrengthsChangedListener listener = mOnSignalStrengthsChangedListeners.get(i);
                if (null != listener) {
                    listener.onSignalStrengthsChanged(simCard, level);
                }
            }
        }
    }

    public boolean isSimCardExist(int cardIndex) {
        boolean isSimCardExist = false;
        try {
            Method method = TelephonyManager.class.getMethod("getSimState", new Class[]{int.class});
            int simState = (Integer) method.invoke(mTelephonyManager, new Object[]{Integer.valueOf(cardIndex)});
            if (TelephonyManager.SIM_STATE_READY == simState) {
                isSimCardExist = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return isSimCardExist;
    }

    /**
     * 获取sim信号 状态信息
     *
     * @return int[]  index: 0:sim1 1:sim2
     */
    public SimSignalInfo[] getSimSignalInfos() {
        return new SimSignalInfo[]{mSim1SignalInfo, mSim2SignalInfo};
    }

    private int getSignalStrengthsLevel(SignalStrength signalStrength) {
        int level = -1;
        try {
            Method levelMethod = SignalStrength.class.getDeclaredMethod("getLevel");
            level = (int) levelMethod.invoke(signalStrength);
        } catch (Exception e) {
            Log.e(TAG, "getSignalStrengthsLevel: "+e.getMessage() );
        }
        return level;
    }

    private class Sim1SignalStrengthsListener extends PhoneStateListener {

        public Sim1SignalStrengthsListener(int subId) {
            super();
            //设置当前监听的sim卡
            ReflectUtil.setFieldValue(this, "mSubId", subId);
        }

        @Override
        public void onSignalStrengthsChanged(SignalStrength signalStrength) {
            super.onSignalStrengthsChanged(signalStrength);
            int level = getSignalStrengthsLevel(signalStrength);
            if (mSim1SignalInfo.mLevel == level) {
                return;
            }
            mSim1SignalInfo.mLevel = level;
            SignalStrengthsHandler.this.notifyChange(SimCard.SIM_CARD_1, mSim1SignalInfo.mLevel);
            Log.d(TAG, "sim 1 signal strengths level = " + mSim1SignalInfo.mLevel);
        }

        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
            switch(state){
                case TelephonyManager.CALL_STATE_IDLE:
//                    NotiService.SendNumber(incomingNumber);
                    Log.i(TAG, "卡1的响铃:来电挂断"+incomingNumber);
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    Log.i(TAG, "卡1的响铃:来电接听"+incomingNumber);
                    break;
                case TelephonyManager.CALL_STATE_RINGING:
                    Log.i(TAG, "卡1的响铃:来电号码xxxxx"+incomingNumber);
                    break;
            }
        }

    }

    private class Sim2SignalStrengthsListener extends PhoneStateListener {

        public Sim2SignalStrengthsListener(int subId) {
            super();
            //设置当前监听的sim卡
            ReflectUtil.setFieldValue(this, "mSubId", subId);
        }

        @Override
        public void onSignalStrengthsChanged(SignalStrength signalStrength) {
            super.onSignalStrengthsChanged(signalStrength);
            int level = getSignalStrengthsLevel(signalStrength);
            if (mSim2SignalInfo.mLevel == level) {
                return;
            }
            mSim2SignalInfo.mLevel = level;
            SignalStrengthsHandler.this.notifyChange(SimCard.SIM_CARD_2, mSim2SignalInfo.mLevel);
            Log.d(TAG, "sim 2 signal strengths level = " + mSim2SignalInfo.mLevel);
        }

        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
            switch(state){
                case TelephonyManager.CALL_STATE_IDLE:
//                    NotiService.SendNumber(incomingNumber);
                    Log.i(TAG, "卡2的响铃:来电挂断"+incomingNumber);
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    Log.i(TAG, "卡2的响铃:来电接听"+incomingNumber);
                    break;
                case TelephonyManager.CALL_STATE_RINGING:
                    Log.i(TAG, "卡2的响铃:来电号码xxxxx"+incomingNumber);
                    break;
            }
        }
    }

    public interface OnSignalStrengthsChangedListener {
        void onSignalStrengthsChanged(SimCard simCard, int level);

        void onSimStateChanged(boolean isSim1Exist, boolean isSim2Exist);
    }

    public enum SimCard {
        SIM_CARD_1, SIM_CARD_2
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP_MR1)
    class SimStateReceive extends BroadcastReceiver {
        private final static String ACTION_SIM_STATE_CHANGED = "android.intent.action.SIM_STATE_CHANGED";

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "sim state changed");
            if (intent.getAction().equals(ACTION_SIM_STATE_CHANGED)) {
                mSim1SignalInfo.mIsActive = isSimCardExist(INDEX_SIM1)
                        && null != mSubscriptionManager.getActiveSubscriptionInfoForSimSlotIndex(INDEX_SIM1);
                mSim2SignalInfo.mIsActive = isSimCardExist(INDEX_SIM2)
                        && null != mSubscriptionManager.getActiveSubscriptionInfoForSimSlotIndex(INDEX_SIM2);

                mSim1SignalInfo.mLevel = 0;
                mSim2SignalInfo.mLevel = 0;
                if (mSim1SignalInfo.mIsActive) {
                    listenSimSignalStrengths(SimCard.SIM_CARD_1);
                } else {
                    unListenSimSignalStrengths(SimCard.SIM_CARD_1);
                }
                if (mSim2SignalInfo.mIsActive) {
                    listenSimSignalStrengths(SimCard.SIM_CARD_2);
                } else {
                    unListenSimSignalStrengths(SimCard.SIM_CARD_2);
                }
                notyfyStateChange(mSim1SignalInfo.mIsActive, mSim2SignalInfo.mIsActive);
            }
        }
    }

    public class SimSignalInfo {
        /**
         * 信号强度 0 - 5
         */
        public int mLevel;

        /**
         * sim卡是否有效
         */
        public boolean mIsActive;
    }
}
