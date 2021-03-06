
package com.github.mobile.ui.ref;

import android.accounts.Account;
import android.util.Log;

import com.github.mobile.R;
import com.github.mobile.core.ref.RefUtils;
import com.github.mobile.ui.DialogFragmentActivity;
import com.github.mobile.ui.ProgressDialogTask;
import com.github.mobile.util.ToastUtils;

import org.eclipse.egit.github.core.IRepositoryIdProvider;
import org.eclipse.egit.github.core.Reference;
import org.eclipse.egit.github.core.service.DataService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Dialog to select a branch or tag
 */
public class RefDialog {

    private static final String TAG = "RefDialog";

    private final DataService service;

    private Map<String, Reference> refs;

    private final int requestCode;

    private final DialogFragmentActivity activity;

    private final IRepositoryIdProvider repository;

    /**
     * Create dialog helper to display refs
     *
     * @param activity
     * @param requestCode
     * @param repository
     * @param service
     */
    public RefDialog(final DialogFragmentActivity activity,
            final int requestCode, final IRepositoryIdProvider repository,
            final DataService service) {
        this.activity = activity;
        this.requestCode = requestCode;
        this.repository = repository;
        this.service = service;
    }

    private void load(final Reference selectedRef) {
        new ProgressDialogTask<List<Reference>>(activity) {

            @Override
            public List<Reference> run(Account account) throws Exception {
                List<Reference> allRefs = service.getReferences(repository);
                Map<String, Reference> loadedRefs = new TreeMap<String, Reference>();
                for (Reference ref : allRefs)
                    if (RefUtils.isValid(ref))
                        loadedRefs.put(ref.getRef(), ref);
                refs = loadedRefs;
                return allRefs;
            }

            @Override
            protected void onSuccess(List<Reference> all) throws Exception {
                super.onSuccess(all);

                show(selectedRef);
            }

            @Override
            protected void onException(Exception e) throws RuntimeException {
                super.onException(e);

                Log.d(TAG, "Exception loading references", e);
                ToastUtils.show(activity, e, R.string.error_refs_load);
            }

            @Override
            public void execute() {
                showIndeterminate(R.string.loading_refs);

                super.execute();
            }
        }.execute();
    }

    /**
     * Show dialog with given reference selected
     *
     * @param selectedRef
     */
    public void show(Reference selectedRef) {
        if (refs == null || refs.isEmpty()) {
            load(selectedRef);
            return;
        }

        final ArrayList<Reference> refList = new ArrayList<Reference>(
                refs.values());
        int checked = -1;
        if (selectedRef != null) {
            String ref = selectedRef.getRef();
            for (int i = 0; i < refList.size(); i++) {
                String candidate = refList.get(i).getRef();
                if (ref.equals(candidate)) {
                    checked = i;
                    break;
                } else if (ref.equals(RefUtils.getName(candidate))) {
                    checked = i;
                    break;
                }
            }
        }

        RefDialogFragment.show(activity, requestCode,
                activity.getString(R.string.select_ref), null, refList, checked);
    }
}
