package ir.payebash.Activities;

import android.database.Cursor;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.github.islamkhsh.CardSliderViewPager;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import ir.payebash.Adapters.BannerAdapter;
import ir.payebash.Application;
import ir.payebash.BuildConfig;
import ir.payebash.Classes.HSH;
import ir.payebash.Models.event.story.StoryModel;
import ir.payebash.R;
import ir.payebash.utils.RecyclerSnapHelper;
import ir.payebash.utils.storiesprogressview.StoriesProgressView;
import ru.tinkoff.scrollingpagerindicator.ScrollingPagerIndicator;


public class StoriesActivity extends AppCompatActivity implements View.OnClickListener,
        StoriesProgressView.StoriesListener {

    private StoriesProgressView storiesProgressView;
    private Button btnBeup;
    private ConstraintLayout clImmadiate;
    private ImageView imgProfile;
    @Inject
    ImageLoader imageLoader;
    @Inject
    DisplayImageOptions options;
    private TextView tvFullname, txtEventTitle, txtLocation, tvWoman,tvCreatedate,
            txtCategory, txtTimeToJoin, txtCost, txtStartDate, tvFollowers;
    private List<StoryModel> feed;
    private long pressTime = 0L;
    private long limit = 500L;
    private int position = 0;

    private View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    pressTime = System.currentTimeMillis();
                    storiesProgressView.pause();
                    return false;
                case MotionEvent.ACTION_UP:
                    long now = System.currentTimeMillis();
                    storiesProgressView.resume();
                    return limit < now - pressTime;
            }
            return false;
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story);
        Application.getComponent().Inject(this);
        feed = (List<StoryModel>) getIntent().getExtras().getSerializable("stories");
        position = getIntent().getExtras().getInt("position");

        storiesProgressView = findViewById(R.id.stories);
        storiesProgressView.setStoriesCount(feed.size()); // <- set stories
        storiesProgressView.setStoryDuration(7500L); // <- set a story duration
        storiesProgressView.setStoriesListener(this); // <- set listener
        storiesProgressView.startStories(position); // <- start progress

        View reverse = findViewById(R.id.reverse);
        reverse.setOnClickListener(v -> storiesProgressView.reverse());
        reverse.setOnTouchListener(onTouchListener);

        // bind skip view
        View skip = findViewById(R.id.skip);
        skip.setOnClickListener(v -> storiesProgressView.skip());
        skip.setOnTouchListener(onTouchListener);

        tvCreatedate = findViewById(R.id.tv_createdate);
        tvFullname = findViewById(R.id.txt_fullname);
        tvFollowers = findViewById(R.id.txt_followers);
        txtEventTitle = findViewById(R.id.txt_event_title);
        //txtEventDate = findViewById(R.id.txt_event_date);
        txtCategory = findViewById(R.id.txt_category);
        txtLocation = findViewById(R.id.txt_location);
        txtTimeToJoin = findViewById(R.id.txt_time_to_join);
        txtCost = findViewById(R.id.txt_cost);
        txtStartDate = findViewById(R.id.txt_startdate);
        imgProfile = findViewById(R.id.img_profile);
        btnBeup = findViewById(R.id.btn_beup);
        clImmadiate = findViewById(R.id.cl_immadiate);
        tvWoman = findViewById(R.id.tv_woman);
        bind(position);

    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onNext(int pos) {
        //Toast.makeText(this, "onNext" + pos, Toast.LENGTH_SHORT).show();
        bind(pos);
    }


    @Override
    public void onPrev(int pos) {
        // Call when finished revserse animation.
        //Toast.makeText(this, "onPrev" + pos, Toast.LENGTH_SHORT).show();
        bind(pos);
    }

    @Override
    public void onComplete() {
        finish();
    }

    @Override
    protected void onDestroy() {
        // Very important !
        storiesProgressView.destroy();
        super.onDestroy();
    }

    private void bind(int pos) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault());
        try {
            tvCreatedate.setText(HSH.printDifference(simpleDateFormat.parse(feed.get(pos).getCreateDate()
                    .replace("T", " ")), new Date()) + " پیش");

            txtTimeToJoin.setText(HSH.printDifference(new Date(),
                    simpleDateFormat.parse(feed.get(pos).getTimeToJoin().replace("T", " "))));
        } catch (ParseException e) {
        }
        tvFullname.setText(feed.get(pos).getEventOwner().get(0).getName());
        txtEventTitle.setText(feed.get(pos).getTitle());
        tvFollowers.setText(feed.get(pos).getNumberFollowers());
        txtCost.setText(feed.get(pos).getCost());
        txtStartDate.setText(feed.get(pos).getStartDate());
        tvWoman.setVisibility(feed.get(pos).isIsWoman()? View.VISIBLE : View.GONE);
        clImmadiate.setVisibility(feed.get(pos).isIsImmediate() ? View.VISIBLE : View.GONE);

        Cursor cr = null;
        //PostDetailsModel item = null;
        try {
            cr = Application.database.rawQuery("SELECT name from categories where id = '" + feed.get(pos).getSubject() + "' ", null);
            cr.moveToFirst();
            txtCategory.setText(cr.getString(cr.getColumnIndex("name")));
        } catch (Exception e) {
        }

        try {
            cr = Application.database.rawQuery("SELECT id,StateCity from Citys where id = '" + feed.get(pos).getCity() + "' ", null);
            cr.moveToFirst();
            txtLocation.setText(cr.getString(cr.getColumnIndex("StateCity")));
        } catch (Exception e) {
            if (feed.get(pos).getCity() == 1)
                txtLocation.setText("سراسر کشور");
        }
        cr.close();
        imageLoader.displayImage(feed.get(pos).getEventOwner().get(0).getProfileImage(), imgProfile, options);

        try {
            ArrayList<String> banners = new ArrayList<>();
            String[] temp = feed.get(pos).getImages().split(",");

            RecyclerView recyclerView = findViewById(R.id.recycler_banner);
            ScrollingPagerIndicator indicator = findViewById(R.id.indicator);
            for (int i = 0; i < temp.length; i++) {

                temp[i] = (getString(R.string.image) + temp[i] + ".jpg");
                banners.add(getString(R.string.image) + temp[i] + ".jpg");
                CardSliderViewPager cardSliderViewPager = findViewById(R.id.pager);
                //cardSliderViewPager.setAdapter(new BannerAdapter(banners, imageLoader));
            }

            BannerAdapter bannerAdapter = new BannerAdapter(Arrays.asList(temp), StoriesActivity.this);
            recyclerView.setAdapter(bannerAdapter);

            RecyclerSnapHelper snapHelper = new RecyclerSnapHelper();
            snapHelper.attachToRecyclerView(recyclerView);

            indicator.attachToRecyclerView(recyclerView);
            indicator.setSelectedDotColor(getResources().getColor(R.color.white));
            indicator.setVisibleDotCount(3);

        } catch (Exception e) {
        }
    }
}