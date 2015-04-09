package com.fourpool.thesetup;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.InjectView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.github.ksoichiro.android.observablescrollview.ScrollUtils;
import com.squareup.picasso.Picasso;
import java.util.List;
import javax.inject.Inject;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import static butterknife.ButterKnife.inject;
import static com.fourpool.thesetup.TheSetupApp.objectGraph;
import static com.fourpool.thesetup.Util.url;
import static java.util.concurrent.TimeUnit.SECONDS;

public class InterviewActivity extends ActionBarActivity
    implements ObservableScrollViewCallbacks, Spanner.Listener {
  public static final String EXTRA_INTERVIEW = "interview";

  public static final String VIEW_NAME_IMAGE = "image_header";
  public static final String VIEW_NAME_SUMMARY = "summary";
  public static final String VIEW_NAME_DATE = "date";
  public static final String VIEW_NAME_CATEGORIES = "categories";
  public static final String VIEW_NAME_TOOLBAR = "toolbar";
  public static final String VIEW_NAME_NAME = "name";

  private static final float MAX_TEXT_SCALE_DELTA = 0.3f;

  @Inject TheSetup theSetup;
  @Inject Picasso picasso;
  @Inject Spanner spanner;

  @InjectView(R.id.toolbar) Toolbar toolbar;
  @InjectView(R.id.image) ImageView imageView;
  @InjectView(R.id.overlay) View overlayView;
  @InjectView(R.id.summary) TextView summaryView;
  @InjectView(R.id.date) TextView dateView;
  @InjectView(R.id.categories) TextView categoriesView;
  @InjectView(R.id.contents) TextView contentsView;
  @InjectView(R.id.scroller) ObservableScrollView scrollView;
  @InjectView(R.id.name) TextView nameView;

  private Interview interview;
  private int actionBarSize;
  private int flexibleImageSpaceHeight;
  private int toolbarColor;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_interview);
    inject(this);
    objectGraph(this).inject(this);

    ViewCompat.setTransitionName(imageView, VIEW_NAME_IMAGE);
    ViewCompat.setTransitionName(summaryView, VIEW_NAME_SUMMARY);
    ViewCompat.setTransitionName(dateView, VIEW_NAME_DATE);
    ViewCompat.setTransitionName(categoriesView, VIEW_NAME_CATEGORIES);
    ViewCompat.setTransitionName(toolbar, VIEW_NAME_TOOLBAR);
    ViewCompat.setTransitionName(nameView, VIEW_NAME_NAME);

    setSupportActionBar(toolbar);

    flexibleImageSpaceHeight =
        getResources().getDimensionPixelSize(R.dimen.flexible_space_image_height);
    actionBarSize = Util.actionBarSize(this);
    toolbarColor = getResources().getColor(R.color.primary);

    this.interview = getIntent().getParcelableExtra(EXTRA_INTERVIEW);
    final String slug = interview.slug();

    nameView.setText(interview.name());
    setTitle(null);

    picasso.load(url(interview)).into(imageView);

    final Func1<InterviewResponse, Interview> map = new Func1<InterviewResponse, Interview>() {
      @Override public Interview call(InterviewResponse response) {
        return response.interview();
      }
    };

    final Action1<Interview> subscriber = new Action1<Interview>() {
      @Override public void call(final Interview interview) {
        updateInterview(interview);
      }
    };

    if (savedInstanceState != null) {
      this.interview = savedInstanceState.getParcelable(EXTRA_INTERVIEW);
      updateInterview(interview);
    } else {
      theSetup.interview(slug)
          .map(map)
          .subscribeOn(Schedulers.io())
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe(subscriber);
    }
  }

  @Override protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putParcelable(EXTRA_INTERVIEW, interview);
  }

  @Override public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
    // Translate overlay and image
    float flexibleRange = flexibleImageSpaceHeight - actionBarSize;
    int minOverlayTransitionY = actionBarSize - overlayView.getHeight();
    overlayView.setTranslationY(ScrollUtils.getFloat(-scrollY, minOverlayTransitionY, 0));
    imageView.setTranslationY(ScrollUtils.getFloat(-scrollY / 2, minOverlayTransitionY, 0));

    // Change alpha of overlay
    overlayView.setAlpha(ScrollUtils.getFloat((float) scrollY / flexibleRange, 0, 1));

    // Scale title text
    float scale = 1 + ScrollUtils.getFloat((flexibleRange - scrollY) / flexibleRange, 0,
        MAX_TEXT_SCALE_DELTA);
    nameView.setPivotX(0);
    nameView.setPivotY(0);
    nameView.setScaleX(scale);
    nameView.setScaleY(scale);

    // Translate title text
    int maxTitleTranslationY = (int) (flexibleImageSpaceHeight - nameView.getHeight() * scale);
    int titleTranslationY = maxTitleTranslationY - scrollY;
    titleTranslationY = Math.max(0, titleTranslationY);
    nameView.setTranslationY(titleTranslationY);

    // Change alpha of toolbar background
    if (-scrollY + flexibleImageSpaceHeight <= actionBarSize) {
      toolbar.setBackgroundColor(ScrollUtils.getColorWithAlpha(1, toolbarColor));
    } else {
      toolbar.setBackgroundColor(ScrollUtils.getColorWithAlpha(0, toolbarColor));
    }
  }

  @Override public void onDownMotionEvent() {
  }

  @Override public void onUpOrCancelMotionEvent(ScrollState scrollState) {
  }

  @Override public void onLinkClick(String url) {
    Intent i = new Intent(Intent.ACTION_VIEW);
    i.setData(Uri.parse(url));
    startActivity(i);
  }

  @Override public void onItemClick(String slug) {
    List<Hardware> hardwareList = interview.gear().hardware();
    if (hardwareList != null) {
      for (Hardware hardware : hardwareList) {
        if (hardware.slug().equalsIgnoreCase(slug)) {
          String url = hardware.url();
          Intent i = new Intent(Intent.ACTION_VIEW);
          i.setData(Uri.parse(url));
          startActivity(i);
          return;
        }
      }
    }

    List<Software> softwareList = interview.gear().software();
    if (softwareList != null) {
      for (Software software : softwareList) {
        if (software.slug().equalsIgnoreCase(slug)) {
          String url = software.url();
          Intent i = new Intent(Intent.ACTION_VIEW);
          i.setData(Uri.parse(url));
          startActivity(i);
          return;
        }
      }
    }
  }

  private void updateInterview(Interview updatedInterview) {
    this.interview = updatedInterview;
    String contents = interview.contents();

    Spanner.ProcessedContents processed =
        spanner.process(InterviewActivity.this, contents, InterviewActivity.this);
    List<Spanner.SpanInfo> spanInfos = processed.spanInfos;

    Spannable interviewSpannable = new SpannableString(processed.contents);
    for (Spanner.SpanInfo spanInfo : spanInfos) {
      interviewSpannable.setSpan(spanInfo.span, spanInfo.start, spanInfo.end, spanInfo.flags);
    }

    contentsView.setText(interviewSpannable);
    contentsView.setMovementMethod(LinkMovementMethod.getInstance());

    summaryView.setText(interview.summary());
    CharSequence date = DateUtils.getRelativeTimeSpanString(SECONDS.toMillis(interview.date()));
    dateView.setText(date);
    categoriesView.setText(TextUtils.join(" ", interview.categories()));

    scrollView.setScrollViewCallbacks(InterviewActivity.this);

    ScrollUtils.addOnGlobalLayoutListener(scrollView, new Runnable() {
      @Override public void run() {
        onScrollChanged(0, false, false);
      }
    });
  }
}
