package com.todo.ui.crud;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.loonggg.lib.alarmmanager.clock.AlarmManagerUtil;
import com.todo.R;
import com.todo.data.bean.CalendarBean;
import com.todo.data.database.Schedule;
import com.todo.data.database.WeekSchedule;
import com.todo.ui.base.BaseActivity;
import com.todo.ui.main.MainActivity;
import com.todo.utils.DateFormatUtil;
import com.todo.utils.DateTimePickDialogUtil;
import com.todo.utils.ImageButtonText;
import com.todo.utils.LogUtil;

import org.joda.time.DateTime;
import org.joda.time.Minutes;
import org.litepal.crud.DataSupport;

import java.util.Calendar;
import java.util.Locale;

/**
 * Created by tianyang on 2017/2/20.
 */
public class ModifyActivity extends BaseActivity implements ImageButtonText.OnImageButtonTextClickListener {
    private WeekSchedule mSchedule;
    private EditText titleEt;
    private TextView startTimeTv, xunhuanTv;
    private SwitchCompat switchCompat;
    private ImageButtonText ibt1, ibt2, ibt3, ibt4;
    private String title, startTime, xunhuan, tag;
    private CalendarBean calendarBean = new CalendarBean();
    public Calendar startCalendar;
    private int mScheduleId;
    private int alarmId;  //存入数据库的id同样设置闹钟id
    //记录是否点击过时间选择器
    private boolean isClicked = false;

    private int selectedIndex = 0; //重复类型
    private String[] weekdays = {"周一", "周二", "周三", "周四", "周五", "周六", "周日"};
    private String[] types = {"只此一次", "每天", "每周", "自定义"};
    private boolean[] selectedWeekdays = new boolean[7];


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify);
        initToorBar();
        initView();
        initDatas();
        initEvents();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        save();
        return true;
    }

    private void initToorBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolBar);
        toolbar.setTitle("编辑");
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    private void initView() {
        titleEt = (EditText) findViewById(R.id.title_et);
        startTimeTv = (TextView) findViewById(R.id.starttime_content_tv);
        xunhuanTv = (TextView) findViewById(R.id.xunhuan_content_tv);
        switchCompat = (SwitchCompat) findViewById(R.id.switchCompat);
        ibt1 = (ImageButtonText) findViewById(R.id.imageText1);
        ibt2 = (ImageButtonText) findViewById(R.id.imageText2);
        ibt3 = (ImageButtonText) findViewById(R.id.imageText3);
        ibt4 = (ImageButtonText) findViewById(R.id.imageText4);
        ibt1.getTextView().setText("工作");
        ibt2.getTextView().setText("生活");
        ibt3.getTextView().setText("学习");
        ibt4.getTextView().setText("其它");
        ibt1.getImgView().setImageResource(R.mipmap.gongzuo);
        ibt2.getImgView().setImageResource(R.mipmap.shenghuo);
        ibt3.getImgView().setImageResource(R.mipmap.xuexi);
        ibt4.getImgView().setImageResource(R.mipmap.qita);
        ibt1.setmOnImageButtonTextClickListener(this);
        ibt2.setmOnImageButtonTextClickListener(this);
        ibt3.setmOnImageButtonTextClickListener(this);
        ibt4.setmOnImageButtonTextClickListener(this);

    }

    private void initDatas() {
        mScheduleId = getIntent().getExtras().getInt("ScheduleID");
        mSchedule = DataSupport.find(WeekSchedule.class, mScheduleId);
    }

    private void initEvents() {
        titleEt.setText(mSchedule.getTitle());
        titleEt.setSelection(mSchedule.getTitle().length());
        switchCompat.setChecked(mSchedule.isRemind());
        startTimeTv.setText(mSchedule.getStartTime());
        xunhuanTv.setText(mSchedule.getCycleTime());
        switch (mSchedule.getBiaoqian()) {
            case "工作":
                ibt1.getImgView().setImageResource(R.mipmap.gongzuo1);
                ibt1.getTextView().setTextColor(getResources().getColor(R.color.b0));
                tag = "工作";
                break;
            case "学习":
                ibt2.getImgView().setImageResource(R.mipmap.xuexi1);
                ibt2.getTextView().setTextColor(getResources().getColor(R.color.b0));
                tag = "学习";
                break;
            case "生活":
                ibt3.getImgView().setImageResource(R.mipmap.shenghuo1);
                ibt3.getTextView().setTextColor(getResources().getColor(R.color.b0));
                tag = "生活";
                break;
            default:
                ibt4.getImgView().setImageResource(R.mipmap.qita1);
                ibt4.getTextView().setTextColor(getResources().getColor(R.color.b0));
                tag = "其它";
        }
    }

    @Override
    public void OnImageButtonTextClick(View v) {
        switch (v.getId()) {
            case R.id.imageText1:
                if (ibt1.isChecked()) {
                    ibt1.setChecked(false);
                    ibt1.getImgView().setImageResource(R.mipmap.gongzuo);
                    ibt1.getTextView().setTextColor(getResources().getColor(R.color.g0));
                } else {
                    tag = ibt1.getTextView().getText().toString();
                    resetAllImageBUttonText();
                    ibt1.setChecked(true);
                    ibt1.getImgView().setImageResource(R.mipmap.gongzuo1);
                    ibt1.getTextView().setTextColor(getResources().getColor(R.color.b0));
                }
                break;
            case R.id.imageText2:
                if (ibt2.isChecked()) {
                    ibt2.setChecked(false);
                    ibt2.getImgView().setImageResource(R.mipmap.shenghuo);
                    ibt2.getTextView().setTextColor(getResources().getColor(R.color.g0));
                } else {
                    tag = ibt2.getTextView().getText().toString();
                    resetAllImageBUttonText();
                    ibt2.setChecked(true);
                    ibt2.getImgView().setImageResource(R.mipmap.shenghuo1);
                    ibt2.getTextView().setTextColor(getResources().getColor(R.color.b0));
                }
                break;
            case R.id.imageText3:
                if (ibt3.isChecked()) {
                    ibt3.setChecked(false);
                    ibt3.getImgView().setImageResource(R.mipmap.xuexi);
                    ibt3.getTextView().setTextColor(getResources().getColor(R.color.g0));
                } else {
                    tag = ibt3.getTextView().getText().toString();
                    resetAllImageBUttonText();
                    ibt3.setChecked(true);
                    ibt3.getImgView().setImageResource(R.mipmap.xuexi1);
                    ibt3.getTextView().setTextColor(getResources().getColor(R.color.b0));
                }
                break;
            case R.id.imageText4:
                if (ibt4.isChecked()) {
                    ibt4.setChecked(false);
                    ibt4.getImgView().setImageResource(R.mipmap.qita);
                    ibt4.getTextView().setTextColor(getResources().getColor(R.color.g0));
                } else {
                    tag = ibt4.getTextView().getText().toString();
                    resetAllImageBUttonText();
                    ibt4.setChecked(true);
                    ibt4.getImgView().setImageResource(R.mipmap.qita1);
                    ibt4.getTextView().setTextColor(getResources().getColor(R.color.b0));
                }
                break;
            default:
        }
    }


    public void resetAllImageBUttonText() {
        ibt1.setChecked(false);
        ibt1.getImgView().setImageResource(R.mipmap.gongzuo);
        ibt1.getTextView().setTextColor(getResources().getColor(R.color.g0));
        ibt2.setChecked(false);
        ibt2.getImgView().setImageResource(R.mipmap.shenghuo);
        ibt2.getTextView().setTextColor(getResources().getColor(R.color.g0));
        ibt3.setChecked(false);
        ibt3.getImgView().setImageResource(R.mipmap.xuexi);
        ibt3.getTextView().setTextColor(getResources().getColor(R.color.g0));
        ibt4.setChecked(false);
        ibt4.getImgView().setImageResource(R.mipmap.qita);
        ibt4.getTextView().setTextColor(getResources().getColor(R.color.g0));
    }

    //选择提醒时间点击事件
    public void startTime(View view) {
        isClicked = true;
        calendarBean = new CalendarBean();
        DateTimePickDialogUtil dateTimePickDialogUtil = new DateTimePickDialogUtil(this, "");
        dateTimePickDialogUtil.dateTimePicKDialog(startTimeTv, calendarBean);
    }

    public void xunHuan(View view) {
        new AlertDialog.Builder(this)
                .setTitle("请选择重复类型")
                .setSingleChoiceItems(types, 0,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                selectedIndex = i;
                            }
                        })
                .setNegativeButton("取消", null)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.d("qqq", "selectedIndex   " + selectedIndex);
                        if (selectedIndex == 3) showWeekDialog();
                        xunhuanTv.setText(types[selectedIndex]);
                    }
                }).show();
    }

    public void showWeekDialog() {
        selectedWeekdays = new boolean[7];
        new AlertDialog.Builder(this)
                .setTitle("请选择重复类型")
                .setMultiChoiceItems(weekdays, null,
                        new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                                selectedWeekdays[i] = b;
                            }
                        })
                .setNegativeButton("取消", null)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        xunhuanTv.setText(getXunhuanText(selectedWeekdays));
                    }
                }).show();
    }


    public String getXunhuanText(boolean[] b) {
        String a = "每周（";
        for (int i = 0; i < b.length; i++) {
            if (b[i]) a += weekdays[i];
            if (b.length > 1 && b[i]) a += "，";
        }
        a = a.substring(0, a.length() - 1);
        return a + ")";
    }


    private void save() {
        //在点击保存时再读取calendarBean中的值。
        if (calendarBean.getCalendar() == null) {
            startCalendar = DateFormatUtil.parse(mSchedule.getStartTime()).toCalendar(Locale.CHINA);
            LogUtil.d("null....");
        } else startCalendar = calendarBean.getCalendar();
        if (isTimeRight()) {
            //有闹钟删除闹钟并删除数据库中数据
            if (mSchedule.isRemind())
                AlarmManagerUtil.cancelAlarm(this, "com.loonggg.alarm.clock", mSchedule.getScheduleId());
            DataSupport.delete(Schedule.class, mSchedule.getScheduleId());
            DataSupport.deleteAll(WeekSchedule.class, "scheduleId=?", mSchedule.getScheduleId() + "");
            //添加数据
            addToDataBase();
            if (switchCompat.isChecked()) addAlarm();
            Toast.makeText(this, "save", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, MainActivity.class));
        } else {
            Toast.makeText(this, "提醒时间已过期", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean isTimeRight() {
        DateTime now = DateTime.now();
        DateTime dateTime = new DateTime(startCalendar);
        int minutes = Minutes.minutesBetween(now, dateTime).getMinutes();
        return minutes > 0;
    }


    private void addToDataBase() {
        DateTime dateTime = new DateTime(startCalendar);
        Schedule schedule = new Schedule();
        schedule.setTitle(titleEt.getText().toString());
        schedule.setRemind(switchCompat.isChecked());
        schedule.setStartTime(DateFormatUtil.format(dateTime));
        schedule.setCycleTime(xunhuanTv.getText().toString());
        schedule.setBiaoqian(tag);
        schedule.save();
        alarmId = schedule.getId();
    }

    public void addAlarm() {
        if (selectedIndex == 0) {
            AlarmManagerUtil.setAlarm(this, 0, startCalendar.get(Calendar.HOUR_OF_DAY), startCalendar.get(Calendar.MINUTE), alarmId, 0, titleEt.getText().toString(), 1);
            Toast.makeText(this, "设置成功", Toast.LENGTH_SHORT).show();
        } else if (selectedIndex == 1) {
            AlarmManagerUtil.setAlarm(this, 1, startCalendar.get(Calendar.HOUR_OF_DAY), startCalendar.get(Calendar.MINUTE), alarmId, 0, titleEt.getText().toString(), 1);
            Toast.makeText(this, "设置成功", Toast.LENGTH_SHORT).show();
        } else if (selectedIndex == 2) {
            DateTime dateTime = new DateTime(startCalendar);
            AlarmManagerUtil.setAlarm(this, 2, startCalendar.get(Calendar.HOUR_OF_DAY), startCalendar.get(Calendar.MINUTE), alarmId, dateTime.getDayOfWeek(), titleEt.getText().toString(), 1);
        } else if (selectedIndex == 3) {
            for (int i = 0; i < selectedWeekdays.length; i++) {
                if (selectedWeekdays[i]) {
                    AlarmManagerUtil.setAlarm(this, 2, startCalendar.get(Calendar.HOUR_OF_DAY), startCalendar.get(Calendar.MINUTE), alarmId, i + 1, titleEt.getText().toString(), 1);
                }
            }
        }
    }

}