package com.miqtech.master.client.ui;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.miqtech.master.client.R;
import com.miqtech.master.client.adapter.NewPeopleTaskAdapter;
import com.miqtech.master.client.entity.TaskInfo;
import com.miqtech.master.client.ui.baseactivity.BaseActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * 新手任务
 * Created by Administrator on 2015/12/3.
 */
public class NewPeopleTaskActivity extends BaseActivity implements View.OnClickListener {
    private Context context;
    private ListView lvtask;
    private List<TaskInfo> tasklist = new ArrayList<TaskInfo>();
    private List<TaskInfo> tasklist2 = new ArrayList<TaskInfo>();
    private NewPeopleTaskAdapter adapter;

    @Override
    protected void init() {
        // TODO Auto-generated method stub
        super.init();
        setContentView(R.layout.activity_new_people_task);
        initView();
        initData();
    }

    @Override
    protected void initView() {
        // TODO Auto-generated method stub
        super.initView();
        lvtask = (ListView) findViewById(R.id.lvbnewtask);

        setLeftIncludeTitle("新手任务");
        setLeftBtnImage(R.drawable.back);
        getLeftBtn().setOnClickListener(this);
        context = NewPeopleTaskActivity.this;

        adapter = new NewPeopleTaskAdapter(context, tasklist2);
        lvtask.setAdapter(adapter);
    }

    @Override
    protected void initData() {
        // TODO Auto-generated method stub
        super.initData();
        tasklist = getIntent().getParcelableArrayListExtra("mTasklist");
        updateData(tasklist);
    }

    private void updateData(final List<TaskInfo> list) {
        if (list != null) {
            tasklist2.clear();
            tasklist2.addAll(list);
            adapter.notifyDataSetChanged();
            lvtask.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                    Intent i = new Intent(context, SubjectActivity.class);
                    i.putExtra(SubjectActivity.HTML5_TYPE, SubjectActivity.RENWU);
                    i.putExtra("id", list.get(arg2).getId() + "");
                    startActivity(i);
                }
            });
        }
    }

    @Override
    public void onClick(View arg0) {
        switch (arg0.getId()) {
            case R.id.ibLeft:
                onBackPressed();
                break;
            default:
                break;
        }

    }
}
