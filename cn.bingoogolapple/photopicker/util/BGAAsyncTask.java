package cn.bingoogolapple.photopicker.util;

import android.os.AsyncTask;

/* loaded from: classes.dex */
public abstract class BGAAsyncTask<Params, Result> extends AsyncTask<Params, Void, Result> {
    private Callback<Result> mCallback;

    public interface Callback<Result> {
        void onPostExecute(Result result);

        void onTaskCancelled();
    }

    public BGAAsyncTask(Callback<Result> callback) {
        this.mCallback = callback;
    }

    public void cancelTask() {
        if (getStatus() != AsyncTask.Status.FINISHED) {
            cancel(true);
        }
    }

    @Override // android.os.AsyncTask
    protected void onPostExecute(Result result) {
        super.onPostExecute(result);
        Callback<Result> callback = this.mCallback;
        if (callback != null) {
            callback.onPostExecute(result);
        }
    }

    @Override // android.os.AsyncTask
    protected void onCancelled() {
        super.onCancelled();
        Callback<Result> callback = this.mCallback;
        if (callback != null) {
            callback.onTaskCancelled();
        }
        this.mCallback = null;
    }
}