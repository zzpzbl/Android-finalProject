package com.example.finalpj;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.example.finalpj.adapter.FmPagerAdapter;
import com.example.finalpj.adapter.RecordsAdapter;
import com.example.finalpj.entity.Event;
import com.example.finalpj.fragment.FutureFragment;
import com.example.finalpj.fragment.PastFragment;
import com.example.finalpj.utils.DBUtil;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private String[] titles = new String[]{"过去", "未来"};
    private List<Fragment> fragments;
    private PagerAdapter pagerAdapter;
    private Button addEventButton;
    private EditText searchEditText;
    private RecyclerView recyclerView;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initComponent();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        initComponent();
    }

    private void initComponent() {
        context = MainActivity.this;
        tabLayout = findViewById(R.id.tablayout);
        viewPager = findViewById(R.id.viewpager);
        addEventButton = findViewById(R.id.button_add);
        searchEditText = findViewById(R.id.search_edit_text);
        recyclerView = findViewById(R.id.recyclerView);
        fragments = new ArrayList<>();
        fragments.add(new PastFragment(context));
        fragments.add(new FutureFragment(context));
        pagerAdapter = new FmPagerAdapter(fragments, getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        for (int i = 0; i < titles.length; i++) {
            tabLayout.addTab(tabLayout.newTab());
        }
        tabLayout.setupWithViewPager(viewPager, false);
        addEventButton.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, EditActivity.class);
            startActivity(intent);
        });
        searchEditText.setOnEditorActionListener((textView, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                String text = textView.getText().toString();
                List<Event> events = DBUtil.selectEventFuzzily(text);
                RecordsAdapter recordsAdapter = new RecordsAdapter(context, events);
                recyclerView.setAdapter(recordsAdapter);
                closeSoftKeyBoard(searchEditText, context);
                return true;
            }
            return false;
        });
        for (int i = 0; i < titles.length; i++) {
            tabLayout.getTabAt(i).setText(titles[i]);
        }
    }

    public void flushPage() {
        fragments.clear();
        fragments.add(new PastFragment(context));
        fragments.add(new FutureFragment(context));
        pagerAdapter.notifyDataSetChanged();
        Log.i("flushPage", "触发 flushPage");
    }

    private void closeSoftKeyBoard(EditText searchEditText, Context context) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(searchEditText.getWindowToken(), 0);
    }

    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.recyclerView, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}
