package com.ppyy.demo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.ppyy.photoselector.MediaSelector;
import com.ppyy.photoselector.MimeType;
import com.ppyy.photoselector.SketchViewHolderCreator;
import com.ppyy.photoselector.bean.FileBean;
import com.ppyy.photoselector.conf.PhotoSelectorConfig;
import com.ppyy.photoselector.utils.LogUtils;
import com.ppyy.photoselector.utils.SizeUtils;
import com.ppyy.photoselector.utils.SpacesItemDecoration;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener, RadioGroup.OnCheckedChangeListener, GridImgAdapter.OnAddImageListener {
    private static final int REQUEST_CODE_CHOOSE = 11;

    @BindView(R.id.tool_bar)
    Toolbar mToolbar;

    @BindView(R.id.rv_img)
    RecyclerView mRvImg;

    @BindView(R.id.rg_theme)
    RadioGroup mRgTheme;
    /*@BindView(R.id.rb_theme_default)
    RadioButton mRbThemeDefault;
    @BindView(R.id.rb_theme_zhihu_blue)
    RadioButton mRbThemeZhihuBlue;
    @BindView(R.id.rb_theme_bilibili_pink)
    RadioButton mRbThemeBiliPink;
    @BindView(R.id.rb_theme_wumai_grey)
    RadioButton mRbThemeWumaiGrey;
    @BindView(R.id.rb_theme_wangyi_red)
    RadioButton mRbThemeWangyiRed;
    @BindView(R.id.rb_theme_today_white)
    RadioButton mRbThemeTodayWhite;
    @BindView(R.id.rb_theme_night_mode)
    RadioButton mRbThemeNightMode;*/

    @BindView(R.id.rg_type)
    RadioGroup mRgGroup;
    /*@BindView(R.id.rb_all)
    RadioButton mRbAll;
    @BindView(R.id.rb_photo)
    RadioButton mRbPhoto;
    @BindView(R.id.rb_video)
    RadioButton mRbVideo;
    @BindView(R.id.rb_audio)
    RadioButton mRbAudio;*/

    @BindView(R.id.cb_support_dark_status_bar)
    CheckBox mCbSupportDarkStatusBar;

    @BindView(R.id.btn_span_count_cut)
    Button mBtnSpanCountCut;
    @BindView(R.id.tv_span_count)
    TextView mTvSpanCount;
    @BindView(R.id.btn_span_count_add)
    Button mBtnSpanCountAdd;

    @BindView(R.id.btn_max_selectable_cut)
    Button mBtnMaxSelectableCut;
    @BindView(R.id.tv_max_selectable)
    TextView mTvMaxSelectable;
    @BindView(R.id.btn_max_selectable_add)
    Button mBtnMaxSelectableAdd;

    @BindView(R.id.cb_single_mode)
    CheckBox mCbSingleMode;

    @BindView(R.id.cb_preview_photo)
    CheckBox mCbPreviewPhoto;
    @BindView(R.id.cb_show_gif)
    CheckBox mCbShowGif;
    @BindView(R.id.cb_show_gif_flag)
    CheckBox mCbShowGifFlag;
    @BindView(R.id.cb_show_header_item)
    CheckBox mCbShowHeaderItem;
    @BindView(R.id.cb_canceled_touch_outside)
    CheckBox mCbCanceledOnTouchOutside;

    @BindView(R.id.cb_compress)
    CheckBox mCbCompress;
    @BindView(R.id.rg_compress)
    RadioGroup mRgCompress;
    @BindView(R.id.rb_compress_system)
    RadioButton mRbCompressSystem;
    @BindView(R.id.rb_compress_lu_ban)
    RadioButton mRbCompressLuBan;

    private int mChooseMode = MimeType.ALL;
    private int mThemeId = R.style.PhotoSelectorTheme;
    private ArrayList<FileBean> mSelectedItems = new ArrayList<>();

    private int mCompressMode = PhotoSelectorConfig.SYSTEM_COMPRESS_MODE;
    private GridImgAdapter mGridImgAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);

        mRvImg.setLayoutManager(new GridLayoutManager(this, 3));
        mRvImg.addItemDecoration(new SpacesItemDecoration(SizeUtils.dp2px(this, 3), 3));
        mGridImgAdapter = new GridImgAdapter(this, null);
        mRvImg.setAdapter(mGridImgAdapter);
        mGridImgAdapter.setMaxSelectable(getMaxSelectable());

        mGridImgAdapter.setOnAddImageListener(this);

        mBtnMaxSelectableAdd.setOnClickListener(this);
        mBtnMaxSelectableCut.setOnClickListener(this);
        mBtnSpanCountAdd.setOnClickListener(this);
        mBtnSpanCountCut.setOnClickListener(this);
        mRgGroup.setOnCheckedChangeListener(this);
        mRgTheme.setOnCheckedChangeListener(this);
        mRgCompress.setOnCheckedChangeListener(this);

        mCbSingleMode.setOnCheckedChangeListener(this);
        mCbPreviewPhoto.setOnCheckedChangeListener(this);
        mCbShowGif.setOnCheckedChangeListener(this);
        mCbCompress.setOnCheckedChangeListener(this);
    }

    @Override
    public void onClick(View view) {
        int maxSelectable, spanCount;
        switch (view.getId()) {
            case R.id.btn_max_selectable_add:
                maxSelectable = getMaxSelectable();
                if (maxSelectable < 15) {
                    mTvMaxSelectable.setText(String.valueOf(maxSelectable + 1));
                }
                break;
            case R.id.btn_max_selectable_cut:
                maxSelectable = getMaxSelectable();
                if (maxSelectable > 1) {
                    mTvMaxSelectable.setText(String.valueOf(maxSelectable - 1));
                }
                break;
            case R.id.btn_span_count_add:
                spanCount = getSpanCount();
                if (spanCount < 8) {
                    mTvSpanCount.setText(String.valueOf(spanCount + 1));
                }
                break;
            case R.id.btn_span_count_cut:
                spanCount = getSpanCount();
                if (spanCount > 2) {
                    mTvSpanCount.setText(String.valueOf(spanCount - 1));
                }
                break;
        }
    }

    private int getMaxSelectable() {
        return Integer.parseInt(mTvMaxSelectable.getText().toString());
    }

    private int getSpanCount() {
        return Integer.parseInt(mTvSpanCount.getText().toString());
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
        switch (checkedId) {
            case R.id.rb_all:
                mChooseMode = MimeType.ALL;
                break;
            case R.id.rb_photo:
                mChooseMode = MimeType.PHOTO;
                break;
            case R.id.rb_video:
                mChooseMode = MimeType.VIDEO;
                break;
            case R.id.rb_audio:
                mChooseMode = MimeType.AUDIO;
                break;
            case R.id.rb_theme_default:
                mThemeId = R.style.PhotoSelectorTheme;
                mCbSupportDarkStatusBar.setChecked(false);
                break;
            case R.id.rb_theme_bilibili_pink:
                mThemeId = R.style.PhotoSelectorTheme_BiliBiliPink;
                mCbSupportDarkStatusBar.setChecked(false);
                break;
            case R.id.rb_theme_wangyi_red:
                mThemeId = R.style.PhotoSelectorTheme_WangYiRed;
                mCbSupportDarkStatusBar.setChecked(false);
                break;
            case R.id.rb_theme_wumai_grey:
                mThemeId = R.style.PhotoSelectorTheme_WuMaiGrey;
                mCbSupportDarkStatusBar.setChecked(false);
                break;
            case R.id.rb_theme_today_white:
                mThemeId = R.style.PhotoSelectorTheme_TodayWhite;
                if (!mCbSupportDarkStatusBar.isChecked()) mCbSupportDarkStatusBar.setChecked(true);
                break;
            case R.id.rb_theme_night_mode:
                mThemeId = R.style.PhotoSelectorTheme_Night;
                mCbSupportDarkStatusBar.setChecked(false);
                break;
            case R.id.rb_compress_system:
                mCompressMode = PhotoSelectorConfig.SYSTEM_COMPRESS_MODE;
                break;
            case R.id.rb_compress_lu_ban:
                mCompressMode = PhotoSelectorConfig.LU_BAN_COMPRESS_MODE;
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        switch (compoundButton.getId()) {
            case R.id.cb_show_gif:
                mCbShowGifFlag.setEnabled(isChecked);
                break;
            case R.id.cb_single_mode:
                mBtnMaxSelectableCut.setEnabled(!isChecked);
                mBtnMaxSelectableAdd.setEnabled(!isChecked);
                mTvMaxSelectable.setText(isChecked ? "1" : mTvMaxSelectable.getText());
                break;
            case R.id.cb_compress:
                mRgCompress.setVisibility(isChecked ? View.VISIBLE : View.GONE);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK)
            return;
        if (requestCode == REQUEST_CODE_CHOOSE) {
            mSelectedItems.clear();
            mSelectedItems.addAll(data.getParcelableArrayListExtra(PhotoSelectorConfig.EXTRA_RESULT_SELECTED_ITEMS));
            for (FileBean item : mSelectedItems) {
                LogUtils.e("selected item path : " + item.getPath());
                String compressPath = item.getCompressPath();
                if (!TextUtils.isEmpty(compressPath)) {
                    LogUtils.e("压缩之前的大小 : " + Formatter.formatFileSize(this, new File(item.getPath()).length()));
                    LogUtils.e("selected item compress path : " + compressPath);
                    LogUtils.e("压缩之后的大小 : " + Formatter.formatFileSize(this, new File(compressPath).length()));
                }
            }
            mGridImgAdapter.setDataList(mSelectedItems);
        }
    }

    @Override
    public void onAddImage() {
        if (mSelectedItems != null)
            LogUtils.e("mSelectedItems size : " + mSelectedItems.size());
        mGridImgAdapter.setMaxSelectable(getMaxSelectable());
        MediaSelector.from(this)
                .choose(mChooseMode)
                .themeId(mThemeId)
                .supportDarkStatusBar(mCbSupportDarkStatusBar.isChecked())
                .maxSelectable(getMaxSelectable())
                .gridSize(getSpanCount())
                .previewPhoto(mCbPreviewPhoto.isChecked())
                .showGif(mCbShowGif.isChecked())
                .showGifFlag(mCbShowGifFlag.isChecked() && mCbShowGifFlag.isEnabled())
                // .setGifFlagResId(R.drawable.ic_gif_custom)
                .showHeaderItem(mCbShowHeaderItem.isChecked())
                .setCanceledOnTouchOutside(mCbCanceledOnTouchOutside.isChecked())
                .customViewHolder(new SketchViewHolderCreator())
                .selectedItems(mSelectedItems)
                .compress(mCbCompress.isChecked())
                .compressMode(mCompressMode)
                .forResult(REQUEST_CODE_CHOOSE);
    }
}
