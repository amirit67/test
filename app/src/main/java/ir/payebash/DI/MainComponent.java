package ir.payebash.DI;

import javax.inject.Singleton;

import dagger.Component;
import ir.payebash.Activities.PostDetailsActivity;
import ir.payebash.Activities.PostRegisterActivity;
import ir.payebash.Activities.UpdatePostActivity;
import ir.payebash.Activities.UserProfileActivity;
import ir.payebash.Fragments.EventsWantedFragment;
import ir.payebash.Fragments.HomeFragment;
import ir.payebash.Fragments.MyPayeFragment;
import ir.payebash.Fragments.ProfileFragment;
import ir.payebash.Fragments.SearchFragment;
import ir.payebash.Fragments.SlideShowFragment;
import ir.payebash.Fragments.UncomingEventsFragment;

/**
 * Created by KingStar on 3/2/2018.
 */
@Singleton
@MainScope
@Component(modules = {ImageLoaderMoudle.class, NetModule.class})
public interface MainComponent {
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
