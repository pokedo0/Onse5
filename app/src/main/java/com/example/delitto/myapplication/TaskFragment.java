package com.example.delitto.myapplication;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.example.delitto.myapplication.util.DisplayUtil;

import de.hdodenhof.circleimageview.CircleImageView;
import fr.ganfra.materialspinner.MaterialSpinner;

/**
 * Created by pokedo on 2016/11/18.
 */

public class TaskFragment extends Fragment implements View.OnClickListener {
    private MaterialSpinner spinner;
    private ArrayAdapter<String> adapter;
    private EditText publicEdittext;
    private EditText privateEdittext;
    private EditText inputphone;
    private BottomNavigationBar bottomNavigationBar;
    private CircleImageView expressCircleImage;
    private CircleImageView takeoutCircleImage;
    private CircleImageView otherCircleImage;
    private Context mContext;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.task_fragment, container, false);

        publicEdittext = (EditText) view.findViewById(R.id.public_edittext);
        privateEdittext = (EditText) view.findViewById(R.id.private_edittext);
        inputphone = (EditText) view.findViewById(R.id.input_contact_phone);
        bottomNavigationBar = (BottomNavigationBar) getActivity().findViewById(R.id.bottom_navigation_bar);
        expressCircleImage = (CircleImageView) view.findViewById(R.id.express_logo);
        takeoutCircleImage = (CircleImageView) view.findViewById(R.id.takeout_logo);
        otherCircleImage = (CircleImageView) view.findViewById(R.id.other_logo);

        mContext = TaskFragment.this.getContext();

        //设置下拉选择框的数据
        spinner = (MaterialSpinner) view.findViewById(R.id.spinner);
        String[] ITEMS = {"0.5h", "1h", "1.5h", "2h", "2.5h", "3h", "3.5h", "4h"};
        adapter = new ArrayAdapter<String>(this.getContext(), android.R.layout.simple_spinner_item, ITEMS);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        //Edittext设置焦点监听,获取焦点时候隐藏底栏,否则隐藏输入键盘
        hideWidget();
        //解决外层Scrollview和Edittext滚动冲突问题
        scrollConflict();

        //监听发布任务类型 选择的图标
        expressCircleImage.setOnClickListener(this);
        takeoutCircleImage.setOnClickListener(this);
        otherCircleImage.setOnClickListener(this);

        return view;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.item_publish){
            AlertDialog.Builder _dialog = new AlertDialog.Builder(mContext);
            _dialog.setTitle("确定");
            _dialog.setMessage("发布任务之后不可取消，是否确认？");
            _dialog.setCancelable(false);
            _dialog.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface _dialog, int which) {
                    final ProgressDialog _progressDialog = new ProgressDialog(mContext);   //弹出进度条
                    _progressDialog.setTitle("正在确认发布");
                    _progressDialog.setMessage("请稍后..");
                    _progressDialog.show();

                    //3秒后返回首页
                    new android.os.Handler().postDelayed(
                            new Runnable() {
                                @Override
                                public void run() {
                                    _progressDialog.dismiss();
                                }
                            }, 3000);
                }
            });
            _dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface _dialog, int which) {
                }
            });
            _dialog.show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean verify() {
        if (spinner.getSelectedItem().equals(spinner.getHint())) {
            spinner.setError("请选择该下拉栏");
            return false;
        }
        if (publicEdittext.getText().length() < 5) {
            publicEdittext.setError("字符长度必须大于5");
            return false;
        }
        if (publicEdittext.getText().length() < 5) {
            privateEdittext.setError("字符长度必须大于5");
            return false;
        }
        if (inputphone.getText().length() != 11) {
            inputphone.setError("手机号码长度为11位");
            return false;
        }
        if (expressCircleImage.getBorderColor() == R.color.border_color && otherCircleImage.getBorderColor()
                == R.color.border_color && takeoutCircleImage.getBorderColor() == R.color.border_color) {
            Toast.makeText(mContext, "请选择任务类型!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @Override
    public void onClick(View view) {
        if (view instanceof CircleImageView) {
            changeStyle(view);
        }
    }

    //改变Logo的Style，每次click重新设置三个logo的属性
    private void changeStyle(View view) {
        CircleImageView cirview = (CircleImageView) view;
        switch (view.getId()) {
            case R.id.express_logo:
                setSelectedStyle(expressCircleImage);
                setUnselectedStyle(takeoutCircleImage);
                setUnselectedStyle(otherCircleImage);
                break;
            case R.id.takeout_logo:
                setSelectedStyle(takeoutCircleImage);
                setUnselectedStyle(expressCircleImage);
                setUnselectedStyle(otherCircleImage);
                break;
            case R.id.other_logo:
                setSelectedStyle(otherCircleImage);
                setUnselectedStyle(takeoutCircleImage);
                setUnselectedStyle(expressCircleImage);
                break;
        }
    }

    //选择logo时的style
    private void setSelectedStyle(CircleImageView cirview) {
        cirview.setBorderColor(getResources().getColor(R.color.primary_darker));
        cirview.setBorderWidth(DisplayUtil.dip2px(this, 2));
    }

    //未选择logo时的style
    private void setUnselectedStyle(CircleImageView cirview) {
        cirview.setBorderColor(getResources().getColor(R.color.border_color));
        cirview.setBorderWidth(DisplayUtil.dip2px(this, 1));
    }

    //解决外层Scrollview和Edittext滚动冲突问题
    protected void scrollConflict() {
        publicEdittext.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (v.getId() == R.id.public_edittext) {
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                    switch (event.getAction() & MotionEvent.ACTION_MASK) {
                        case MotionEvent.ACTION_UP:
                            v.getParent().requestDisallowInterceptTouchEvent(false);
                            break;
                    }
                }
                return false;
            }
        });

        privateEdittext.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (v.getId() == R.id.private_edittext) {
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                    switch (event.getAction() & MotionEvent.ACTION_MASK) {
                        case MotionEvent.ACTION_UP:
                            v.getParent().requestDisallowInterceptTouchEvent(false);
                            break;
                    }
                }
                return false;
            }
        });
    }

    //Edittext设置焦点监听,获取焦点时候隐藏底栏,否则隐藏输入键盘
    protected void hideWidget() {
        publicEdittext.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    bottomNavigationBar.hide();
                    Log.d("~focus", bottomNavigationBar.isHidden() + "");
                } else {
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    bottomNavigationBar.show();
                }
            }
        });
        privateEdittext.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    bottomNavigationBar.hide();
                    Log.d("~focus", bottomNavigationBar.isHidden() + "");
                } else {
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    bottomNavigationBar.show();
                }
            }
        });

        inputphone.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    bottomNavigationBar.hide();
                    Log.d("~focus", bottomNavigationBar.isHidden() + "");
                } else {
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    bottomNavigationBar.show();
                }
            }
        });
    }
}
