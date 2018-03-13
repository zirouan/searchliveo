package br.com.liveo.searchliveo;

/*
 * Copyright 2016 Rudson Lima
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.animation.Animator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.speech.RecognizerIntent;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class SearchLiveo extends FrameLayout {

    private Activity mContext;
    private EditText mEdtSearch;

    private ImageView mImgArrow;
    private ImageView mImgVoice;
    private ImageView mImgClose;

    private RelativeLayout mViewSearch;

    private int mColorPrimaryDark;
    private boolean active = false;
    private boolean isVoice = true;

    private int mColorIcon = -1;
    private int mAfterCharacter = 0;
    private int mColorIconArrow = -1;
    private int mColorIconVoice = -1;
    private int mColorIconClose = -1;

    private int mStatusBarHideColor = -1;
    private int mStatusBarShowColor = -1;
    private OnSearchListener mSearchListener;

    private Timer mTimer;
    private int mSearchDelay = 800;

    public static int REQUEST_CODE_SPEECH_INPUT = 7777;

    private static String SEARCH_TEXT = "searchText";
    private static String STATE_TO_SAVE = "stateToSave";
    private static String INSTANCE_STATE = "instanceState";


    /**
     * Start context and the listener Search Live library.
     * Use this method when you are using an Activity
     *
     * @param context - Context Activity
     */
    public SearchLiveo with(Context context) {

        if (this.mContext == null) {
            try {
                this.mContext = (Activity) context;
                this.mSearchListener = (OnSearchListener) context;
            } catch (ClassCastException ignored) {

            }
        } else {
            build();
        }

        return this;
    }

    /**
     * Start context and the listener Search Live library.
     * Use this method when you are using an Fragment
     *
     * @param getActivity - Context Fragment
     * @param context     - Listener
     */
    public SearchLiveo with(Activity getActivity, OnSearchListener context) {

        if (this.mContext == null) {
            try {
                this.mContext = getActivity;
                this.mSearchListener = context;
            } catch (ClassCastException ignored) {
            }
        } else {
            build();
        }

        return this;
    }

    public void build() {

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Resources.Theme theme = this.mContext.getTheme();
                TypedArray typedArray = theme.obtainStyledAttributes(
                        new int[]{android.R.attr.colorPrimaryDark});

                setColorPrimaryDark(typedArray.getResourceId(0, 0));
            }
        } catch (Exception e) {
            e.getStackTrace();
        }
    }

    public SearchLiveo(Context context) {
        this(context, null);
    }

    public SearchLiveo(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SearchLiveo(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (!isInEditMode()) {
            init(context);
            initAttribute(context, attrs, defStyleAttr);
        }
    }

    private void init(Context context) {
        View view = LayoutInflater.from(context).inflate(
                R.layout.search_liveo, this, true);

        setEdtSearch((EditText) view.findViewById(R.id.edt_search));

        mImgArrow = view.findViewById(R.id.img_arrow);
        mImgVoice = view.findViewById(R.id.img_voice);
        mImgClose = view.findViewById(R.id.img_close);

        mImgClose.setVisibility(isVoice() ? View.GONE : View.VISIBLE);

        mViewSearch = view.findViewById(R.id.view_search);
        mViewSearch.setVisibility(View.INVISIBLE);

        getEdtSearch().setOnKeyListener(onKeyListener);

        mImgArrow.setOnClickListener(onClickSearchArrow);
        mImgVoice.setOnClickListener(onClickVoiceSearch);
        mImgClose.setOnClickListener(onClickCloseSearch);

        getEdtSearch().setOnEditorActionListener(onEditorActionListener);
        getEdtSearch().addTextChangedListener(new OnTextWatcherEdtSearch());
    }

    private void initAttribute(Context context, AttributeSet attributeSet, int defStyleAttr) {
        TypedArray attr = context.obtainStyledAttributes(attributeSet,
                R.styleable.SearchLiveo, defStyleAttr, 0);
        if (attr != null) {
            try {

                if (attr.hasValue(R.styleable.SearchLiveo_searchLiveoHint)) {
                    hint(attr.getString(R.styleable.SearchLiveo_searchLiveoHint));
                }

                if (attr.hasValue(R.styleable.SearchLiveo_searchLiveoTextColor)) {
                    getEdtSearch().setTextColor(attr.getColor(
                            R.styleable.SearchLiveo_searchLiveoTextColor, -1));
                }

                if (attr.hasValue(R.styleable.SearchLiveo_searchLiveoHintColor)) {
                    getEdtSearch().setHintTextColor(attr.getColor(
                            R.styleable.SearchLiveo_searchLiveoHintColor, -1));
                }

                if (attr.hasValue(R.styleable.SearchLiveo_searchLiveoColorIcon)) {
                    setColorIcon(attr.getColor(
                            R.styleable.SearchLiveo_searchLiveoColorIcon, -1));
                }

                if (attr.hasValue(R.styleable.SearchLiveo_searchLiveoColorArrow)) {
                    setColorIconArrow(attr.getColor(
                            R.styleable.SearchLiveo_searchLiveoColorArrow, -1));
                }

                if (attr.hasValue(R.styleable.SearchLiveo_searchLiveoColorVoice)) {
                    setColorIconVoice(attr.getColor(
                            R.styleable.SearchLiveo_searchLiveoColorVoice, -1));
                }

                if (attr.hasValue(R.styleable.SearchLiveo_searchLiveoColorClose)) {
                    setColorIconClose(attr.getColor(
                            R.styleable.SearchLiveo_searchLiveoColorClose, -1));
                }

                if (attr.hasValue(R.styleable.SearchLiveo_searchLiveoBackground)) {
                    mViewSearch.setBackgroundColor(attr.getColor(
                            R.styleable.SearchLiveo_searchLiveoBackground, -1));
                }

                if (attr.hasValue(R.styleable.SearchLiveo_searchLiveoStatusBarShowColor)) {
                    setStatusBarShowColor(attr.getColor(
                            R.styleable.SearchLiveo_searchLiveoStatusBarShowColor, -1));
                }

                if (attr.hasValue(R.styleable.SearchLiveo_searchLiveoStatusBarHideColor)) {
                    setStatusBarHideColor(attr.getColor(
                            R.styleable.SearchLiveo_searchLiveoStatusBarHideColor, -1));
                }
            } finally {
                attr.recycle();
            }
        }
    }

    /**
     * Time in milliseconds of delay. Only after the given time will the search be made.
     *
     * @param delay default 800
     */
    public SearchLiveo searchDelay(int delay) {
        this.mSearchDelay = delay;
        return this;
    }


    /**
     * Set a new background color. If you do not use this method and standard color is white SearchLiveo.
     * In his layout.xml you can use the "app:searchLiveoBackground="@color/..."" attribute
     *
     * @param resId color attribute - colors.xml file
     */
    public SearchLiveo backgroundResource(int resId) {
        mViewSearch.setBackgroundResource(resId);
        return this;
    }

    /**
     * Set a new background color. If you do not use this method and standard color is white SearchLiveo.
     * In his layout.xml you can use the "app:searchLiveoBackground="@color/..."" attribute
     *
     * @param color color attribute - colors.xml file
     */
    public SearchLiveo backgroundColor(int color) {
        mViewSearch.setBackgroundColor(ContextCompat.getColor(mContext, color));
        return this;
    }

    /**
     * Set a new text color.
     * In his layout.xml you can use the "app:searchLiveoTextColor="@color/..."" attribute
     *
     * @param color color attribute - colors.xml file
     */
    public SearchLiveo textColor(int color) {
        getEdtSearch().setTextColor(ContextCompat.getColor(mContext, color));
        return this;
    }

    /**
     * Set a new hint color.
     * In his layout.xml you can use the "app:searchLiveoHintColor="@color/..."" attribute
     *
     * @param color color attribute - colors.xml file
     */
    public SearchLiveo hintColor(int color) {
        getEdtSearch().setHintTextColor(ContextCompat.getColor(mContext, color));
        return this;
    }

    /**
     * Set a new text.
     *
     * @param text "valeu"
     */
    public SearchLiveo text(String text) {
        getEdtSearch().setText(text);
        return this;
    }


    /**
     * Set a new hint.
     * In his layout.xml you can use the "app:searchLiveoHint="value"" attribute
     *
     * @param text "valeu"
     */
    public SearchLiveo hint(String text) {
        getEdtSearch().setHint(text);
        return this;
    }

    /**
     * Set a new text.
     *
     * @param text string attribute - string.xml file
     */
    public SearchLiveo text(int text) {
        getEdtSearch().setText(mContext.getString(text));
        return this;
    }

    /**
     * Set a new hint.
     * In his layout.xml you can use the "app:searchLiveoHint="@string/..."" attribute
     *
     * @param text string attribute - string.xml file
     */
    public SearchLiveo hint(int text) {
        getEdtSearch().setHint(mContext.getString(text));
        return this;
    }

    /**
     * Set a new color for all icons (arrow, voice and close).
     * In his layout.xml you can use the "app:searchLiveoColorIcon="@color/..."" attribute
     *
     * @param color color attribute - colors.xml file
     */
    public SearchLiveo colorIcon(int color) {
        this.setColorIcon(ContextCompat.getColor(mContext, color));
        return this;
    }

    /**
     * Set a new color for back arrow
     * In his layout.xml you can use the "app:searchLiveoColorArrow="@color/..."" attribute
     *
     * @param color color attribute - colors.xml file
     */
    public SearchLiveo colorIconArrow(int color) {
        this.setColorIconArrow(ContextCompat.getColor(mContext, color));
        return this;
    }

    /**
     * Set a new color for voice
     * In his layout.xml you can use the "app:searchLiveoColorVoice="@color/..."" attribute
     *
     * @param color color attribute - colors.xml file
     */
    public SearchLiveo colorIconVoice(int color) {
        this.setColorIconVoice(ContextCompat.getColor(mContext, color));
        return this;
    }

    /**
     * Set a new color for close
     * In his layout.xml you can use the "app:searchLiveoColorClose="@color/..."" attribute
     *
     * @param color color attribute - colors.xml file
     */
    public SearchLiveo colorIconClose(int color) {
        this.setColorIconClose(ContextCompat.getColor(mContext, color));
        return this;
    }

    /**
     * Set a new color for statusBar when the SearchLiveo is closed
     * In his layout.xml you can use the "app:searchLiveoStatusBarHideColor="@color/..."" attribute
     *
     * @param color color attribute - colors.xml file
     */
    public SearchLiveo statusBarHideColor(int color) {
        setStatusBarHideColor(ContextCompat.getColor(mContext, color));
        return this;
    }

    /**
     * Set a new color for statusBar when the SearchLiveo for visible
     * In his layout.xml you can use the "app:searchLiveoStatusBarShowColor="@color/..."" attribute
     *
     * @param color color attribute - colors.xml file
     */
    public SearchLiveo statusBarShowColor(int color) {
        setStatusBarShowColor(ContextCompat.getColor(mContext, color));
        return this;
    }

    /**
     * Hide voice icon
     */
    public SearchLiveo hideVoice() {
        setIsVoice(false);
        mImgVoice.setVisibility(View.GONE);
        return this;
    }

    /**
     * Show voice icon
     */
    public SearchLiveo showVoice() {
        setIsVoice(true);
        mImgVoice.setVisibility(View.VISIBLE);
        return this;
    }

    private void colorIcon() {
        if (getColorIcon() != -1 && getColorIconArrow() == -1) {
            mImgArrow.setColorFilter(this.getColorIcon());
        }

        if (getColorIcon() != -1 && getColorIconVoice() == -1) {
            mImgVoice.setColorFilter(this.getColorIcon());
        }

        if (getColorIcon() != -1 && getColorIconClose() == -1) {
            mImgClose.setColorFilter(this.getColorIcon());
        }
    }

    private void colorIconArrow() {
        if (getColorIconArrow() != -1) {
            mImgArrow.setColorFilter(this.getColorIconArrow());
        }
    }

    private void colorIconVoice() {
        if (this.getColorIconVoice() != -1) {
            mImgVoice.setColorFilter(this.getColorIconVoice());
        } else {
            mImgVoice.clearColorFilter();
        }
    }

    private void colorIconClose() {
        if (this.getColorIconClose() != -1) {
            mImgClose.setColorFilter(this.getColorIconClose());
        } else {
            mImgClose.clearColorFilter();
        }
    }

    private OnKeyListener onKeyListener = new OnKeyListener() {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    mSearchListener.hideSearch();
                    hide();
                    return true;
                }
            }
            return false;
        }
    };

    private TextView.OnEditorActionListener onEditorActionListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                if (mSearchListener != null) {
                    mSearchListener.changedSearch(getEdtSearch().getText().toString());
                    mSearchListener.hideSearch();
                }
                return true;
            }
            return false;
        }
    };

    private OnClickListener onClickVoiceSearch = new OnClickListener() {
        @Override
        public void onClick(View v) {
            startVoice(getEdtSearch());
        }
    };

    private OnClickListener onClickCloseSearch = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (getEdtSearch().getText().toString().length() != 0) {
                getEdtSearch().setText("");
                mContext.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        InputMethodManager inputMethodManager = (InputMethodManager)
                                mContext.getSystemService(Context.INPUT_METHOD_SERVICE);

                        if (inputMethodManager != null) {
                            inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED,
                                    InputMethodManager.HIDE_IMPLICIT_ONLY);
                        }
                    }
                });
            }
        }
    };

    private OnClickListener onClickSearchArrow = new OnClickListener() {
        @Override
        public void onClick(View v) {
            mSearchListener.hideSearch();
        }
    };

    private OnClickListener onClickRecyclerView = new OnClickListener() {
        @Override
        public void onClick(View v) {
            hide();
        }
    };

    /**
     * If SearchView is active(show), this method returns the value true
     */
    public boolean isActive() {
        return active;
    }

    private void setActive(boolean active) {
        this.active = active;
    }

    public boolean isVoice() {
        return isVoice;
    }

    public void setIsVoice(boolean isVoice) {
        this.isVoice = isVoice;
    }

    public int getStatusBarHideColor() {
        return mStatusBarHideColor;
    }

    public void setStatusBarHideColor(int mStatusBarHideColor) {
        this.mStatusBarHideColor = mStatusBarHideColor;
    }

    public int getStatusBarShowColor() {
        return mStatusBarShowColor;
    }

    public void setStatusBarShowColor(int mStatusBarShowColor) {
        this.mStatusBarShowColor = mStatusBarShowColor;
    }

    private int getColorIcon() {
        return mColorIcon;
    }

    private void setColorIcon(int colorIcon) {
        this.mColorIcon = colorIcon;
        this.colorIcon();
    }

    public int getColorIconArrow() {
        return mColorIconArrow;
    }

    public void setColorIconArrow(int color) {
        this.mColorIconArrow = color;
        this.colorIconArrow();
    }

    public int getColorIconVoice() {
        return mColorIconVoice;
    }

    public void setColorIconVoice(int color) {
        this.mColorIconVoice = color;
        this.colorIconVoice();
    }

    public int getColorIconClose() {
        return mColorIconClose;
    }

    public void setColorIconClose(int color) {
        this.mColorIconClose = color;
        this.colorIconClose();
    }

    public EditText getEdtSearch() {
        return mEdtSearch;
    }

    public void setEdtSearch(EditText edtSearch) {
        this.mEdtSearch = edtSearch;
    }

    public int getAfterCharacter() {
        return mAfterCharacter;
    }

    public SearchLiveo afterCharacter(int mAfterCharacter) {
        this.mAfterCharacter = mAfterCharacter;
        return this;
    }

    private class OnTextWatcherEdtSearch implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            try {

                if (mTimer != null) {
                    mTimer.cancel();
                }

                if (getEdtSearch().getText().toString().length() == 0) {
                    mImgClose.setVisibility(isVoice() ? View.GONE : View.VISIBLE);
                    mImgVoice.setVisibility(isVoice() ? View.VISIBLE : View.GONE);
                    mImgVoice.setImageResource(R.drawable.ic_keyboard_voice);
                    colorIconVoice();
                } else {
                    mImgVoice.setVisibility(View.GONE);
                    mImgClose.setVisibility(View.VISIBLE);
                    mImgClose.setImageResource(R.drawable.ic_close);
                    colorIconClose();
                }

                colorIcon();
                colorIconArrow();
            } catch (Exception e) {
                e.getStackTrace();
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (s.length() > 3) {
                mTimer = new Timer();
                mTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        // TODO: do what you need here (refresh list)
                        if (mTimer != null) {
                            mTimer.cancel();
                            mContext.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    hideKeybord();
                                }
                            });
                        }
                    }

                }, mSearchDelay);
            }
        }
    }

    /**
     * Hide SearchLiveo
     */
    public void hide() {
        try {
            hideAnimation();
            setActive(false);
        } catch (Exception e) {
            e.getStackTrace();
        }
    }

    /**
     * Show SearchLiveo
     */
    public SearchLiveo show() {
        setActive(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            try {
                showAnimation();
            } catch (ClassCastException ignored) {
            }

        } else {

            Animation mFadeIn = AnimationUtils.loadAnimation(
                    mContext.getApplicationContext(), android.R.anim.fade_in);
            mViewSearch.setEnabled(true);
            mViewSearch.setVisibility(View.VISIBLE);
            mViewSearch.setAnimation(mFadeIn);

            mContext.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    InputMethodManager inputMethodManager = (InputMethodManager)
                            mContext.getSystemService(Context.INPUT_METHOD_SERVICE);

                    if (mContext != null && inputMethodManager != null) {
                        inputMethodManager.toggleSoftInput(
                                InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                    }
                }
            });
        }

        getEdtSearch().requestFocus();
        return this;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void showAnimation() {
        try {

            if (getStatusBarShowColor() != -1) {
                mContext.getWindow().setStatusBarColor(getStatusBarShowColor());
            } else {
                mContext.getWindow().setStatusBarColor(ContextCompat.getColor(mContext, R.color.search_liveo_primary_dark));
            }

            final Animator animator = ViewAnimationUtils.createCircularReveal(mViewSearch,
                    mViewSearch.getWidth() - (int) dpToPixel(24, this.mContext),
                    (int) dpToPixel(23, this.mContext), 0,
                    (float) Math.hypot(mViewSearch.getWidth(), mViewSearch.getHeight()));
            animator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    mContext.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            InputMethodManager inputMethodManager = (InputMethodManager)
                                    mContext.getSystemService(Context.INPUT_METHOD_SERVICE);

                            if (mContext != null && inputMethodManager != null) {
                                inputMethodManager.toggleSoftInput(
                                        InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                            }
                        }
                    });
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });

            animator.setDuration(300);
            animator.start();
        } catch (Exception e) {
            e.getStackTrace();
            mContext.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    InputMethodManager inputMethodManager = (InputMethodManager)
                            mContext.getSystemService(Context.INPUT_METHOD_SERVICE);

                    if (mContext != null && inputMethodManager != null) {
                        inputMethodManager.toggleSoftInput(
                                InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                    }

                }
            });
        }

        mViewSearch.setVisibility(View.VISIBLE);
    }

    private SearchLiveo hideAnimation() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            if (getStatusBarHideColor() != -1) {
                mContext.getWindow().setStatusBarColor(getStatusBarHideColor());
            } else {
                mContext.getWindow().setStatusBarColor(getColorPrimaryDark());
            }

            final Animator animatorHide = ViewAnimationUtils.createCircularReveal(mViewSearch,
                    mViewSearch.getWidth() - (int) dpToPixel(24, mContext),
                    (int) dpToPixel(23, mContext),
                    (float) Math.hypot(mViewSearch.getWidth(), mViewSearch.getHeight()), 0);
            animatorHide.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    mContext.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            InputMethodManager inputMethodManager = (InputMethodManager)
                                    mContext.getSystemService(Context.INPUT_METHOD_SERVICE);

                            if (mContext != null && inputMethodManager != null) {
                                inputMethodManager.hideSoftInputFromWindow(
                                        mViewSearch.getWindowToken(), 0);
                            }
                        }
                    });
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    mViewSearch.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            animatorHide.setDuration(200);
            animatorHide.start();

        } else {

            mContext.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    InputMethodManager inputMethodManager = (InputMethodManager)
                            mContext.getSystemService(Context.INPUT_METHOD_SERVICE);

                    if (mContext != null && inputMethodManager != null) {
                        inputMethodManager.hideSoftInputFromWindow(
                                mViewSearch.getWindowToken(), 0);
                    }
                }
            });

            Animation mFadeOut = AnimationUtils.loadAnimation(
                    mContext.getApplicationContext(), android.R.anim.fade_out);

            mViewSearch.setAnimation(mFadeOut);
            mViewSearch.setVisibility(View.INVISIBLE);
        }

        getEdtSearch().setText("");
        mViewSearch.setEnabled(false);
        return this;
    }

    private int getColorPrimaryDark() {
        return mColorPrimaryDark;
    }

    private void setColorPrimaryDark(int mColorPrimaryDark) {
        this.mColorPrimaryDark = ContextCompat.getColor(mContext, mColorPrimaryDark);
    }

    private float dpToPixel(float dp, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return dp * (metrics.densityDpi / 160f);
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(INSTANCE_STATE, super.onSaveInstanceState());
        bundle.putBoolean(STATE_TO_SAVE, this.isActive());

        if (!getEdtSearch().getText().toString().trim().equals("")) {
            bundle.putString(SEARCH_TEXT, getEdtSearch().getText().toString());
        }

        return bundle;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {

        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            this.setActive(bundle.getBoolean(STATE_TO_SAVE));

            String text = bundle.getString(SEARCH_TEXT, "");
            if (!text.trim().equals("")) {
                getEdtSearch().setText(text);
            }

            if (this.isActive()) {
                show();
            }

            state = bundle.getParcelable(INSTANCE_STATE);
        }

        super.onRestoreInstanceState(state);
    }

    public void hideKeybord() {
        InputMethodManager inputManager = (InputMethodManager) mContext.getSystemService(
                Context.INPUT_METHOD_SERVICE);

        if (mContext.getCurrentFocus() != null && inputManager != null) {
            inputManager.hideSoftInputFromWindow(mContext.getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    private void startVoice(EditText editText) {
        InputMethodManager inputMethodManager = (InputMethodManager)
                mContext.getSystemService(Context.INPUT_METHOD_SERVICE);

        if (inputMethodManager != null) {
            inputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        }

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, mContext.getString(R.string.searchview_voice));

        try {
            mContext.startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(mContext.getApplicationContext(), R.string.not_supported, Toast.LENGTH_SHORT).show();
        }
    }

    public void resultVoice(int requestCode, int resultCode, Intent data) {
        if (requestCode == SearchLiveo.REQUEST_CODE_SPEECH_INPUT) {
            if (resultCode == Activity.RESULT_OK && null != data) {
                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                if (mSearchListener != null) {
                    getEdtSearch().setText(result.get(0));
                    mSearchListener.changedSearch(result.get(0));
                }
            }
        }
    }
}