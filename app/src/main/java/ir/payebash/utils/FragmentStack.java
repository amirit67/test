package ir.payebash.utils;

import android.app.Activity;
import android.os.Handler;
import android.view.inputmethod.InputMethodManager;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Lifecycle;

import java.util.ArrayList;
import java.util.List;

import ir.payebash.R;

/**
 * Created by Alireza Eskandarpour Shoferi on 11/16/2017.
 */

public class FragmentStack {
    private final Activity activity;
    private final FragmentManager manager;
    private final int containerId;

    public FragmentStack(Activity activity, FragmentManager manager, int containerId) {
        this.activity = activity;
        this.manager = manager;
        this.containerId = containerId;
    }

    public int size() {
        return getFragments().size();
    }

    public void push(Fragment fragment) {

        Fragment top = peek();
        if (top != null) {
            manager.beginTransaction()
                    //.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                    .remove(top)
                    .add(containerId, fragment, indexToTag(manager.getBackStackEntryCount() + 1))
                    .addToBackStack(null)
                    .commit();
        } else {
            manager.beginTransaction()
                    .add(containerId, fragment, indexToTag(0))
                    .commit();
        }

        manager.executePendingTransactions();
    }

    public boolean back() {
        Fragment top = peek();
        if (top instanceof OnBackPressedHandlingFragment) {
            if (((OnBackPressedHandlingFragment) top).onBackPressed())
                return true;
        }
        return pop();
    }

    public boolean pop() {
        if (manager.getBackStackEntryCount() == 0)
            return false;
        manager.popBackStackImmediate();
        return true;
    }

    public void replace(Fragment fragment) {
        String fragmentTag = fragment.getClass().getSimpleName();

        boolean fragmentPopped = manager
                .popBackStackImmediate(fragmentTag, 0);

        //String tag = indexToTag(fragmentTag);
        FragmentTransaction ftx = manager.beginTransaction();
        Fragment fra = manager.findFragmentByTag(fragmentTag);
        if (!fragmentPopped && fra == null)
            ftx.addToBackStack(fragmentTag);

        if (fra != null) {
            if (fra.getLifecycle().getCurrentState() != Lifecycle.State.INITIALIZED) {
                return;
            }
            ftx.remove(fra);
        }

        if (fragmentTag.equals("CreateEventDialog"))
            ftx.setCustomAnimations(R.anim.slide_in_down,
                    R.anim.slide_out_down, R.anim.slide_in_up,
                    R.anim.slide_out_up);

        else
            ftx.setCustomAnimations(R.anim.slide_in_right,
                    R.anim.slide_out_left, R.anim.slide_in_left,
                    R.anim.slide_out_right);

        ftx.replace(containerId, fragment, fragmentTag);

        ftx.commit();

        new Handler().postDelayed(() -> {
            try {
                ((InputMethodManager) activity.getSystemService("input_method")).hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
            } catch (Exception e) {
            }
        }, 250);

    }

    /**
     * Returns the topmost fragment in the stack.
     */
    public Fragment peek() {
        return manager.findFragmentById(containerId);
    }

    /**
     * Returns a back fragment if the fragment is of given class.
     * If such fragment does not exist and activity implements the given class then the activity
     * will be returned.
     *
     * @param fragment     a fragment to search from.
     * @param callbackType a class of type for callback to search.
     * @param <T>          a type of callback.
     * @return a back fragment or activity.
     */
    @SuppressWarnings("unchecked")
    public <T> T findCallback(Fragment fragment, Class<T> callbackType) {

        Fragment back = getBackFragment(fragment);

        if (back != null && callbackType.isAssignableFrom(back.getClass()))
            return (T) back;

        if (callbackType.isAssignableFrom(activity.getClass()))
            return (T) activity;

        return null;
    }

    private Fragment getBackFragment(Fragment fragment) {
        List<Fragment> fragments = getFragments();
        for (int f = fragments.size() - 1; f >= 0; f--) {
            if (fragments.get(f) == fragment && f > 0)
                return fragments.get(f - 1);
        }
        return null;
    }

    private List<Fragment> getFragments() {
        List<Fragment> fragments = new ArrayList<>(manager.getBackStackEntryCount() + 1);
        for (int i = 0; i < manager.getBackStackEntryCount() + 1; i++) {
            Fragment fragment = manager.findFragmentByTag(indexToTag(i));
            if (fragment != null)
                fragments.add(fragment);
        }
        return fragments;
    }

    private String indexToTag(int index) {
        return Integer.toString(index);
    }

    public interface OnBackPressedHandlingFragment {
        boolean onBackPressed();
    }
}
