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
import butterknife.InjectView;
import java.util.List;
import javax.inject.Inject;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import static butterknife.ButterKnife.inject;
import static com.fourpool.thesetup.InterviewActivity.EXTRA_INTERVIEW;
import static com.fourpool.thesetup.InterviewActivity.VIEW_NAME_TOOLBAR;
import static com.fourpool.thesetup.TheSetupApp.objectGraph;
import static com.google.common.collect.ObjectArrays.concat;

public class InterviewsActivity extends ActionBarActivity implements InterviewsAdapter.Listener {
  @Inject TheSetup theSetup;

  @InjectView(R.id.toolbar) Toolbar toolbar;
  @InjectView(R.id.list) RecyclerView interviewList;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_interviews);
    inject(this);
    objectGraph(this).inject(this);

    setSupportActionBar(toolbar);

    final InterviewsAdapter adapter = new InterviewsAdapter(this);
    adapter.setListener(this);
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

    theSetup.interviews()
        .map(map)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(adapter);
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
}
