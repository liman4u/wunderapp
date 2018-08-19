package com.wunder.wunderapp.utils;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import static com.google.common.base.Preconditions.checkNotNull;


/**
 * This provides methods to help Activities load their UI.
 */
public class ActivityUtils {

    /**
     * The {@code fragment} is added to the container view with id {@code frameId}. The operation is
     * performed by the {@code fragmentManager}.
     */
    public static void addFragmentToActivity(@NonNull FragmentManager fragmentManager,
                                             @NonNull Fragment fragment, int frameId) {
        checkNotNull(fragmentManager);
        checkNotNull(fragment);


        Fragment oldFragment =  fragmentManager.findFragmentByTag(fragment.getClass().getSimpleName());
        if(oldFragment != null)
        {
            fragmentManager.beginTransaction().show(oldFragment).commit();

        }else{

            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(frameId, fragment);
            transaction.addToBackStack(fragment.getClass().getSimpleName());
            transaction.commit();

        }


    }

}
