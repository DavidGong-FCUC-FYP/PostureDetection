package com.posturedetection.android;

import static android.provider.MediaStore.EXTRA_OUTPUT;

import android.Manifest;
import android.app.ActionBar;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.os.LocaleListCompat;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.bigkoo.pickerview.view.TimePickerView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.posturedetection.android.data.LoginUser;
import com.posturedetection.android.data.model.AccountSettings;
import com.posturedetection.android.util.ActivityCollector;
import com.posturedetection.android.util.CityBean;
import com.posturedetection.android.util.PhotoUtils;
import com.posturedetection.android.util.ProvinceBean;
import com.posturedetection.android.util.ToastUtils;
import com.posturedetection.android.widget.ItemGroup;
import com.posturedetection.android.widget.RoundImageView;
import com.posturedetection.android.widget.TitleLayout;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;




public class PersonalInformationActivity extends AppCompatActivity implements View.OnClickListener{

    private ItemGroup ig_id,ig_name,ig_email,ig_phone,ig_gender,ig_region,ig_brithday;
    private LoginUser loginUser = LoginUser.getInstance();
    private LinearLayout ll_portrait;
    private ToastUtils mToast = new ToastUtils();

    private ArrayList<String> optionsItems_gender = new ArrayList<>();
    private ArrayList<ProvinceBean> options1Items = new ArrayList<>();
    private ArrayList<ArrayList<String>> options2Items = new ArrayList<>();

    private OptionsPickerView pvOptions;

    private RoundImageView ri_portrati;
    private Uri imageUrl;  //拍照功能的地址
    private static final int TAKE_PHOTO = 1;
    private static final int FROM_ALBUMS = 2;
    private PopupWindow popupWindow;
    private String imagePath;  //从相册中选的地址
    private PhotoUtils photoUtils = new PhotoUtils();

    private static final int EDIT_NAME = 3;
    private TitleLayout titleLayout;

    private Intent intent;

    private SharedPreferences sp;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_persional_information);
//        sp = getSharedPreferences("account_settings", MODE_PRIVATE);
//        String account_settings_json = sp.getString("account_settings", null);
//        setAppLocaleFromJson(account_settings_json);

        ActivityCollector.addActivity(this);

        initOptionData();

        ig_id = (ItemGroup)findViewById(R.id.ig_id);
        ig_name = (ItemGroup)findViewById(R.id.ig_name);
        ig_email = (ItemGroup)findViewById(R.id.ig_email);
        ig_phone = (ItemGroup)findViewById(R.id.ig_phone);
        ig_gender = (ItemGroup)findViewById(R.id.ig_gender);
        ig_region = (ItemGroup)findViewById(R.id.ig_region);
        ig_brithday = (ItemGroup)findViewById(R.id.ig_birthday);
        ll_portrait = (LinearLayout)findViewById(R.id.ll_portrait);
        ri_portrati = (RoundImageView)findViewById(R.id.ri_portrait);
        titleLayout = (TitleLayout)findViewById(R.id.tl_title);

        ig_name.setOnClickListener(this);
        ig_email.setOnClickListener(this);
        ig_phone.setOnClickListener(this);
        ig_gender.setOnClickListener(this);
        ig_region.setOnClickListener(this);
        ig_brithday.setOnClickListener(this);
        ll_portrait.setOnClickListener(this);



        titleLayout.getIv_backward().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //设置点击保存的逻辑
        titleLayout.getIv_save().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser.update();
                setResult(RESULT_OK);
                String sv = getString(R.string.save_success);
                mToast.showShort(PersonalInformationActivity.this,sv);
               //back to previous activity
                //onDestroy();
                finish();
            }
        });

        initInfo();

    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        //如果是退出则loginUser的数据重新初始化（也就是不保存数据库）
       // loginUser.reinit();
        ActivityCollector.removeActivity(this);
    }

    public void onClick(View v){
        switch (v.getId()){
            //点击修改地区逻辑
            case R.id.ig_region:
                pvOptions = new OptionsPickerBuilder(this, new OnOptionsSelectListener() {
                    @Override
                    public void onOptionsSelect(int options1, int options2, int options3, View v) {
                        //选择了则显示并暂存LoginUser，退出时在保存至数据库
                        String tx = options1Items.get(options1).getPickerViewText()
                                + options2Items.get(options1).get(options2);
                        ig_region.getContentEdt().setText(tx);
                        loginUser.setRegion(tx);
                    }
                }).setCancelColor(Color.GRAY).build();
                pvOptions.setPicker(options1Items, options2Items);//二级选择器
                pvOptions.show();
                break;

            //点击修改性别逻辑
            case R.id.ig_gender:
                //性别选择器
                pvOptions = new OptionsPickerBuilder(PersonalInformationActivity.this, new OnOptionsSelectListener() {
                    @Override
                    public void onOptionsSelect(int options1, int option2, int options3 ,View v) {
                        //选择了则显示并暂存LoginUser，退出时在保存至数据库
                        String tx = optionsItems_gender.get(options1);
                        ig_gender.getContentEdt().setText(tx);
                        loginUser.setGender(tx);
                    }
                }).setCancelColor(Color.GRAY).build();
                pvOptions.setPicker(optionsItems_gender);
                pvOptions.show();
                break;

            //点击修改生日逻辑
            case R.id.ig_birthday:
                //时间选择器
                //修改打开的默认时间，如果选择过则是选择过的时间，否则是系统默认时间
                Calendar selectedDate = Calendar.getInstance();
                if (loginUser.getBirthday() != null){
                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
                        selectedDate.setTime(sdf.parse(loginUser.getBirthday()));
                    }catch (ParseException e){
                        e.printStackTrace();
                    }
                }
                //初始化picker并show
                TimePickerView pvTime = new TimePickerBuilder(PersonalInformationActivity.this, new OnTimeSelectListener() {
                    @Override
                    public void onTimeSelect(Date date, View v) {
                        //选择了则显示并暂存LoginUser，退出时在保存至数据库
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
                        ig_brithday.getContentEdt().setText(sdf.format(date));
                        loginUser.setBirthday(sdf.format(date));
                    }
                }).setDate(selectedDate).setCancelColor(Color.GRAY).build();
                pvTime.show();
                break;
            //点击修改头像的逻辑
            case R.id.ll_portrait:
                //展示选择框，并设置选择框的监听器
                show_popup_windows();
                break;
            //点击修改名字的逻辑
            case R.id.ig_name:
                intent  = new Intent(PersonalInformationActivity.this, EditItemActivity.class);
                intent.putExtra("title","Name");
                startActivity(intent);
                break;
            case R.id.ig_email:
                intent  = new Intent(PersonalInformationActivity.this, EditItemActivity.class);
                intent.putExtra("title","Email");
                startActivity(intent);
                break;
            case R.id.ig_phone:
                intent  = new Intent(PersonalInformationActivity.this, EditItemActivity.class);
                intent.putExtra("title","Phone");
                startActivity(intent);
                break;
            default:
                break;
        }
    }
    //处理拍摄照片回调
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode,data);
        switch (requestCode){
            //拍照得到图片
            case TAKE_PHOTO:
                if(resultCode == RESULT_OK){
                    try {
                        //将拍摄的图片展示并更新数据库
                        Bitmap bitmap = BitmapFactory.decodeStream((getContentResolver().openInputStream(imageUrl)));
                        ri_portrati.setImageBitmap(bitmap);
                        loginUser.setPortrait(photoUtils.bitmap2byte(bitmap));
                    }catch (FileNotFoundException e){
                        e.printStackTrace();
                    }
                }
                break;
            //从相册中选择图片
            case FROM_ALBUMS:
                if(resultCode == RESULT_OK){
                    //判断手机版本号
                    if(Build.VERSION.SDK_INT >= 19){
                        imagePath =  photoUtils.handleImageOnKitKat(this, data);
                    }else {
                        imagePath = photoUtils.handleImageBeforeKitKat(this, data);
                    }
                }
                if(imagePath != null){
                    //将拍摄的图片展示并更新数据库
                    Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
                    ri_portrati.setImageBitmap(bitmap);
                    loginUser.setPortrait(photoUtils.bitmap2byte(bitmap));
                }else{
                    Log.d("Posture Detection","Not get image");
                }
                break;
            //如果是编辑名字，则修改展示
            case EDIT_NAME:
                if(resultCode == RESULT_OK){
                    ig_name.getContentEdt().setText(loginUser.getName());
                }
                break;
            default:
                break;
        }
    }
    //从数据库中初始化数据并展示
    private void initInfo(){
        LoginUser loginUser = LoginUser.getInstance();
        ig_id.getContentEdt().setText(String.valueOf(loginUser.getId()));  //ID是int，转string
        ig_name.getContentEdt().setText(loginUser.getName());
        ig_email.getContentEdt().setText(loginUser.getEmail());
        ig_phone.getContentEdt().setText(loginUser.getPhone());
        ri_portrati.setImageBitmap(photoUtils.byte2bitmap(loginUser.getPortrait()));
        ig_gender.getContentEdt().setText(loginUser.getGender());
        ig_region.getContentEdt().setText(loginUser.getRegion());
        ig_brithday.getContentEdt().setText(loginUser.getBirthday());
    }

    //初始化性别、地址和生日的数据
    private void initOptionData(){
        //性别选择器数据
        optionsItems_gender.add(new String("Other"));
        optionsItems_gender.add(new String("Male"));
        optionsItems_gender.add(new String("Female"));

        //地址选择器数据
        String province_data = readJsonFile("province.json");
        String city_data = readJsonFile("city.json");

        Gson gson = new Gson();

        options1Items = gson.fromJson(province_data, new TypeToken<ArrayList<ProvinceBean>>(){}.getType());
        ArrayList<CityBean> cityBean_data = gson.fromJson(city_data, new TypeToken<ArrayList<CityBean>>(){}.getType());
        for(ProvinceBean provinceBean:options1Items){
            ArrayList<String> temp = new ArrayList<>();
            for (CityBean cityBean : cityBean_data){
                if(provinceBean.getProvince().equals(cityBean.getProvince())){
                    temp.add(cityBean.getName());
                }
            }
            options2Items.add(temp);
        }

    }

    //传入：asset文件夹中json文件名
    //返回：读取的String
    private String readJsonFile(String file){
        StringBuilder newstringBuilder = new StringBuilder();
        try {
            InputStream inputStream = getResources().getAssets().open(file);

            InputStreamReader isr = new InputStreamReader(inputStream);

            BufferedReader reader = new BufferedReader(isr);

            String jsonLine;
            while ((jsonLine = reader.readLine()) != null) {
                newstringBuilder.append(jsonLine);
            }
            reader.close();
            isr.close();
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String data =  newstringBuilder.toString();
        return data;
    }

    //展示修改头像的选择框，并设置选择框的监听器
    private void show_popup_windows(){
        RelativeLayout layout_photo_selected = (RelativeLayout) getLayoutInflater().inflate(R.layout.layout_photo_select,null);
        if(popupWindow==null){
            popupWindow = new PopupWindow(layout_photo_selected, ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT, true);
        }
        //显示popupwindows
        popupWindow.showAtLocation(layout_photo_selected, Gravity.CENTER, 0, 0);
        //设置监听器
        TextView take_photo =  (TextView) layout_photo_selected.findViewById(R.id.take_photo);
        TextView from_albums = (TextView)  layout_photo_selected.findViewById(R.id.from_albums);
        LinearLayout cancel = (LinearLayout) layout_photo_selected.findViewById(R.id.cancel);
        //拍照按钮监听
        take_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(popupWindow != null && popupWindow.isShowing()) {
                    imageUrl = photoUtils.take_photo_util(PersonalInformationActivity.this, "com.posturedetection.android.fileprovider", "output_image.jpg");
                    //调用相机，拍摄结果会存到imageUri也就是outputImage中
                    Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                    intent.putExtra(EXTRA_OUTPUT, imageUrl);
                    startActivityIfNeeded(intent, TAKE_PHOTO);
                    //去除选择框
                    popupWindow.dismiss();
                }
            }
        });
        //相册按钮监听
        from_albums.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //申请权限
                if(ContextCompat.checkSelfPermission(PersonalInformationActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(PersonalInformationActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
                }else {
                    //打开相册
                    Intent intent = new Intent("android.intent.action.GET_CONTENT");
                    intent.setType("image/*");
                    startActivity(intent);
                }
                //去除选择框
                popupWindow.dismiss();
            }
        });
        //取消按钮监听
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (popupWindow != null && popupWindow.isShowing()) {
                    popupWindow.dismiss();
                }
            }
        });
    }


    private void setAppLocaleFromJson(String accountSettingsJson) {
        if (accountSettingsJson != null) {
            Gson gson = new Gson();
            AccountSettings accountSettings = gson.fromJson(accountSettingsJson, AccountSettings.class);
            if (accountSettings != null) {
                String language = "en";
                switch (accountSettings.getLanguage()) {
                    case 0:
                        language = "en";
                        break;
                    case 1:
                        language = "zh";
                        break;
                    case 2:
                        language = "ms";
                        break;
                }

                LocaleListCompat locales = LocaleListCompat.forLanguageTags(language);
                AppCompatDelegate.setApplicationLocales(locales);
            }
        }
    }
}
