package com.luckincoffee.base;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.List;

public class BetterLifecycleFragment extends Fragment {

    private boolean isLastVisible = false;
    private boolean hidden = false;
    private boolean isFirst = true;
    private boolean isResuming = false;
    private boolean isViewDestroyed = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isLastVisible = false;
        hidden = false;
        isFirst = true;
        isViewDestroyed = false;
    }

    @Override
    public void onResume() {
        super.onResume();
        isResuming = true;
        tryToChangeVisibility(true);
    }

    @Override
    public void onPause() {
        super.onPause();
        isResuming = false;
        tryToChangeVisibility(false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        isViewDestroyed = true;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        setUserVisibleHintClient(isVisibleToUser);
    }

    private void setUserVisibleHintClient(boolean isVisibleToUser) {
        tryToChangeVisibility(isVisibleToUser);
        if (isAdded()) {
            // 当Fragment不可见时，其子Fragment也是不可见的。因此要通知子Fragment当前可见状态改变了。
            List<Fragment> fragments = getChildFragmentManager().getFragments();
            if (fragments != null) {
                for (Fragment fragment : fragments) {
                    if (fragment instanceof BetterLifecycleFragment) {
                        ((BetterLifecycleFragment) fragment).setUserVisibleHintClient(isVisibleToUser);
                    }
                }
            }
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        onHiddenChangedClient(hidden);
    }

    public void onHiddenChangedClient(boolean hidden) {
        this.hidden = hidden;
        tryToChangeVisibility(!hidden);
        if (isAdded()) {
            List<Fragment> fragments = getChildFragmentManager().getFragments();
            if (fragments != null) {
                for (Fragment fragment : fragments) {
                    if (fragment instanceof BetterLifecycleFragment) {
                        ((BetterLifecycleFragment) fragment).onHiddenChangedClient(hidden);
                    }
                }
            }
        }
    }

    private void tryToChangeVisibility(boolean tryToShow) {
        // 上次可见
        if (isLastVisible) {
            if (tryToShow) {
                return;
            }
            if (!isFragmentVisible()) {
                onFragmentPause();
                isLastVisible = false;
            }
            // 上次不可见
        } else {
            boolean tryToHide = !tryToShow;
            if (tryToHide) {
                return;
            }
            if (isFragmentVisible()) {
                onFragmentResume(isFirst, isViewDestroyed);
                isLastVisible = true;
                isFirst = false;
            }
        }
    }

    /**
     * Fragment是否可见
     *
     * @return
     */
    public boolean isFragmentVisible() {
        if (isResuming()
                && getUserVisibleHint()
                && !hidden) {
            return true;
        }
        return false;
    }

    /**
     * Fragment 是否在前台。
     *
     * @return
     */
    private boolean isResuming() {
        return isResuming;
    }

    /**
     * Fragment 可见时回调
     *
     * @param isFirst         是否是第一次显示
     * @param isViewDestroyed Fragment中的View是否被回收过。
     *                        存在这种情况：Fragment 的 View 被回收，但是Fragment实例仍在。
     */
    protected void onFragmentResume(boolean isFirst, boolean isViewDestroyed) {
    }
    /**
     * Fragment 不可见时回调
     */
    protected void onFragmentPause() {
    }
}