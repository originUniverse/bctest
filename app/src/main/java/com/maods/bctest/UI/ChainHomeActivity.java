package com.maods.bctest.UI;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.maods.bctest.ChainCommonOperations;
import com.maods.bctest.EOS.EOSOperations;
import com.maods.bctest.GlobalConstants;
import com.maods.bctest.R;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by MAODS on 2018/7/17.
 */

public class ChainHomeActivity extends Activity {
    private static final String TAG="ChainHomeActivity";

    private static final String GET_CHAIN_INFO="get_chain_info";
    private static final String[] BTC_actions=new String[]{};
    private static final String[] ETH_actions=new String[]{};
    private static final String[] EOS_actions=new String[]{
            GET_CHAIN_INFO
    };
    private static final String[] Fabric_actions=new String[]{};

    String mTarget;
    String[] mTargetActions;
    private ChainCommonOperations mCommonOps=null;

    private LinearLayout mContentView;
    private TextView mTitleView;
    private TextView mInfoView;
    private ListView mListView;
    private TextView mEmptyView;
    private ArrayAdapter<String> mAdapter;
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.chain_home);
        mContentView=(LinearLayout)findViewById(R.id.container);
        mTitleView=(TextView)findViewById(R.id.title);
        mInfoView=(TextView)findViewById(R.id.info);
        mEmptyView=(TextView)findViewById(R.id.empty);
        mListView=(ListView)findViewById(R.id.list);

        Intent intent=getIntent();
        mTarget=intent.getStringExtra(GlobalConstants.EXTRA_KEY_CHAIN);
        switch(mTarget) {
            case GlobalConstants.BTC:
                mTargetActions = BTC_actions;
                break;
            case GlobalConstants.ETH:
                mTargetActions = ETH_actions;
                break;
            case GlobalConstants.EOS:
                mTargetActions = EOS_actions;
                mCommonOps=new EOSOperations();
                break;
            case GlobalConstants.FABRIC:
                mTargetActions = Fabric_actions;
                break;
            default:
                mTargetActions = EOS_actions;
                break;
        }

        if(mTargetActions.length==0){
            mListView.setVisibility(View.GONE);
        }else{
            mEmptyView.setVisibility(View.GONE);
            mAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,mTargetActions);
            mListView.setAdapter(mAdapter);
            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    handleOnItemClickInUIThread(view,position);
                }
            });
        }

        if(mCommonOps!=null){
            Thread t=new Thread(new Runnable() {
                @Override
                public void run() {
                    final List<String> serverNodes=mCommonOps.getServerNode();
                    ChainHomeActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            updateInfo(serverNodes);
                        }
                    });
                }
            });
            t.start();
        }
    }

    private void updateInfo(List<String>servers){
        if(servers.size()==0){
            mInfoView.setText(R.string.no_server_available);
        }else{
            StringBuilder sb=new StringBuilder();
            for(int i=0;i<servers.size();i++){
                sb.append(servers.get(i)+"\n");
            }
            String serversStr=String.format(getResources().getString(R.string.available_servers),sb.toString());
            mInfoView.setText(serversStr);
        }
    }

    private void handleOnItemClickInUIThread(View view,int position){
        switch(mTarget){
            case GlobalConstants.EOS:{
                switch(mTargetActions[position]){
                    case GET_CHAIN_INFO:
                        startEOSGetChainInfo();
                        break;
                    default:
                        break;
                }
                break;
            }
            default:
                break;
        }
    }
    private void startEOSGetChainInfo(){
        Intent intent=new Intent();
        intent.setClass(this,EOSInfoActivity.class);
        intent.putExtra(GlobalConstants.EXTRA_KEY_ACTION,GlobalConstants.EXTRA_VALUE_EOS_GET_CHAIN_INFO);
        startActivity(intent);
    }
}