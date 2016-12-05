
package com.github.mobile.ui.search;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.github.kevinsawicki.wishlist.SingleTypeAdapter;
import com.github.mobile.R;
import com.github.mobile.accounts.AccountUtils;
import com.github.mobile.api.model.User;
import com.github.mobile.api.service.PaginationService;
import com.github.mobile.api.service.SearchService;
import com.github.mobile.core.ResourcePager;
import com.github.mobile.core.user.RefreshUserTask;
import com.github.mobile.ui.PagedItemFragment;
import com.github.mobile.ui.user.UserViewActivity;
import com.github.mobile.util.AvatarLoader;
import com.google.inject.Inject;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import static android.app.SearchManager.QUERY;

/**
 * Fragment to display a list of {@link User} instances
 */
public class SearchUserListFragment extends PagedItemFragment<User> {

    @Inject
    private SearchService service;

    @Inject
    private AvatarLoader avatars;

    private String query;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setEmptyText(R.string.no_people);
    }

    @Override
    protected int getLoadingMessage() {
        return R.string.loading_people;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        query = getStringExtra(QUERY);
    }

    @Override
    public void refresh() {
        query = getStringExtra(QUERY);

        super.refresh();
    }

    @Override
    public void refreshWithProgress() {
        query = getStringExtra(QUERY);

        super.refreshWithProgress();
    }

    @Override
    protected ResourcePager<User> createPager() {
        return new ResourcePager<User>() {

            @Override
            protected Object getId(User resource) {
                return resource.id;
            }

            @Override
            public Iterator<Collection<User>> createIterator(int page, int size) {
                return new PaginationService<User>(page) {
                    @Override
                    public Collection<User> getSinglePage(int page, int itemsPerPage) throws IOException {
                        return service.searchUsers(query, page).execute().body().items;
                    }
                }.getIterator();
            }
        };
    }

    @Override
    protected SingleTypeAdapter<User> createAdapter(List<User> items) {
        return new SearchUserListAdapter(getActivity(),
                items.toArray(new User[items.size()]), avatars);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        final User result = (User) l.getItemAtPosition(position);
        new RefreshUserTask(getActivity(), result.login) {

            @Override
            protected void onSuccess(org.eclipse.egit.github.core.User user) throws Exception {
                super.onSuccess(user);

                if (!AccountUtils.isUser(getActivity(), user))
                    startActivity(UserViewActivity.createIntent(user));
            }
        }.execute();
    }

    @Override
    protected int getErrorMessage(Exception exception) {
        return R.string.error_users_search;
    }
}
