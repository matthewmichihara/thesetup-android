package com.fourpool.thesetup;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.ViewSwitcher;
import butterknife.InjectView;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import rx.functions.Action1;
import rx.functions.Func1;

import static butterknife.ButterKnife.inject;
import static com.fourpool.thesetup.InterviewActivity.EXTRA_INTERVIEW;
import static com.fourpool.thesetup.InterviewActivity.VIEW_NAME_TOOLBAR;
import static com.fourpool.thesetup.TheSetupApp.objectGraph;
import static com.google.common.collect.ObjectArrays.concat;
import static rx.android.schedulers.AndroidSchedulers.mainThread;
import static rx.schedulers.Schedulers.io;

public class InterviewsActivity extends ActionBarActivity implements InterviewsAdapter.Listener {
  private static final String EXTRA_INTERVIEWS = "interviews";

  @Inject TheSetup theSetup;

  @InjectView(R.id.toolbar) Toolbar toolbar;
  @InjectView(R.id.switcher) ViewSwitcher switcher;
  @InjectView(R.id.list) RecyclerView interviewList;
  @InjectView(R.id.progress) ProgressBar progress;

  private final List<Interview> interviews = new ArrayList<>();

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_interviews);
    inject(this);
    objectGraph(this).inject(this);
    setSupportActionBar(toolbar);

    final InterviewsAdapter adapter = new InterviewsAdapter(this, interviews, this);
    final RecyclerView.LayoutManager lm = new LinearLayoutManager(this);
    interviewList.setLayoutManager(lm);
    interviewList.setHasFixedSize(true);
    interviewList.setAdapter(adapter);

    final Func1<InterviewsResponse, List<Interview>> map =
        new Func1<InterviewsResponse, List<Interview>>() {
          @Override public List<Interview> call(InterviewsResponse response) {
            return response.interviews();
          }
        };

    final Action1<List<Interview>> interviewsSubscriber = new Action1<List<Interview>>() {
      @Override public void call(List<Interview> newInterviews) {
        updateInterviews(adapter, newInterviews);
      }
    };

    if (savedInstanceState != null) {
      ArrayList<Interview> savedInterviews =
          savedInstanceState.getParcelableArrayList(EXTRA_INTERVIEWS);
      updateInterviews(adapter, savedInterviews);
    } else {
      theSetup.interviews()
          .map(map)
          .subscribeOn(io())
          .observeOn(mainThread())
          .subscribe(interviewsSubscriber);
    }
  }

  @Override protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putParcelableArrayList(EXTRA_INTERVIEWS, new ArrayList<>(interviews));
  }

  @Override
  public void onInterviewClick(Interview interview, Pair<View, String>... sharedElements) {
    Intent intent = new Intent(this, InterviewActivity.class);
    intent.putExtra(EXTRA_INTERVIEW, interview);

    Pair<View, String> sharedToolbar = new Pair<View, String>(toolbar, VIEW_NAME_TOOLBAR);

    ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this,
        concat(sharedElements, sharedToolbar));

    ActivityCompat.startActivity(this, intent, options.toBundle());
  }

  private void updateInterviews(InterviewsAdapter adapter, List<Interview> newInterviews) {
    interviews.clear();
    interviews.addAll(newInterviews);
    adapter.notifyDataSetChanged();

    switcher.showNext();
    progress.setVisibility(View.GONE);
  }
}
