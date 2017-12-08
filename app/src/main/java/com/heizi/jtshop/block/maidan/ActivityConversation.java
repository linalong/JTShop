package com.heizi.jtshop.block.maidan;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.heizi.jtshop.MyApplication;
import com.heizi.jtshop.R;
import com.heizi.jtshop.activity.BaseSwipeBackCompatActivity;

import java.util.Locale;

import butterknife.InjectView;
import io.rong.imkit.RongIM;
import io.rong.imkit.fragment.ConversationFragment;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;


/**
 * Created by Bob on 15/8/18.
 * 会话页面
 */
public class ActivityConversation extends BaseSwipeBackCompatActivity implements View.OnClickListener{
    @InjectView(R.id.tv_title)
    public TextView mTvTitle;
    private RelativeLayout mBack;

    private String mTargetId;
    private String mTitle;

    /**
     * 刚刚创建完讨论组后获得讨论组的id 为targetIds，需要根据 为targetIds 获取 targetId
     */
    private String mTargetIds;

    /**
     * 会话类型
     */
    private Conversation.ConversationType mConversationType;

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_conversation;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();

        getIntentDate(intent);

        isReconnect(intent);
    }



    /**
     * 展示如何从 Intent 中得到 融云会话页面传递的 Uri
     */
    private void getIntentDate(Intent intent) {

        mTargetId = intent.getData().getQueryParameter("targetId");
        mTitle = intent.getData().getQueryParameter("title");
        mTargetIds = intent.getData().getQueryParameter("targetIds");
        //intent.getData().getLastPathSegment();//获得当前会话类型
        mConversationType = Conversation.ConversationType.valueOf(intent.getData().getLastPathSegment().toUpperCase(Locale.getDefault()));

        enterFragment(mConversationType, mTargetId);
        mTvTitle.setText(mTitle);
    }


    /**
     * 加载会话页面 ConversationFragment
     *
     * @param mConversationType
     * @param mTargetId
     */
    private void enterFragment(Conversation.ConversationType mConversationType, String mTargetId) {

        ConversationFragment fragment = (ConversationFragment) getSupportFragmentManager().findFragmentById(R.id.conversation);

        Uri uri = Uri.parse("rong://" + getApplicationInfo().packageName).buildUpon()
                .appendPath("conversation").appendPath(mConversationType.getName().toLowerCase())
                .appendQueryParameter("targetId", mTargetId).build();

        fragment.setUri(uri);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_back:
                finish();
            break;
        }

    }

    /**
     * 判断消息是否是 push 消息
     */
    private void isReconnect(Intent intent) {


//        String token = null;
//
//        if (DemoContext.getInstance() != null) {
//
//            token = DemoContext.getInstance().getSharedPreferences().getString("DEMO_TOKEN", "default");
//        }
//
//        //push或通知过来
//        if (intent != null && intent.getData() != null && intent.getData().getScheme().equals("rong")) {
//
//            //通过intent.getData().getQueryParameter("push") 为true，判断是否是push消息
//            if (intent.getData().getQueryParameter("push") != null
//                    && intent.getData().getQueryParameter("push").equals("true")) {
//
//                reconnect(token);
//            } else {
//                //程序切到后台，收到消息后点击进入,会执行这里
//                if (RongIM.getInstance() == null || RongIM.getInstance().getRongIMClient() == null) {
//
//                    reconnect(token);
//                } else {
//                    enterFragment(mConversationType, mTargetId);
//                }
//            }
//        }
    }





    /**
     * 重连
     *
     * @param token
     */
    private void reconnect(String token) {

        if (getApplicationInfo().packageName.equals(MyApplication.getCurProcessName(getApplicationContext()))) {

            RongIM.connect(token, new RongIMClient.ConnectCallback() {
                @Override
                public void onTokenIncorrect() {

                }

                @Override
                public void onSuccess(String s) {

                    enterFragment(mConversationType, mTargetId);
                }

                @Override
                public void onError(RongIMClient.ErrorCode errorCode) {

                }
            });
        }
    }
}
