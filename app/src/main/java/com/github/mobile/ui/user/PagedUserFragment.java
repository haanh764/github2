
package com.github.mobile.ui.user;

import android.view.View;
import android.widget.ListView;

import com.github.kevinsawicki.wishlist.SingleTypeAdapter;
import com.github.mobile.accounts.AccountUtils;
import com.github.mobile.ui.PagedItemFragment;
import com.github.mobile.util.AvatarLoader;
import com.google.inject.Inject;

import org.eclipse.egit.github.core.User;
import org.eclipse.egit.github.core.service.UserService;

import java.util.List;

/**
 * Fragment to page over users
 */
public abstract class PagedUserFragment extends PagedItemFragment<User> {

    /**
     * Avatar loader
     */
    @Inject
    protected AvatarLoader avatars;

    /**
     * User service
     */
    @Inject
    protected UserService service;

    @Override
    protected SingleTypeAdapter<User> createAdapter(List<User> items) {
        User[] users = items.toArray(new User[items.size()]);
        return new UserListAdapter(getActivity().getLayoutInflater(), users,
                avatars);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        User user = (User) l.getItemAtPosition(position);
        if (!AccountUtils.isUser(getActivity(), user))
            startActivity(UserViewActivity.createIntent(user));
    }
}
