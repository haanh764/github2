
package com.github.mobile.ui.user;

import android.os.Bundle;
import android.support.v4.content.Loader;
import android.view.View;
import android.widget.ListView;

import com.github.kevinsawicki.wishlist.SingleTypeAdapter;
import com.github.mobile.R;
import com.github.mobile.ThrowableLoader;
import com.github.mobile.accounts.AccountUtils;
import com.github.mobile.ui.ItemListFragment;
import com.github.mobile.util.AvatarLoader;
import com.google.inject.Inject;

import org.eclipse.egit.github.core.User;
import org.eclipse.egit.github.core.service.OrganizationService;

import java.util.List;

import static com.github.mobile.Intents.EXTRA_USER;

/**
 * Fragment to display the members of an org.
 */
public class OrgMembersFragment extends ItemListFragment<User> implements
        OrganizationSelectionListener {

    private User org;

    @Inject
    private OrganizationService service;

    @Inject
    private AvatarLoader avatars;

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (org != null)
            outState.putSerializable(EXTRA_USER, org);
    }

    @Override
    public void onDetach() {
        OrganizationSelectionProvider selectionProvider = (OrganizationSelectionProvider) getActivity();
        if (selectionProvider != null)
            selectionProvider.removeListener(this);

        super.onDetach();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        org = ((OrganizationSelectionProvider) getActivity()).addListener(this);
        if (org == null && savedInstanceState != null)
            org = (User) savedInstanceState.getSerializable(EXTRA_USER);
        setEmptyText(R.string.no_members);

        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<List<User>> onCreateLoader(int id, Bundle args) {
        return new ThrowableLoader<List<User>>(getActivity(), items) {

            @Override
            public List<User> loadData() throws Exception {
                return service.getMembers(org.getLogin());
            }
        };
    }

    @Override
    protected SingleTypeAdapter<User> createAdapter(List<User> items) {
        User[] users = items.toArray(new User[items.size()]);
        return new UserListAdapter(getActivity().getLayoutInflater(), users,
                avatars);
    }

    @Override
    public void onOrganizationSelected(User organization) {
        int previousOrgId = org != null ? org.getId() : -1;
        org = organization;
        // Only hard refresh if view already created and org is changing
        if (previousOrgId != org.getId())
            refreshWithProgress();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        User user = (User) l.getItemAtPosition(position);
        if (!AccountUtils.isUser(getActivity(), user))
            startActivity(UserViewActivity.createIntent(user));
    }

    @Override
    protected int getErrorMessage(Exception exception) {
        return R.string.error_members_load;
    }
}
