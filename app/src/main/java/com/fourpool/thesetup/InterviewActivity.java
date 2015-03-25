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
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.InjectView;
import com.squareup.picasso.Picasso;
import java.util.List;
import javax.inject.Inject;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import static butterknife.ButterKnife.inject;
import static com.fourpool.thesetup.TheSetupApp.objectGraph;
import static java.util.concurrent.TimeUnit.SECONDS;

public class InterviewActivity extends ActionBarActivity {
  public static final String EXTRA_INTERVIEW = "interview";

  public static final String VIEW_NAME_IMAGE = "image_header";
  public static final String VIEW_NAME_SUMMARY = "summary";
  public static final String VIEW_NAME_DATE = "date";
  public static final String VIEW_NAME_CATEGORIES = "categories";
  public static final String VIEW_NAME_TOOLBAR = "toolbar";

  @Inject TheSetup theSetup;
  @Inject Spanner spanner;

  @InjectView(R.id.toolbar) Toolbar toolbar;
  @InjectView(R.id.image) ImageView imageView;
  @InjectView(R.id.summary) TextView summaryView;
  @InjectView(R.id.date) TextView dateView;
  @InjectView(R.id.categories) TextView categoriesView;
  @InjectView(R.id.contents) TextView contentsView;

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

    final Interview interview = getIntent().getParcelableExtra(EXTRA_INTERVIEW);
    final String slug = interview.slug();

    setTitle(interview.name());
    setSupportActionBar(toolbar);

    Picasso.with(this).load(interview.imageUrl()).into(imageView);

    final Func1<InterviewResponse, Interview> map = new Func1<InterviewResponse, Interview>() {
      @Override public Interview call(InterviewResponse response) {
        return response.interview();
      }
    };

    final Action1<Interview> subscriber = new Action1<Interview>() {
      @Override public void call(final Interview interview) {
        String contents = interview.contents();

        final Spanner.Listener listener = new Spanner.Listener() {
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
        };

        Spanner.ProcessedContents processed =
            spanner.process(InterviewActivity.this, contents, listener);
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
      }
    };

    theSetup.interview(slug)
        .map(map)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(subscriber);
  }
}
