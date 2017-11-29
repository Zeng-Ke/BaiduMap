package com.zk.baidumap;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.baidu.mapapi.search.sug.SuggestionSearchOption;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static android.R.attr.editable;


public class SearchAddressActivity extends AppCompatActivity {

    private EditText mEditAddress;
    private RecyclerView mRecyclerview;
    public String mCity = "广州";
    public String mKeyword;
    public static final String KEY_RESULT_ADDRESS = "KEY_RESULT_ADDRESS";

    private SuggestionSearch mSuggestionSearch;
    private AddressAdapter mAddressAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_address);
        mEditAddress = (EditText) findViewById(R.id.edit_address);
        mRecyclerview = (RecyclerView) findViewById(R.id.rv_address);
        mSuggestionSearch = SuggestionSearch.newInstance();
        mAddressAdapter = new AddressAdapter(new ArrayList<SuggestionResult.SuggestionInfo>());
        mRecyclerview.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerview.setAdapter(mAddressAdapter);
        initListener();

    }

    private void initListener() {
        mSuggestionSearch.setOnGetSuggestionResultListener(new OnGetSuggestionResultListener() {
            @Override
            public void onGetSuggestionResult(SuggestionResult suggestionResult) {
                if (suggestionResult.error.equals(SearchResult.ERRORNO.NETWORK_ERROR) || suggestionResult.error.equals(SearchResult.ERRORNO
                        .NETWORK_TIME_OUT)) {
                    Toast.makeText(SearchAddressActivity.this, "网络错误", Toast.LENGTH_SHORT);
                    return;
                }
                List<SuggestionResult.SuggestionInfo> allPoi = suggestionResult.getAllSuggestions();
                if (allPoi != null) {
                    mAddressAdapter.setNewDatas(allPoi);
                }
            }
        });


        mEditAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                mKeyword = editable.toString();
                mSuggestionSearch.requestSuggestion(new SuggestionSearchOption().city(mCity).keyword(mKeyword));
            }
        });
    }


    public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.Hodler> {

        private List<SuggestionResult.SuggestionInfo> mInfos;

        public AddressAdapter(List<SuggestionResult.SuggestionInfo> infos) {
            mInfos = infos;
        }

        public void setNewDatas(List<SuggestionResult.SuggestionInfo> infos) {
            mInfos = infos;
            notifyDataSetChanged();
        }

        @Override
        public AddressAdapter.Hodler onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_address, parent, false);
            return new Hodler(view);
        }

        @Override
        public void onBindViewHolder(Hodler holder, int position) {
            final SuggestionResult.SuggestionInfo address = mInfos.get(position);
            holder.textView.setText(address.key);
            holder.textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent();
                    intent.putExtra(KEY_RESULT_ADDRESS,new LatLngBean(address.pt.longitude,address.pt.latitude));
                    setResult(RESULT_OK,intent);
                    finish();
                }
            });
        }


        @Override
        public int getItemCount() {
            return mInfos.size();
        }

        public class Hodler extends RecyclerView.ViewHolder {

            public TextView textView;

            public Hodler(View itemView) {
                super(itemView);
                textView = itemView.findViewById(R.id.tv_address);
            }
        }


    }

}
