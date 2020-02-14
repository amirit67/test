package ir.payebash.DI;

import javax.inject.Singleton;

import dagger.Component;
import ir.payebash.activities.MyEventDetailsActivity;
import ir.payebash.activities.PostDetailsActivity;
import ir.payebash.activities.PostRegisterActivity;
import ir.payebash.activities.SplashActivity;
import ir.payebash.activities.StoriesActivity;
import ir.payebash.activities.UpdatePostActivity;
import ir.payebash.activities.UserProfileActivity;
import ir.payebash.Adapters.BannerAdapter;
import ir.payebash.Adapters.ContactsAdapter;
import ir.payebash.Adapters.FollowersAdapter;
import ir.payebash.Adapters.ImagesAdapter;
import ir.payebash.Adapters.MessageAdapter;
import ir.payebash.Adapters.PayeAdapter;
import ir.payebash.Adapters.RequestAdapter;
import ir.payebash.Adapters.RoomsAdapter;
import ir.payebash.Adapters.StoryAdapter;
import ir.payebash.Fragments.EventsWantedFragment;
import ir.payebash.Fragments.HomeFragment;
import ir.payebash.Fragments.MyPayeFragment;
import ir.payebash.Fragments.ProfileFragment;
import ir.payebash.Fragments.SearchFragment;
import ir.payebash.Fragments.SlideShowFragment;
import ir.payebash.Fragments.UncomingEventsFragment;
import ir.payebash.Fragments.user.ActivitiesFragment;
import ir.payebash.asynktask.follow.AsynctaskCheckContacts;
import ir.payebash.asynktask.AsynctaskEventDetails;
import ir.payebash.asynktask.follow.AsynctaskFollow;
import ir.payebash.asynktask.user.AsynctaskGetMyEvents;
import ir.payebash.asynktask.AsynctaskGetPost;
import ir.payebash.asynktask.AsynctaskLogin;
import ir.payebash.asynktask.AsynctaskRegister;
import ir.payebash.asynktask.AsynctaskRequestToJoin;
import ir.payebash.asynktask.AsynctaskStoryEvents;
import ir.payebash.asynktask.GetTokenAsynkTask;
import ir.payebash.asynktask.forgotPassword.AsynctaskStep1;
import ir.payebash.asynktask.forgotPassword.AsynctaskStep2;
import ir.payebash.asynktask.forgotPassword.AsynctaskStep3;
import ir.payebash.asynktask.user.AsynctaskGetUserInfo;

/**
 * Created by KingStar on 3/2/2018.
 */
@Singleton
@MainScope
@Component(modules = {AppModule.class, ImageLoaderMoudle.class, NetModule.class})
public interface MainComponent {

    //Fragments
    void Inject(AsynctaskGetPost getPost);

    void Inject(AsynctaskGetMyEvents asynctaskGetMyEvents);

    //Asynktask
    void Inject(AsynctaskGetUserInfo asynctaskGetUserInfo);

    void Inject(GetTokenAsynkTask getTokenAsynkTask);

    void Inject(AsynctaskEventDetails asynctaskEventDetails);

    void Inject(AsynctaskStoryEvents asynctaskStoryEvents);

    void Inject(AsynctaskLogin asynctaskLogin);

    void Inject(AsynctaskRegister asynctaskRegister);

    void Inject(AsynctaskStep1 asynctaskStep1);

    void Inject(AsynctaskStep2 asynctaskStep2);

    void Inject(AsynctaskStep3 asynctaskStep3);

    void Inject(AsynctaskRequestToJoin asynctaskRequestToJoin);

    void Inject(AsynctaskCheckContacts asynctaskCheckContacts);

    void Inject(AsynctaskFollow asynctaskFollow);


    //Adapters
    void Inject(FollowersAdapter followersAdapter);

    void Inject(StoryAdapter storyAdapter);

    void Inject(PayeAdapter payeAdapter);

    void Inject(MessageAdapter messageAdapter);

    void Inject(RequestAdapter requestAdapter);

    void Inject(ContactsAdapter contactsAdapter);
    ///////////////////////////////////////

    void Inject(HomeFragment mainActivity);

    void Inject(SearchFragment mainActivity);

    void Inject(EventsWantedFragment mainActivity);

    void Inject(BannerAdapter bannerAdapter);

    void Inject(RoomsAdapter roomsAdapter);

    void Inject(ImagesAdapter imagesAdapter);

    void Inject(UncomingEventsFragment mainActivity);

    void Inject(MyPayeFragment mainActivity);

    void Inject(ProfileFragment mainActivity);

    void Inject(PostDetailsActivity mainActivity);

    void Inject(PostRegisterActivity mainActivity);

    void Inject(UpdatePostActivity mainActivity);

    void Inject(UserProfileActivity mainActivity);

    void Inject(SlideShowFragment mainActivity);


    //Activity
    void Inject(StoriesActivity storiesActivity);

    void Inject(MyEventDetailsActivity myEventDetailsActivity);

    void Inject(SplashActivity splashActivity);

    //Fragments
    void Inject(ActivitiesFragment activitiesFragment);
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

