package innovations.tcs.com.mlcpuser.Activities;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.util.ArrayList;

import innovations.tcs.com.mlcpuser.Beans.MyCarsBean;
import innovations.tcs.com.mlcpuser.Databases.DatabaseHandlerCarList;
import innovations.tcs.com.mlcpuser.R;
import innovations.tcs.com.mlcpuser.RecyclerAdapters.MyCarsRecyclerAdapter;

public class MyCarsActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    ArrayList<MyCarsBean> myCarsBeansList;
    Toolbar toolbar;
    ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mycars);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        toolbar.setNavigationIcon(R.drawable.menu_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        final DatabaseHandlerCarList carListDB = new DatabaseHandlerCarList(this);
        recyclerView = (RecyclerView) findViewById(R.id.list_colors);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(gridLayoutManager);
        myCarsBeansList = new ArrayList<>();

        MyCarsBean mcb;
        if (carListDB.getCarListCount() > 0) {
            for (String car : carListDB.getCarList()) {
                mcb = new MyCarsBean("1", car);
                myCarsBeansList.add(mcb);
            }
        }
        MyCarsRecyclerAdapter myCarsRecyclerAdapter = new MyCarsRecyclerAdapter(MyCarsActivity.this, myCarsBeansList);
        recyclerView.setAdapter(myCarsRecyclerAdapter);
    }
}