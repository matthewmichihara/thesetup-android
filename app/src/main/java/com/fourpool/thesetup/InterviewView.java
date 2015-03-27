package com.fourpool.thesetup;

import android.content.Context;
import android.support.v4.util.Pair;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.InjectView;
import butterknife.OnClick;
import com.squareup.picasso.Picasso;
import javax.inject.Inject;

import static butterknife.ButterKnife.inject;
import static com.fourpool.thesetup.InterviewActivity.VIEW_NAME_CATEGORIES;
import static com.fourpool.thesetup.InterviewActivity.VIEW_NAME_DATE;
import static com.fourpool.thesetup.InterviewActivity.VIEW_NAME_IMAGE;
import static com.fourpool.thesetup.InterviewActivity.VIEW_NAME_NAME;
import static com.fourpool.thesetup.InterviewActivity.VIEW_NAME_SUMMARY;
import static com.fourpool.thesetup.TheSetupApp.objectGraph;
import static com.fourpool.thesetup.Util.url;
import static java.util.concurrent.TimeUnit.SECONDS;

public class InterviewView extends FrameLayout {
  @Inject Picasso picasso;

  @InjectView(R.id.image) ImageView imageView;
  @InjectView(R.id.name) TextView nameView;
  @InjectView(R.id.summary) TextView summaryView;
  @InjectView(R.id.date) TextView dateView;
  @InjectView(R.id.categories) TextView categoriesView;

  private Interview interview;
  private Listener listener;

  public interface Listener {
    void onClick(Interview interview, Pair<View, String>... sharedElements);
  }

  public InterviewView(Context context) {
    this(context, null);
  }

  public InterviewView(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public InterviewView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);

    inflate(context, R.layout.view_interview, this);
    inject(this);
    objectGraph(context).inject(this);
  }

  public void setInterview(Interview interview) {
    this.interview = interview;

    String imageUrl = url(interview);
    picasso.load(imageUrl).placeholder(android.R.color.darker_gray).into(imageView);

    nameView.setText(interview.name());
    summaryView.setText(interview.summary());

    CharSequence date = DateUtils.getRelativeTimeSpanString(SECONDS.toMillis(interview.date()));
    dateView.setText(date);

    String categories = TextUtils.join(" ", interview.categories());
    categoriesView.setText(categories);
  }

  public void setListener(Listener listener) {
    this.listener = listener;
  }

  @OnClick(R.id.card_view) public void onClick() {
    Pair<View, String> sharedImage = new Pair<View, String>(imageView, VIEW_NAME_IMAGE);
    Pair<View, String> sharedSummary = new Pair<View, String>(summaryView, VIEW_NAME_SUMMARY);
    Pair<View, String> sharedDate = new Pair<View, String>(dateView, VIEW_NAME_DATE);
    Pair<View, String> sharedCategories =
        new Pair<View, String>(categoriesView, VIEW_NAME_CATEGORIES);
    Pair<View, String> sharedName = new Pair<View, String>(nameView, VIEW_NAME_NAME);

    if (listener != null) {
      listener.onClick(interview, sharedImage, sharedSummary, sharedDate, sharedCategories, sharedName);
    }
  }
}
