
package com.github.mobile.ui.comment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;

import com.github.mobile.R;
import com.github.mobile.ui.TabPagerActivity;
import com.github.mobile.util.AvatarLoader;
import com.github.mobile.util.TypefaceUtils;
import com.google.inject.Inject;

import org.eclipse.egit.github.core.Comment;

import static com.github.mobile.Intents.EXTRA_COMMENT;

/**
 * Base activity for creating comments
 */
public abstract class CreateCommentActivity extends
        TabPagerActivity<CommentPreviewPagerAdapter> {

    private MenuItem applyItem;

    /**
     * Avatar loader
     */
    @Inject
    protected AvatarLoader avatars;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        configureTabPager();
    }

    @Override
    public void invalidateOptionsMenu() {
        super.invalidateOptionsMenu();

        if (applyItem != null)
            applyItem.setEnabled(adapter != null
                    && !TextUtils.isEmpty(adapter.getCommentText()));
    }

    @Override
    protected void setCurrentItem(int position) {
        super.setCurrentItem(position);

        adapter.setCurrentItem(position);
    }

    /**
     * Create comment
     *
     * @param comment
     */
    protected abstract void createComment(String comment);

    /**
     * Finish this activity passing back the created comment
     *
     * @param comment
     */
    protected void finish(Comment comment) {
        Intent data = new Intent();
        data.putExtra(EXTRA_COMMENT, comment);
        setResult(RESULT_OK, data);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.m_apply:
            if (adapter != null && !TextUtils.isEmpty(adapter.getCommentText())) {
                createComment(adapter.getCommentText());
            }
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected String getIcon(final int position) {
        switch (position) {
        case 0:
            return TypefaceUtils.ICON_PENCIL;
        case 1:
            return TypefaceUtils.ICON_EYE;
        default:
            return super.getIcon(position);
        }
    }

    @Override
    protected CommentPreviewPagerAdapter createAdapter() {
        return new CommentPreviewPagerAdapter(this, null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu options) {
        getMenuInflater().inflate(R.menu.comment, options);
        applyItem = options.findItem(R.id.m_apply);
        return true;
    }
}
