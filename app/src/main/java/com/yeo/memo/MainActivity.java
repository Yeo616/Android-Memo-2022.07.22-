package com.yeo.memo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.yeo.memo.adapter.MemoAdapter;
import com.yeo.memo.api.MemoApi;
import com.yeo.memo.api.NetworkClient;
import com.yeo.memo.config.Config;
import com.yeo.memo.model.Memo;
import com.yeo.memo.model.MemoList;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity {

    String accessToken;

    Button btnAdd;
    ProgressBar progressBar;

    // 라사이클러 뷰 관련 멤버변수 3개
    RecyclerView recyclerView;
    MemoAdapter adapter;
    ArrayList<Memo> memoList = new ArrayList<>();

    // 페이징에 필요한 변수
    int offset = 0;
    int limit = 7;
    int count = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1. 쉐어드프리퍼런스에 억세스토큰을 가져온다.
        SharedPreferences sp =
                getApplication().getSharedPreferences(Config.PREFERENCES_NAME, MODE_PRIVATE);
        accessToken = sp.getString("accessToken", "");

        // 2. 만약 억세스토큰이 없으면, 회원가입 액티비티를 실행하고,
        //    그렇지 않으면, 메모가져오는 API 호출해서, 리사이클러뷰로 화면에 내 메모 보여준다.
        if(accessToken.isEmpty()){
            Intent intent =
                    new Intent(MainActivity.this, RegisterActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        btnAdd = findViewById(R.id.btnAdd);
        progressBar = findViewById(R.id.progressBar);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int lastPosition = ((LinearLayoutManager)recyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition();
                int totalCount = recyclerView.getAdapter().getItemCount();

                if(  lastPosition+1  == totalCount  ){

                    if(count == limit){
                        // 네트워크 통해서, 데이터를 더 불러오면 된다.
                        addNetworkData();
                    }
                }

            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        getNetworkData();
    }

    // 데이터를 처음 가져올때만 실행하는 함수.
    // 데이터의 초기화도 필요하다.
    private void getNetworkData() {

        memoList.clear();
        count = 0;
        offset = 0;

        progressBar.setVisibility(View.VISIBLE);

        Retrofit retrofit = NetworkClient.getRetrofitClient(MainActivity.this);
        MemoApi api = retrofit.create(MemoApi.class);

        Call<MemoList> call = api.getMemoList("Bearer "+accessToken,
                offset, limit);

        call.enqueue(new Callback<MemoList>() {
            @Override
            public void onResponse(Call<MemoList> call, Response<MemoList> response) {
                progressBar.setVisibility(View.GONE);

                if(response.isSuccessful()){

                    // 정상으로 데이터 받아왔으니, 리사이클러뷰에 표시
                    MemoList data = response.body();
                    count = data.getCount();
                    memoList.addAll( data.getItems() )  ;
                    offset = offset + count;

                    adapter = new MemoAdapter(MainActivity.this, memoList);

                    recyclerView.setAdapter(adapter);

                } else {

                }
            }

            @Override
            public void onFailure(Call<MemoList> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    // 처음이 아니라, 더 가져오는 경우!!
    private void addNetworkData() {

        // 데이터가져오는것을 표현하기 위해서 프로그래스바를 표시
        progressBar.setVisibility(View.VISIBLE);

        Retrofit retrofit = NetworkClient.getRetrofitClient(MainActivity.this);
        MemoApi api = retrofit.create(MemoApi.class);

        Call<MemoList> call = api.getMemoList("Bearer "+accessToken,
                offset, limit);

        call.enqueue(new Callback<MemoList>() {
            @Override
            public void onResponse(Call<MemoList> call, Response<MemoList> response) {
                progressBar.setVisibility(View.GONE);

                if(response.isSuccessful()){

                    // 정상으로 데이터 받아왔으니, 리사이클러뷰에 표시
                    MemoList data = response.body();
                    count = data.getCount();
                    memoList.addAll( data.getItems() );

                    offset = offset + count;

                    adapter.notifyDataSetChanged();

                } else {

                }
            }

            @Override
            public void onFailure(Call<MemoList> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
            }
        });

    }
}



