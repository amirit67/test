package ir.payebash.DI;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.AndroidInjector;
import dagger.android.DaggerApplication;
import dagger.android.support.AndroidSupportInjectionModule;
import ir.payebash.Activities.PostDetailsActivity;
import ir.payebash.Activities.PostRegisterActivity;
import ir.payebash.Activities.UpdatePostActivity;
import ir.payebash.Activities.UserProfileActivity;
import ir.payebash.Adapters.PayeAdapter;
import ir.payebash.Adapters.StoryAdapter;
import ir.payebash.Application;
import ir.payebash.Fragments.EventsWantedFragment;
import ir.payebash.Fragments.HomeFragment;
import ir.payebash.Fragments.MyPayeFragment;
import ir.payebash.Fragments.ProfileFragment;
import ir.payebash.Fragments.SearchFragment;
import ir.payebash.Fragments.SlideShowFragment;
import ir.payebash.Fragments.UncomingEventsFragment;
import ir.payebash.asynktask.AsynctaskGetPost;

/**
 * Created by KingStar on 3/2/2018.
 */
@Singleton
@MainScope
@Component(modules = {AppModule.class, ImageLoaderMoudle.class, NetModule.class})
public interface MainComponent {

    //Fragments
    void Inject(AsynctaskGetPost getPost);


    //Adapters
    void Inject(StoryAdapter storyAdapter);

    void Inject(PayeAdapter payeAdapter);

    void Inject(HomeFragment mainActivity);

    void Inject(SearchFragment mainActivity);

    void Inject(EventsWantedFragment mainActivity);



    void Inject(UncomingEventsFragment mainActivity);

    void Inject(MyPayeFragment mainActivity);

    void Inject(ProfileFragment mainActivity);

    void Inject(PostDetailsActivity mainActivity);

    void Inject(PostRegisterActivity mainActivity);

    void Inject(UpdatePostActivity mainActivity);

    void Inject(UserProfileActivity mainActivity);

    void Inject(SlideShowFragment mainActivity);
}

/*
@Singleton
@Component(modules = {
        ActivityBindingModule.class,
        AndroidSupportInjectionModule.class, AppModule.class, ImageLoaderMoudle.class, NetModule.class})

public interface MainComponent extends AndroidInjector<DaggerApplication> {


    @Component.Builder
    interface Builder {

        @BindsInstance
        MainComponent.Builder application(Application application);

        MainComponent build();
    }
}*/

