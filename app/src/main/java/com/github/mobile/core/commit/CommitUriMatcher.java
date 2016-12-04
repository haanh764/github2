
package com.github.mobile.core.commit;

import android.content.Intent;
import android.text.TextUtils;

import com.github.mobile.core.repo.RepositoryUriMatcher;
import com.github.mobile.ui.commit.CommitCompareViewActivity;
import com.github.mobile.ui.commit.CommitViewActivity;

import org.eclipse.egit.github.core.Repository;

import java.util.List;

/**
 * Parses commits from a path
 */
public class CommitUriMatcher {

    /**
     * Get an intent for an exact commit match
     *
     * @param pathSegments
     * @return {@link Intent} or null if path is not valid
     */
    public static Intent getCommitIntent(List<String> pathSegments) {
        if (pathSegments.size() != 4)
            return null;

        Repository repo = RepositoryUriMatcher.getRepository(pathSegments);
        if (repo == null)
            return null;

        switch (pathSegments.get(2)) {
        case "commit":
            return getSingleCommitIntent(repo, pathSegments);
        case "compare":
            return getCommitCompareIntent(repo, pathSegments);
        default:
            return null;
        }
    }

    private static Intent getSingleCommitIntent(Repository repo, List<String> pathSegments) {
        String ref = pathSegments.get(3);
        if (TextUtils.isEmpty(ref))
            return null;

        return CommitViewActivity.createIntent(repo, ref);
    }

    private static Intent getCommitCompareIntent(Repository repo, List<String> pathSegments) {
        String path = pathSegments.get(3);
        if (TextUtils.isEmpty(path))
            return null;

        String[] refs = path.split("\\.\\.\\.");

        switch (refs.length) {
        case 1:
            return CommitCompareViewActivity.createIntent(repo, refs[0]);
        case 2:
            return CommitCompareViewActivity.createIntent(repo, refs[0], refs[1]);
        }

        return null;
    }
}
